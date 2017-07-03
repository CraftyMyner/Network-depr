package me.itsmas.network.server.user;

import me.itsmas.network.api.UtilTime;
import me.itsmas.network.server.Core;
import me.itsmas.network.server.lang.Lang;
import me.itsmas.network.server.network.Network;
import me.itsmas.network.server.rank.Rank;
import me.itsmas.network.server.util.C;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Transient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity(value = User.COLLECTION)
public class User
{
    @Transient
    public static final String COLLECTION = "users";

    @Id
    private int id;

    @Indexed(options = @IndexOptions(unique = true))
    private UUID uuid;

    private Rank rank;
    private String prefix;

    private String locale;

    private long firstJoin;

    @Property(value = "play_time")
    private long playTime;

    private List<String> logs;

    @Property(value = "trusted_ips")
    private List<String> trustedIps;

    private String pin;

    public User(){}

    User(UUID uuid)
    {
        this.uuid = uuid;

        this.rank = Rank.DEFAULT;
        this.prefix = rank.getName();

        this.locale = Lang.DEFAULT_LOCALE;

        this.firstJoin = System.currentTimeMillis();

        this.playTime = 0L;

        this.logs = new ArrayList<>();

        this.trustedIps = new ArrayList<>();

        this.pin = "";

        addLog("Joined for the first time", false);
    }

    @Transient
    private Core core;

    @Transient
    private Player player;

    @Transient
    private long joinTime;

    @Transient
    private final Map<String, Long> cooldowns = new HashMap<>();

    /**
     * Initialises fields upon user joining the server
     *
     * @param core The plugin instance
     * @param player The {@link Player} associated with this user
     */
    void initJoin(Core core, Player player)
    {
        this.core = core;
        this.player = player;

        joinTime = System.currentTimeMillis();

        updateTabName();

        updateCooldown(Network.SWITCH_COOLDOWN, 2500);
    }

    /**
     * Saves the user to the database
     *
     * @param proxy Whether the user is leaving the proxy
     */
    public void save(boolean proxy)
    {
        updatePlayTime();

        core.getDatabase().saveUser(this);

        if (!proxy)
        {
            core.getUserManager().updateSaved(this);
        }
    }

    /**
     * Determines whether a player is on cooldown
     *
     * @param name The name of the cooldown
     * @param time The length of the cooldown in milliseconds
     * @param message Whether to message the player if they are still on cooldown
     * @return Whether the player is on cooldown
     */
    public boolean isCoolingDown(String name, long time, boolean message)
    {
        if (!cooldowns.containsKey(name) || cooldowns.get(name) < System.currentTimeMillis())
        {
            updateCooldown(name, time);
            return false;
        }

        if (message)
        {
            if (isOnline())
            {
                sendMessage("cooldown;message");
            }
        }

        return true;
    }

    /**
     * Updates a cooldown
     *
     * @param name The name of the cooldown
     * @param time The length in milliseconds the cooldown should last
     */
    private void updateCooldown(String name, long time)
    {
        cooldowns.put(name, System.currentTimeMillis() + time);
    }

    /**
     * Updates the user's playtime
     */
    public void updatePlayTime()
    {
        playTime += (System.currentTimeMillis() - joinTime);

        joinTime = System.currentTimeMillis();
    }

    /**
     * Gets whether the user is online
     *
     * @return If the user is online
     */
    public boolean isOnline()
    {
        return player != null && player.isOnline();
    }

    /**
     * Fetches the user's IP address
     *
     * @see #isOnline()
     * @return The user's IP, or null if offline
     */
    public String getIp()
    {
        if (player == null)
        {
            return null;
        }

        return player.getAddress().getAddress().getHostAddress();
    }

    /**
     * Adds a log to the user's record
     *
     * @see #addLog(String, boolean)
     * @param log The log message
     */
    public void addLog(String log)
    {
        addLog(log, true);
    }

    /**
     * Adds a log to the user's record
     *
     * @param log The log message
     * @param serverName Whether to include the server name in the log message
     */
    public void addLog(String log, boolean serverName)
    {
        String server = serverName ? "[" + core.getNetwork().getServerName() + "] " : "";
        String ip = isOnline() ? "[" + getIp() + "]" : "";

        logs.add(UtilTime.now() + " > " + ip + server + log);
    }

    /**
     * Fetches the user's ping if online
     *
     * @throws AssertionError If the user is offline
     * @return The user's ping
     */
    public int getPing()
    {
        assertOnline();

        return ((CraftPlayer) player).getHandle().ping;
    }

