package me.itsmas.network.server.network;

import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.itsmas.network.server.module.SubModule;
import me.itsmas.network.server.util.UtilServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles BungeeCord messaging messages on the network
 */
public class BungeeListener extends SubModule<Network> implements PluginMessageListener
{
    BungeeListener(Network module)
    {
        super(module);

        initBungeeMessages();
    }

    private void initBungeeMessages()
    {
        registerChannels();

        final AtomicBoolean sentServerRequest = new AtomicBoolean(false);

        UtilServer.runRepeating(() ->
        {
            if (module.getServerName() == null && !sentServerRequest.get())
            {
                if (UtilServer.writeBungee("GetServer"))
                {
                    sentServerRequest.set(true);
                }
            }

            UtilServer.writeBungee("GetServers");
        }, 0L, 20L);
    }

    private void registerChannels()
    {
        UtilServer.registerOutgoing("BungeeCord");

        UtilServer.registerIncoming(this, "BungeeCord");
        UtilServer.registerIncoming(this, "ServerUpdates");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player unused, byte[] data)
    {
        ByteArrayDataInput in = ByteStreams.newDataInput(data);

        if (channel.equals("BungeeCord"))
        {
            switch (in.readUTF())
            {
                case "GetServer":
                {
                    String serverName = in.readUTF();

                    log("Received server name: " + serverName);

                    module.setServerName(serverName);

                    if (module instanceof RedisNetwork)
                    {
                        ((RedisNetwork) module).resetQueue();
                    }

                    break;
                }

                case "GetServers":
                {
                    Set<String> servers = Sets.newHashSet(in.readUTF().split(", "));
                    module.setServers(Collections.unmodifiableSet(servers));

                    break;
                }
            }
        }
        else if (channel.equals("ServerUpdates"))
        {
            switch (in.readUTF())
            {
                case "ProxyLeave":
                {
                    String player = in.readUTF();

                    module.removeTrackingData(player);
                }
            }
        }
    }
}
