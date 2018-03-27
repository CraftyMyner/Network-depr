package me.itsmas.network.server.user;

import me.itsmas.network.server.module.SubModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

class UserListener extends SubModule<UserManager>
{
    UserListener(UserManager module)
    {
        super(module);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event)
    {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
        {
            return;
        }

        UUID uuid = event.getUniqueId();

        User user = core.getDatabase().getUser(event.getUniqueId());

        if (user == null)
        {
            user = new User(event.getName(), uuid);

            core.getDatabase().saveUser(user);
        }

        module.insertUser(user);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        User user = module.getUser(player);
        user.initJoin(core, player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        User user = module.getUser(player);

        if (!module.recentlySaved(user))
        {
            // User has left the proxy
            user.save(true);
        }

        module.removeUser(player);
    }
}
