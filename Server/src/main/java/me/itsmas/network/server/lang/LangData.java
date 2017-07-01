package me.itsmas.network.server.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Class holding data for messages in a locale
 */
class LangData
{
    LangData(){}

    /**
     * Map of message IDs to messages
     */
    private Map<String, String> messages = new HashMap<>();

    /**
     * Adds a message to this {@link LangData} object
     *
     * @param id The ID of the message to add
     * @param msg The content of the message
     */
    void addMessage(String id, String msg)
    {
        messages.put(id, msg);
    }

    /**
     * Fetches a message from the message map
     *
     * @see #messages
     * @param id The ID of the message to fetch
     * @return The message
     */
    String getMessage(String id)
    {
        return messages.get(id);
    }
}
