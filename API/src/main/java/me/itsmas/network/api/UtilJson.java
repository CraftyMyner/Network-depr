package me.itsmas.network.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Json utility methods
 */
public final class UtilJson
{
    /**
     * Private constructor; no instances of this class are needed
     */
    private UtilJson()
    {
        UtilException.throwNew();
    }

    /**
     * The {@link JsonParser} instance
     */
    private static final JsonParser PARSER = new JsonParser();

    /**
     * Parses a {@link JsonObject} from a {@link String} input
     *
     * @param input The input string
     * @return The json object or null if unable to parse
     */
    public static JsonObject parse(String input)
    {
        try
        {
            return PARSER.parse(input).getAsJsonObject();
        }
        catch (JsonParseException ex)
        {
            return null;
        }
    }
}
