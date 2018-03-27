package me.itsmas.network.bungee.listener;

import me.itsmas.network.api.API;
import me.itsmas.network.bungee.Core;
import me.itsmas.network.bungee.util.UtilServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener
{
    private final Core core;

    public PingListener(Core core)
    {
        this.core = core;

        UtilServer.registerListener(this);
    }

    private static final String PROTOCOL_NAME = "Network v" + API.VERSION;

    @EventHandler
    public void onPing(ProxyPingEvent event)
    {
        ServerPing ping = event.getResponse();

        ping.getVersion().setName(PROTOCOL_NAME);
    }
}
