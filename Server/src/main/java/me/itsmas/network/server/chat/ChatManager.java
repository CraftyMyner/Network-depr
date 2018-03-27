package me.itsmas.network.server.chat;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.chat.handlers.CapsBlocker;
import me.itsmas.network.server.chat.handlers.ChatFilter;
import me.itsmas.network.server.module.Module;
import me.itsmas.network.server.rank.Rank;
import me.itsmas.network.server.user.User;
import me.itsmas.network.server.util.C;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles chat messages on the server
 */
public class ChatManager extends Module
{
    public ChatManager(Core core)
    {
        super(core, "Chat");
    }

    @Override
    public void onEnable()
    {
        handlers = new HashSet<>();

        addChatHandler((user, msg) ->
        {
            if (user.isCoolingDown("Chat", 1000, false))
            {
                user.sendMessage("chat.cooldown");
                return false;
            }

            return true;
        });

        addChatHandler(new CapsBlocker());
        addChatHandler(new ChatFilter(core));
    }

    /**
     * Set of {@link ChatHandler} instances
     */
    private Set<ChatHandler> handlers;

    /**
     * Adds a new {@link ChatHandler}
     *
     * @param handler The chat handler
     */
    public void addChatHandler(ChatHandler handler)
    {
        handlers.add(handler);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        User user = core.getUserManager().getUser(event.getPlayer());
        String msg = event.getMessage();

        for (ChatHandler handler : handlers)
        {
            if (!handler.handleChat(user, msg))
            {
                event.setCancelled(true);
            }
        }

        event.setFormat(formatChat(user));

        user.addLog("Sent chat: %s", msg);
    }

    /**
     * Formats a chat message for a {@link User}
     *
     * @param user The user
     * @return The formatted message
     */
    private String formatChat(User user)
    {
        Rank rank = user.getRank();

        String prefix = rank.getColour().toString();

        if (rank != Rank.DEFAULT)
        {
            prefix += "[" + user.getFormattedRank() + "] ";
        }

        return prefix + "%s: " + C.WHITE + "%s";
    }
}
