package me.itsmas.network.bungee;

import me.itsmas.network.bungee.listener.PingListener;
import me.itsmas.network.bungee.listener.ServerListener;
import net.md_5.bungee.api.plugin.Plugin;

public class Core extends Plugin
{
    @Override
    public void onEnable()
    {
        new ServerListener(this);
        new PingListener(this);
    }
}
