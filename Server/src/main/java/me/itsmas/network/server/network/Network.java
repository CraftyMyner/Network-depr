package me.itsmas.network.server.network;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.command.annotations.Command;
import me.itsmas.network.server.command.annotations.Optional;
import me.itsmas.network.server.module.Module;
import me.itsmas.network.server.user.User;
import me.itsmas.network.server.util.UtilServer;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Network extends Module
{
    Network(Core core, String name)
    {
        super(core, name);
    }

    @Override
    public void onEnable()
    {
        addSubModule(new BungeeListener(this));
        addSubModule(new LoginListener(this));

        registerCommands(this);
    }

    /**
     * The name of the server on the BungeeCord network
     */
    private String serverName;

    /**
     * Assigns the name of the server
     *
     * @see #serverName
     * @param serverName The server's name
     */
    final void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    /**
     * Fetches the name of the server on the BungeeCord network
     *
     * @see #serverName
     * @return The server's name
     */
    public String getServerName()
    {
        return serverName;
    }

    /**
     * The servers on the BungeeCord network
     */
    private Set<String> servers = new HashSet<>();

    /**
     * Gets whether a server exists on the network
     *
     * @param name The name of the server to test
     * @return Whether a server with the given name exists
     */
    public boolean serverExists(String name)
    {
        for (String server : servers)
        {
            if (server.equalsIgnoreCase(name))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * The delay in ticks before executing a server switch
     */
    private final long serverSwitchDelay = 10L;

    /**
     * The cooldown in milliseconds before a player can switch servers after joining
     */
    private final long serverSwitchCooldown = 2500;

    /**
     * The name of the server switch cooldown
     */
    public static final String SWITCH_COOLDOWN = "Switch Server";

    /**
     * Switches the server a {@link User} is on through BungeeCord
     *
     * @param user The user to switch
     * @param server The server to send the user to
     */
    public void switchServer(User user, String server)
    {
        if (server.equals(getServerName()))
        {
            user.sendMessage("server;already_current");
            return;
        }

        if (user.isCoolingDown(SWITCH_COOLDOWN, serverSwitchCooldown, false))
        {
            user.sendMessage("server;switch_cooldown");
            return;
        }

        if (!serverExists(server))
        {
            user.sendMessage("server;invalid_server");
            return;
        }

        user.save(false);

        user.sendMessage("server;sending", server);

        UtilServer.runDelayed(() -> UtilServer.writeBungee("ConnectOther", user.getName(), server), serverSwitchDelay);
    }

    /**
     * Assigns the servers on the network
     *
     * @see #servers
     * @param servers The servers on the network
     */
    final void setServers(Set<String> servers)
    {
        this.servers = servers;
    }

    /**
     * Updates a player's server location on the network
     *
     * @param player The name of the player switching server
     */
    abstract void updateServer(String player);

    /**
     * Removes a player's server location on the network from storage
     *
     * @param player The player leaving the network
     */
    abstract void removeTrackingData(String player);

    /**
     * Fetches the server a player is on
     *
     * @param player The name of the player
     * @param consumer Consumer containing server object
     */
    public abstract void getServer(String player, Consumer<String> consumer);

    @Command(value = "server")
    public void onServerCommand(User user, @Optional String server)
    {
        if (server == null)
        {
            user.sendMessage("server;current", getServerName());
            return;
        }

        switchServer(user, server);
    }
}
