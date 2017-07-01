package me.itsmas.network.server.network;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.util.UtilServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

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

        log("Redis connection established");
    }

    /**
     * The Jedis pool instance
     */
    private JedisPool pool;

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
    void resetQueue()
    {
        updateQueue.forEach(this::updateServer);

        updateQueue = null;
    }

    @Override
    void updateServer(String player)
    {
        if (getServerName() == null)
        {
            updateQueue.add(player);
            return;
        }

        executeJedis(jedis -> jedis.hset(player.toLowerCase(), SERVER_FIELD, getServerName()));
    }

    @Override
    void removeTrackingData(String player)
    {
        executeJedis(jedis -> jedis.hdel(player.toLowerCase(), SERVER_FIELD));
    }

    @Override
    public void getServer(String player, Consumer<String> consumer)
    {
        executeJedis(jedis ->
            UtilServer.runAsync(() ->
            {
                String value = jedis.hget(player.toLowerCase(), SERVER_FIELD);

                UtilServer.runSync(() -> consumer.accept(value));
            })
        );
    }

    /**
     * Fetches a {@link Jedis} object and executes an action for it
     *
     * @param consumer The action to execute
     */
    private void executeJedis(Consumer<Jedis> consumer)
    {
        try (Jedis jedis = pool.getResource())
        {
            consumer.accept(jedis);
        }
    }
}
