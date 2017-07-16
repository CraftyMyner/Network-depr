package me.itsmas.network.server.command;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.command.annotations.Command;
import me.itsmas.network.server.command.annotations.Optional;
import me.itsmas.network.server.command.annotations.RequiredRank;
import me.itsmas.network.server.command.annotations.SpecificRanks;
import me.itsmas.network.server.command.annotations.Usage;
import me.itsmas.network.server.command.context.ContextResolver;
import me.itsmas.network.server.command.context.IntResolver;
import me.itsmas.network.server.command.context.MaterialResolver;
import me.itsmas.network.server.command.context.RankResolver;
import me.itsmas.network.server.command.context.UserResolver;
import me.itsmas.network.server.module.Module;
import me.itsmas.network.server.rank.Rank;
import me.itsmas.network.server.user.User;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles the execution of commands on the server
 */
public class CommandManager extends Module
{
    public CommandManager(Core core)
    {
        super(core, "Commands");

        registerContextHandlers();
    }

    /**
     * Map linking classes to their context resolvers
     */
    private final Map<Class<?>, ContextResolver<?>> contextHandlers = new HashMap<>();

    /**
     * Registers command context resolvers
     */
    private void registerContextHandlers()
    {
        registerContext(int.class, new IntResolver());
        registerContext(Integer.class, new IntResolver());

        registerContext(Rank.class, new RankResolver());
        registerContext(Material.class, new MaterialResolver());
        registerContext(User.class, new UserResolver(core));
    }

    /**
     * Registers a {@link ContextResolver}
     *
     * @param clazz The class that this resolver will resolve
     * @param context The ContextResolver object
     */
    private void registerContext(Class<?> clazz, ContextResolver context)
    {
        contextHandlers.put(clazz, context);
    }

    /**
     * The registered commands
     */
    private final Set<CommandData> commands = new HashSet<>();

    /**
     * Registers the commands in a class
     *
     * @param object The class instance containing commands
     */
    public void registerCommands(Object object)
    {
        for (Method method : object.getClass().getMethods())
        {
            handleMethod(object, method);
        }
    }

    /**
     * Handles the registering of a command via its method
     *
     * @param object The object containing the method
     * @param method The method to handle
     */
    private void handleMethod(Object object, Method method)
    {
        if (method.isAnnotationPresent(Command.class))
        {
            if (method.getParameterCount() < 1)
            {
                // No parameters, invalid command
                return;
            }

            int paramIndex = -1;

            int minArgs = 0;
            int maxArgs = 0;

            boolean optional = false;

            for (Parameter parameter : method.getParameters())
            {
                // There's already been an optional argument
                if (optional)
                {
                    return;
                }

                paramIndex++;

                Class<?> paramClass = parameter.getType();

                if (paramIndex == 0)
                {
                    if (paramClass != User.class)
                    {
                        // First parameter is not a User, invalid command
                        return;
                    }

                    continue;
                }

                if (!isSupportedParameter(paramClass))
                {
                    // Parameter with no context found or not String
                    return;
                }

                if (parameter.isAnnotationPresent(Optional.class))
                {
                    optional = true;
                }
                else
                {
                    minArgs++;
                }

                maxArgs++;
            }

            if (method.getParameterCount() == 1)
            {
                maxArgs = Integer.MAX_VALUE;
            }

            String[] aliases = method.getDeclaredAnnotation(Command.class).value().split("\\|");
            String usage = getUsage(method);

            Rank requiredRank = getRequiredRank(method);
            Rank[] specificRanks = getSpecificRanks(method);

            CommandData data = new CommandData(object, method, aliases, usage, requiredRank, specificRanks, minArgs, maxArgs);
            commands.add(data);
        }
    }

