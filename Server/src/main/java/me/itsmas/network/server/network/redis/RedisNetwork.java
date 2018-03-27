package me.itsmas.network.server.network.redis;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.network.Network;
import me.itsmas.network.server.network.NetworkPacket;
import me.itsmas.network.server.util.UtilServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Redis {@link Network} implementation
 */
public class RedisNetwork extends Network
{
    public RedisNetwork(Core core)
    {
        super(core, "Redis Network");
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

        initJedis();
    }

    /**
     * Initialises Jedis connection
     */
    private void initJedis()
    {
        log("Attempting to connect to Redis");

        RedisInfo info = new RedisInfo(core);
        pool = new JedisPool(new JedisPoolConfig(), info.getIp(), info.getPort(), Protocol.DEFAULT_TIMEOUT, info.getPassword());

        initPubSub();

        log("Redis connection established");
    }

    /**
     * The Jedis pool instance
     */
    private JedisPool pool;

    /**
     * The publisher/subscriber instance
     */
    private NetworkPubSub pubSub;

    /**
     * Initialises the pubsub
     *
     * @see #pubSub
     */
    private void initPubSub()
    {
        pubSub = new NetworkPubSub(this);
    }

    @Override
    protected void setServerName(String serverName)
    {
        super.setServerName(serverName);

        pubSub.subscribeChannels(serverName, GLOBAL_CHANNEL);
    }

    @Override
    public void onDisable()
    {
        pool.close();
    }

    /**
     * The field storing a player's server in Redis
     */
    private final String SERVER_FIELD = "server";

    /**
     * The queue of players who need their location updating once the server name is found
     */
    private Set<String> updateQueue = new HashSet<>();

    /**
     * Updates the location of all players in the update queue
     *
     * @see #updateQueue
     * @see #updateServer(String)
     */
    public void resetQueue()
    {
        updateQueue.forEach(this::updateServer);

        updateQueue = null;
    }

    @Override
    public void updateServer(String player)
    {
        if (getServerName() == null)
        {
            updateQueue.add(player);
            return;
        }

        executeAsync(jedis -> jedis.hset(player.toLowerCase(), SERVER_FIELD, getServerName()));
    }

    @Override
    public void removeTrackingData(String player)
    {
        executeAsync(jedis -> jedis.hdel(player.toLowerCase(), SERVER_FIELD));
    }

    @Override
    public void getServer(String player, Consumer<String> consumer)
    {
        executeAsync(jedis ->
        {
            String value = jedis.hget(player.toLowerCase(), SERVER_FIELD);

            UtilServer.runSync(() -> consumer.accept(value));
        });
    }

    @Override
    public void sendPacket(NetworkPacket packet, String server)
    {
        executeAsync(jedis -> jedis.publish(server, packet.toString()));
    }

    /**
     * Fetches a {@link Jedis} object from the pool and executes an action for it
     *
     * @see #pool
     * @param consumer The action to execute
     */
    private void executeJedis(Consumer<Jedis> consumer)
    {
        try (Jedis jedis = pool.getResource())
        {
            consumer.accept(jedis);
        }
    }

    /**
     * Executes a {@link Jedis} task asynchronously
     *
     * @see #executeJedis(Consumer)
     * @param consumer The action to execute
     */
    private void executeAsync(Consumer<Jedis> consumer)
    {
        UtilServer.runAsync(() -> executeJedis(consumer));
    }
}
