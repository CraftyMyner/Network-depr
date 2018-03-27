package me.itsmas.network.server.util;

import me.itsmas.network.api.UtilException;
import org.bukkit.ChatColor;

/**
 * Wrapper for {@link ChatColor} enums as strings
 */
public final class C
{
    /**
     * Private constructor; no instances of this class are needed
     */
    private C()
    {
        UtilException.throwNew();
    }

    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String WHITE = ChatColor.WHITE.toString();

    // Formatting
    public static final String RESET = ChatColor.RESET.toString();

    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString();
    public static final String UNDERLINE = ChatColor.UNDERLINE.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();

    /**
     * Bolds an input message
     *
     * @param input The input message
     * @return The bolded message
     */
    public static String bold(String input)
    {
        return BOLD + input;
    }
}