    @EventHandler
    public void onPlayerCommandProcess(PlayerCommandPreprocessEvent event)
    {
        User user = core.getUserManager().getUser(event.getPlayer());

        List<String> args = new ArrayList<>(Arrays.asList(event.getMessage().split(" ")));
        String alias = args.remove(0).substring(1);

        for (CommandData cmd : commands)
        {
            if (ArrayUtils.contains(cmd.getAliases(), alias))
            {
                event.setCancelled(true);

                if (!canUseCommand(user, cmd))
                {
                    return;
                }

                if (args.size() < cmd.getMinArgs() || args.size() > cmd.getMaxArgs())
                {
                    user.sendMessage("command;usage", alias, cmd.getUsage());
                    return;
                }

                if (user.isCoolingDown("Command", 1000, false))
                {
                    user.sendMessage("command;cooldown");
                    return;
                }

                executeCommand(user, args, cmd, event.getMessage());
                return;
            }
        }
    }

    /**
     * Attempts to execute a command
     *
     * @param user The user executing the command
     * @param args The arguments the user has provided
     * @param cmd The command the user will execute
     */
    private void executeCommand(User user, List<String> args, CommandData cmd, String rawMsg)
    {
        List<Object> params = new ArrayList<>();

        params.add(user);

        Method method = cmd.getMethod();

        for (int i = 0; i < method.getParameterCount() - 1; i++)
        {
            Parameter parameter = method.getParameters()[i + 1];

            if (i >= args.size())
            {
                continue;
            }

            String argument = args.get(i);

            // No need to resolve strings
            if (parameter.getType() == String.class)
            {
                params.add(argument);
                continue;
            }

            ContextResolver<?> context = contextHandlers.get(parameter.getType());

            if (context == null)
            {
                // Should never happen
                return;
            }

            Object object = context.resolve(argument);

            if (object == null)
            {
                context.sendError(user, argument);
                return;
            }

            params.add(object);
        }

        while (params.size() < method.getParameterCount())
        {
            params.add(null);
        }

        try
        {
            method.invoke(cmd.getObject(), params.toArray(new Object[0]));

            user.addLog("Executed command: " + rawMsg);
        }
        catch (IllegalAccessException | InvocationTargetException ex)
        {
            logFatal("Error executing command for player " + user.getName() + ": " + rawMsg);
            ex.printStackTrace();
        }
    }

    /**
     * Determines whether a {@link User} can use a command
     *
     * @param user The user
     * @param cmd The command
     * @return Whether the user can use the command
     */
    private boolean canUseCommand(User user, CommandData cmd)
    {
        return
                cmd.getRequiredRank() == Rank.DEFAULT ||
                (cmd.getSpecificRanks() != null && user.hasRankSpecific(cmd.getSpecificRanks())) ||
                user.hasRank(cmd.getRequiredRank());
    }

    /**
     * Fetches the usage of a command from its method
     *
     * @param method The command method
     * @return The command's usage
     */
    private String getUsage(Method method)
    {
        String usage = null;

        if (method.isAnnotationPresent(Usage.class))
        {
            usage = method.getAnnotation(Usage.class).value();
        }

        return usage;
    }

    /**
     * Gets the required rank of a command from its method
     *
     * @param method The command method
     * @return The command's required rank
     */
    private Rank getRequiredRank(Method method)
    {
        Rank requiredRank = Rank.DEFAULT;

        if (method.isAnnotationPresent(RequiredRank.class))
        {
            requiredRank = method.getAnnotation(RequiredRank.class).value();
        }

        return requiredRank;
    }

    /**
     * Gets the specific ranks of a command from its method
     *
     * @param method The command method
     * @return The command's specific ranks
     */
    private Rank[] getSpecificRanks(Method method)
    {
        Rank[] specificRanks = null;

        if (method.isAnnotationPresent(SpecificRanks.class))
        {
            specificRanks = method.getAnnotation(SpecificRanks.class).value();
        }

        return specificRanks;
    }

    /**
     * Gets whether a class is a supported parameter type
     *
     * @param clazz The class
     * @return Whether the class is a supported parameter
     */
    private boolean isSupportedParameter(Class<?> clazz)
    {
        return contextHandlers.keySet().contains(clazz) || clazz == String.class;
    }
}
