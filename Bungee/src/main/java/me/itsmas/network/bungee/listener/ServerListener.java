package me.itsmas.network.bungee.listener;

import me.itsmas.network.bungee.Core;
import me.itsmas.network.bungee.util.UtilServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerListener implements Listener
{
    private final Core core;

    public ServerListener(Core core)
    {
        this.core = core;

        UtilServer.registerListener(this);
    }

    @EventHandler
    public void onProxyLeave(PlayerDisconnectEvent event)
    {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo info = player.getServer().getInfo();

        UtilServer.sendData(info, "ServerUpdates", "ProxyLeave", player.getName());
    }
}
