package me.itsmas.network.server.chat.handlers;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.chat.ChatHandler;
import me.itsmas.network.server.user.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Blocks innapropriate chat messages
 */
public class ChatFilter implements ChatHandler
{
    public ChatFilter(Core core)
    {
        List<String> blocked = core.getConfig("chat.filter");

        blockedWords = blocked.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    /**
     * Blocked words
     */
    private final Set<String> blockedWords;

    @Override
    public boolean handleChat(User user, String msg)
    {
        msg = normalizeMessage(msg);

        for (String blocked : blockedWords)
        {
            if (msg.contains(blocked))
            {
                user.sendMessage("chat;filter");

                user.addLog("Tried to send chat: " + msg);
                return false;
            }
        }

        return true;
    }

    /**
     * Normalizes a chat message
     *
     * @param msg The message
     * @return The normalized message
     */
    private String normalizeMessage(String msg)
    {
        return removeNonLetters(
                replaceNumbers(
                        msg
                )
        ).toLowerCase().trim();
    }

    /**
     * Replaces numbers and symbols with their relevant letters in a message
     *
     * @param msg The message
     * @return The modified message
     */
    private String replaceNumbers(String msg)
    {
        return msg.
                replace("1", "i").
                replace("3", "e").
                replace("4", "a").
                replace("5", "s").
                replace("7", "t").
                replace("0", "o").
                replace("@", "a").
                replace("\\$", "s");
    }

    /**
     * Removes non-letter characters from a message
     *
     * @param msg The message
     * @return The modified message
     */
    private String removeNonLetters(String msg)
    {
        char[] chars = msg.toCharArray();
        StringBuilder builder = new StringBuilder();

        for (char c : chars)
        {
            if (Character.isLetter(c))
            {
                builder.append(c);
            }
        }

        return builder.toString().trim();
    }
}
