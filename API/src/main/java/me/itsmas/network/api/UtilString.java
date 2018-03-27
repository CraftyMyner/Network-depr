package me.itsmas.network.api;

import java.util.StringJoiner;

/**
 * String utility methods
 */
public final class UtilString
{
    /**
     * Private constructor; no instances of this class are needed
     */
    private UtilString()
    {
        UtilException.throwNew();
    }

    /**
     * Minecraft's colour code character (§)
     */
    private static final char COLOUR_CHAR = '\u00A7';

    /**
     * The friendly character used for message formatting
     */
    private static final char FORMAT_CHAR = '&';

    /**
     * All colour and formatting codes
     */
    private static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";

    /**
     * Colours and formats a message using the formatting char
     *
     * @see #FORMAT_CHAR
     * @param input The message to colour
     * @return The coloured message
     */
    public static String colour(String input)
    {
        char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length - 1; i++)
        {
            if (chars[i] == FORMAT_CHAR && ALL_CODES.indexOf(chars[i + 1]) > -1)
            {
                chars[i] = COLOUR_CHAR;
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }

        return new String(chars);
    }

    /**
     * Returns the singular or plural form of a noun depending on the amount
     *
     * @param amount The amount of the noun
     * @param singular The singular form of the noun
     * @param plural The plural form of the noun
     * @return The correctly formed noun
     */
    public static String plural(int amount, String singular, String plural)
    {
        return amount == 1 ? singular : plural;
    }

    /**
     * Combines an array of objects to a string
     *
     * @param delimiter The separating character
     * @return The combined string
     */
    public static String combineArgs(String delimiter, Object... objects)
    {
        StringJoiner joiner = new StringJoiner(" ");

        for (Object object : objects)
        {
            joiner.add(object.toString()).add(delimiter);
        }

        return joiner.toString();
    }

    /**
     * Determines what percentage of a string is in caps
     * @param input The input message
     *
     * @return The caps percentage
     */
    public static double percentCaps(String input)
    {
        int caps = 0;

        for (char character : input.toCharArray())
        {
            if (Character.isUpperCase(character))
            {
                caps++;
            }
        }

        return (double) caps / input.length() * 100.0D;
    }
}