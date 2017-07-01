package me.itsmas.network.bungee.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.itsmas.network.bungee.Core;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;

import java.util.Arrays;

/**
 * Server utility methods
 */
public final class UtilServer
{
    /**
     * Private constructor; no instances of this class are needed
     */
    private UtilServer()
    {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * The plugin instance
     */
    private static final Core plugin = (Core) ProxyServer.getInstance().getPluginManager().getPlugin("Core");

    /**
     * Registers a {@link Listener}
     *
     * @param listener The listener to register
     */
    public static void registerListener(Listener listener)
    {
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, listener);
    }

    /**
     * Sends data to a server using the messaging channel
     *
     * @param server The server to send the data to
     * @param channel The channel to send the data through
     * @param data The data to send to the server
     */
    public static void sendData(ServerInfo server, String channel, String... data)
    {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        Arrays.stream(data).forEach(output::writeUTF);

        server.sendData("Return", output.toByteArray());
    }
}
