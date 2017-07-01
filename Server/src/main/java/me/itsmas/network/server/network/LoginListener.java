package me.itsmas.network.server.network;

import me.itsmas.network.server.module.SubModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginListener extends SubModule<Network>
{
    LoginListener(Network module)
    {
        super(module);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event)
    {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED)
        {
            module.updateServer(event.getPlayer().getName());
        }
    }
}
