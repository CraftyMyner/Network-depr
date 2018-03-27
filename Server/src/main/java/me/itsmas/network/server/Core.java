package me.itsmas.network.server;

import me.itsmas.network.server.chat.ChatManager;
import me.itsmas.network.server.command.CommandManager;
import me.itsmas.network.server.database.Database;
import me.itsmas.network.server.database.MongoDB;
import me.itsmas.network.server.lang.Lang;
import me.itsmas.network.server.module.ModuleManager;
import me.itsmas.network.server.network.Network;
import me.itsmas.network.server.network.redis.RedisNetwork;
import me.itsmas.network.server.task.TaskManager;
import me.itsmas.network.server.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the Core plugin
 */
public class Core extends JavaPlugin
{
    private ModuleManager moduleManager;
    private Database database;
    private CommandManager commandManager;
    private ChatManager chatManager;
    private Network network;
    private UserManager userManager;
    private TaskManager taskManager;
    private Lang lang;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        moduleManager = new ModuleManager();
        database = new MongoDB(this);
        commandManager = new CommandManager(this);
        chatManager = new ChatManager(this);
        network = new RedisNetwork(this);
        userManager = new UserManager(this);
        taskManager = new TaskManager(this);
        lang = new Lang(this);
    }

    @Override
    public void onDisable()
    {
        getModuleManager().onDisable();
    }

    /**
     * Fetches a value from the config
     *
     * @param path The path to the config value
     * @return The value at the given config path
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfig(String path)
    {
        return (T) getConfig().get(path);
    }

    public ModuleManager getModuleManager()
    {
        return moduleManager;
    }

    public Database getDatabase()
    {
        return database;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    public ChatManager getChatManager()
    {
        return chatManager;
    }

    public Network getNetwork()
    {
        return network;
    }

    public UserManager getUserManager()
    {
        return userManager;
    }

    public TaskManager getTaskManager()
    {
        return taskManager;
    }

    public Lang getLang()
    {
        return lang;
    }
}
