package me.itsmas.network.server.command.context;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UserResolver implements ContextResolver<User>
{
    private final Core core;

    public UserResolver(Core core)
    {
        this.core = core;
    }

    @Override
    public User resolve(String input)
    {
        Player player = Bukkit.getPlayer(input);

        if (player == null)
        {
            return null;
        }

        return core.getUserManager().getUser(player);
    }

    @Override
    public void sendError(User user, String input)
    {
        user.sendMessage("command;user_offline");
    }
}
