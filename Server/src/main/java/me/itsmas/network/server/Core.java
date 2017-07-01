package me.itsmas.network.server;

import me.itsmas.network.server.command.CommandManager;
import me.itsmas.network.server.command.chat.ChatManager;
import me.itsmas.network.server.database.Database;
import me.itsmas.network.server.database.MongoDB;
import me.itsmas.network.server.lang.Lang;
import me.itsmas.network.server.module.ModuleManager;
import me.itsmas.network.server.network.Network;
import me.itsmas.network.server.network.RedisNetwork;
import me.itsmas.network.server.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin
{
    private ModuleManager moduleManager;
    private Database database;
    private CommandManager commandManager;
    private Network network;
    private UserManager userManager;
    private Lang lang;
    private ChatManager chatManager;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        moduleManager = new ModuleManager();
        database = new MongoDB(this);
        commandManager = new CommandManager(this);
        network = new RedisNetwork(this);
        userManager = new UserManager(this);
        lang = new Lang(this);
        chatManager = new ChatManager(this);
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

    public Network getNetwork()
    {
        return network;
    }

    public UserManager getUserManager()
    {
        return userManager;
    }

    public Lang getLang()
    {
        return lang;
    }

    public ChatManager getChatManager()
    {
        return chatManager;
    }
}