    /**
     * Determines whether the user has trusted an IP address
     *
     * @param ip The IP to check
     * @return Whether the IP address is trusted
     */
    public boolean isIpTrusted(String ip)
    {
        return trustedIps.contains(ip);
    }

    /**
     * Sets the user's rank
     *
     * @see #rank
     * @see #setRank(Rank, String)
     * @param rank The user's new rank
     */
    public void setRank(Rank rank)
    {
        setRank(rank, rank.getName());
    }

    /**
     * Sets the user's rank
     *
     * @see #rank
     * @param rank The user's new rank
     * @param prefix The prefix to give the user
     */
    public void setRank(Rank rank, String prefix)
    {
        this.rank = rank;
        this.prefix = prefix;

        updateTabName();
    }

    /**
     * Determines whether the user has a {@link Rank}
     *
     * @param rank The rank to check
     * @return Whether the user has the rank
     */
    public boolean hasRank(Rank rank)
    {
        return hasRank(rank, true);
    }

    /**
     * Determines whether the user has a rank
     *
     * @param rank The rank to check
     * @param inform Whether to send a "no permission" message if necessary
     * @return Whether the user has the rank
     */
    public boolean hasRank(Rank rank, boolean inform)
    {
        if (getRank().compareTo(rank) <= 0)
        {
            return true;
        }

        if (inform && isOnline())
        {
            sendMessage("no_permission");
        }

        return false;
    }

    /**
     * Determines whether the user has a specific rank
     *
     * @param ranks The ranks to check
     * @return Whether the user's rank is one of the specified ranks
     */
    public boolean hasRankSpecific(Rank... ranks)
    {
        for (Rank rank : ranks)
        {
            if (getRank() == rank)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Sets the user's locale
     *
     * @param locale The locale
     */
    public void setLocale(String locale)
    {
        assert core.getLang().isSupportedLocale(locale) : "Locale not supported: " + locale;

        if (locale.equals(getLocale()) && isOnline())
        {
            sendMessage("locale;already_using");
            return;
        }

        this.locale = locale;

        if (isOnline())
        {
            sendMessage("locale;updated");
        }
    }

    /**
     * Sends the user a message if online
     *
     * @throws AssertionError If the user is offline
     * @param messageId The ID of the message to send
     * @param format Optional placeholder values
     */
    public void sendMessage(String messageId, Object... format)
    {
        assertOnline();

        getPlayer().sendMessage(core.getLang().getMessage(getLocale(), messageId.toLowerCase(), format));
    }

    /**
     * Sends the user a raw message if online
     *
     * @throws AssertionError If the user is offline
     * @param msg The message to send
     */
    public void sendRaw(String msg)
    {
        assertOnline();

        getPlayer().sendMessage(msg);
    }

    /**
     * Updates the user's tab name with their rank
     */
    private void updateTabName()
    {
        String listName;

        if (getRank() == Rank.DEFAULT)
        {
            listName = getRank().getColour().toString();
        }
        else
        {
            listName = getFormattedRank() + C.DARK_GRAY + " | " + getRank().getColour() + getName();
        }

        getPlayer().setPlayerListName(listName);
    }

    /**
     * Asserts that the user is online
     *
     * @throws AssertionError If the user is offline
     */
    private void assertOnline()
    {
        assert isOnline() : "User is not online";
    }

    /* Getters */

    /**
     * @return The {@link Player} associated with the user
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * @return The user's first join time in milliseconds
     */
    public long getFirstJoin()
    {
        return firstJoin;
    }

    /**
     * @return The user's playtime in milliseconds
     */
    public long getPlayTime()
    {
        updatePlayTime();

        return playTime;
    }

    /**
     * @return The user's database object ID
     */
    public int getDatabaseId()
    {
        return id;
    }

    /**
     * @return The user's name
     */
    public String getName()
    {
        if (player != null)
        {
            return player.getName();
        }

        return Bukkit.getOfflinePlayer(getUniqueId()).getName();
    }

    /**
     * @return The user's {@link UUID}
     */
    public UUID getUniqueId()
    {
        return uuid;
    }

    /**
     * @return The user's {@link Rank}
     */
    public Rank getRank()
    {
        return rank;
    }

    /**
     * @return The user's formatted rank
     */
    public String getFormattedRank()
    {
        return getRank().getColour() + getPrefix();
    }

    /**
     * @return The user's prefix
     */
    private String getPrefix()
    {
        return prefix;
    }

    /**
     * @return The user's locale
     */
    public String getLocale()
    {
        return locale;
    }

    /**
     * @return The user's pin
     */
    public String getPin()
    {
        return pin;
    }
}
