package me.itsmas.network.server.rank;

import org.bukkit.ChatColor;

/**
 * The ranks on the network
 */
public enum Rank
{
    OWNER("Owner", ChatColor.GOLD),
    ADMIN("Admin", ChatColor.RED),
    DEV("Dev", ChatColor.RED),
    // Upper Staff ^

    MOD("Mod", ChatColor.DARK_GREEN),
    HELPER("Helper", ChatColor.GREEN),
    // Staff ^

    BUILDER("Builder", ChatColor.BLUE),
    YOUTUBE("YT", ChatColor.GOLD),
    // Special Ranks ^

    ELITE("Elite", ChatColor.BLUE),
    PRO("PRO", ChatColor.LIGHT_PURPLE),
    VIP("VIP", ChatColor.GREEN),

    DEFAULT("Default", ChatColor.GRAY);
    // Defaults + Donors ^

    /**
     * Rank constructor
     * @param name The name of the rank
     * @param colour The rank's display colour
     */
    Rank(String name, ChatColor colour)
    {
        this.name = name;
        this.colour = colour;
    }

    /**
     * The rank name
     */
    private final String name;

    /**
     * The rank colour
     */
    private final ChatColor colour;

    /**
     * Gets the name of the rank
     *
     * @see #name
     * @return The rank's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the colour of the rank
     *
     * @see #colour
     * @return The rank's colour
     */
    public ChatColor getColour()
    {
        return colour;
    }
}