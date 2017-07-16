package me.itsmas.network.server.chat.handlers;

import me.itsmas.network.api.UtilString;
import me.itsmas.network.server.chat.ChatHandler;
import me.itsmas.network.server.user.User;

/**
 * Prevents chat messages with large amounts of caps from being sent
 */
public class CapsBlocker implements ChatHandler
{
    /**
     * The maximum percentage of a message that can be caps
     */
    private final double CAPS_PERCENT_BLOCK = 50.0D;

    @Override
    public boolean handleChat(User user, String msg)
    {
        if (UtilString.percentCaps(msg) > CAPS_PERCENT_BLOCK)
        {
            user.sendMessage("chat;caps");
            return false;
        }

        return true;
    }
}
