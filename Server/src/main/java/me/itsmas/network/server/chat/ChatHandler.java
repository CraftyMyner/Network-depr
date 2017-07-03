package me.itsmas.network.server.chat;

import me.itsmas.network.server.user.User;

public interface ChatHandler
{
    /**
     * Handles a chat message
     *
     * @param user The user chatting
     * @param msg The message to user sent
     * @return Whether to allow the message to be sent
     */
    boolean handleChat(User user, String msg);
}
