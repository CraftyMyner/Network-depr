package me.itsmas.network.api;

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
        throw new AssertionError("Cannot instantiate utility class");
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
     * Replaces the last occurrence of characters in a {@link String}
     *
     * @param string The string to replace the characters of
     * @param last The sequence of characters to find
     * @param replacement What to replace the character with
     * @return The replaced string
     */
    public static String replaceLast(String string, String last, String replacement)
    {
        StringBuffer buffer = new StringBuffer(string);
        buffer = new StringBuffer(buffer.reverse().toString().replaceFirst(last, replacement));

        return buffer.reverse().toString();
    }
}
