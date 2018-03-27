package me.itsmas.network.server.util;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.itsmas.network.api.UtilException;
import me.itsmas.network.server.Core;
import me.itsmas.network.server.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

/**
 * Server utility methods
 */
public final class UtilServer
{
    /**
     * Private constructor; no instances of this class are needed
     */
    private UtilServer()
    {
        UtilException.throwNew();
    }

    /**
     * The plugin instance
     */
    private static final Core PLUGIN = JavaPlugin.getPlugin(Core.class);

    /**
     * Fetches the online players on the server
     *
     * @return The online players
     */
    public static Collection<? extends Player> getPlayers()
    {
        return Bukkit.getOnlinePlayers();
    }

    /**
     * Registers a {@link Listener}
     *
     * @param listener The listener to register
     */
    public static void registerListener(Listener listener)
    {
        Bukkit.getPluginManager().registerEvents(listener, PLUGIN);
    }

    /**
     * Registers an outgoing plugin channel
     *
     * @param channel The name of the channel
     */
    public static void registerOutgoing(String channel)
    {
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(PLUGIN, channel))
        {
            Bukkit.getMessenger().registerOutgoingPluginChannel(PLUGIN, channel);
        }
    }

    /**
     * Registers an incoming plugin channel
     *
     * @param listener The {@link PluginMessageListener} to receive the plugin messages
     * @param channel The channel the listener should listen to
     */
    public static void registerIncoming(PluginMessageListener listener, String channel)
    {
        Bukkit.getMessenger().registerIncomingPluginChannel(PLUGIN, channel, listener);
    }

    /**
     * Forwards a message to BungeeCord through the plugin messaging channel
     *
     * @param args The data to send
     * @return Whether the data was sent
     */
    public static boolean writeBungee(String... args)
    {
        assert args.length > 0 : "Args length must be at least 1";

        Player player = Iterables.getFirst(UtilServer.getPlayers(), null);

        if (player == null)
        {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        for (String arg : args)
        {
            out.writeUTF(arg);
        }

        player.sendPluginMessage(PLUGIN, "BungeeCord", out.toByteArray());

        return true;
    }

    /**
     * Executes a task asynchronously
     *
     * @see BukkitRunnable#runTaskAsynchronously(Plugin)
     * @param runnable The task to execute
     */
    public static void runAsync(Runnable runnable)
    {
        getBukkitRunnable(runnable).runTaskAsynchronously(PLUGIN);
    }

    /**
     * Executes a task on the main thread
     *
     * @see BukkitRunnable#runTask(Plugin)
     * @param runnable The task to execute
     */
    public static void runSync(Runnable runnable)
    {
        getBukkitRunnable(runnable).runTask(PLUGIN);
    }

    /**
     * Executes task after a given delay
     *
     * @see BukkitRunnable#runTaskLater(Plugin, long)
     * @param runnable The task to execute
     * @param delay The delay before the task should be executed (ticks)
     */
    public static void runDelayed(Runnable runnable, long delay)
    {
        getBukkitRunnable(runnable).runTaskLater(PLUGIN, delay);
    }

    /**
     * Executes a repeating task on the main thread
     *
     * @see BukkitRunnable#runTaskTimer(Plugin, long, long)
     * @param runnable The task to execute
     * @param delay The delay (ticks) before first starting the task
     * @param interval The interval (ticks) between each iteration
     */
    public static void runRepeating(Runnable runnable, long delay, long interval)
    {
        getBukkitRunnable(runnable).runTaskTimer(PLUGIN, delay, interval);
    }

    /**
     * Gets a {@link BukkitRunnable} from a {@link Runnable}
     *
     * @param runnable The input runnable
     * @return The BukkitRunnable
     */
    private static BukkitRunnable getBukkitRunnable(Runnable runnable)
    {
        return new BukkitRunnable()
        {
            @Override
            public void run()
            {
                runnable.run();
            }
        };
    }

    /**
     * Broadcasts a message to all players
     *
     * @param msg The message to send
     */
    public static void broadcast(String msg)
    {
        broadcast(msg, null);
    }

    /**
     * Broadcasts a message to all players of a certain rank
     *
     * @param msg The message to send
     * @param rank The rank required to receive the message
     */
    private static void broadcast(String msg, Rank rank)
    {
        PLUGIN.getUserManager().getUsers().stream().filter(user -> rank == null || user.hasRank(rank, false)).forEach(user -> user.sendRaw(msg));
    }

    private static final String INTERNAL_PREFIX = C.AQUA + C.BOLD + "[INTERNAL]" + C.RESET;

    /**
     * Broadcasts an internal alert
     *
     * @param msg The message to send
     * @param rank The rank required to receive the alert
     */
    public static void broadcastInternal(String msg, Rank rank)
    {
        broadcastInternal(msg, rank, false);
    }

    /**
     * Broadcasts an internal alert
     *
     * @param msg The message to send
     * @param rank The rank required to receive the alert
     * @param error Whether the alert is an error
     */
    public static void broadcastInternal(String msg, Rank rank, boolean error)
    {
        String errorWarning = error ? C.RED + "ERROR: " + C.RESET : "";

        broadcast(INTERNAL_PREFIX + " " + errorWarning + msg, rank);
    }
}
