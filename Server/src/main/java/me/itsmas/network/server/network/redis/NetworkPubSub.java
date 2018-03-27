package me.itsmas.network.server.network.redis;

import com.google.gson.JsonObject;
import me.itsmas.network.api.UtilJson;
import me.itsmas.network.server.module.SubModule;
import me.itsmas.network.server.network.Network;
import me.itsmas.network.server.task.NetworkTask;
import me.itsmas.network.server.user.User;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Jedis PubSub implementation
 */
class NetworkPubSub extends SubModule<RedisNetwork>
{
    NetworkPubSub(RedisNetwork module)
    {
        super(module);
    }

    private final JedisPubSub pubSub = new JedisPubSub()
    {
        @Override
        public void onMessage(String channel, String msg)
        {
            if (!channelIsApplicable(channel))
            {
                return;
            }

            JsonObject object = UtilJson.parse(msg);

            if (object == null)
            {
                logFatal("Invalid JSON in incoming packet from: " + msg);
                return;
            }

            String packetType = object.get("type").getAsString();
            Class<?> clazz = findClass(object, packetType);

            if (clazz == null)
            {
                return;
            }

            UUID userId = UUID.fromString(object.get("user").getAsString());
            User user = core.getUserManager().getUser(userId);

            executeTask(clazz, object, user, packetType);
        }
    };

    /**
     * Executes a task from an incoming packet
     *
     * @param clazz The class of the task
     * @param object The json data from the packet
     * @param user The user relating to the task
     * @param packetType The type of packet being received
     */
    private void executeTask(Class<?> clazz, JsonObject object, User user, String packetType)
    {
        if (user != null)
        {
            try
            {
                Constructor constructor = clazz.getDeclaredConstructor(User.class);
                NetworkTask task = (NetworkTask) constructor.newInstance(user);

                task.parseArguments(object.get("data").getAsJsonObject());
                task.executeOnline(user);
            }
            catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException ex)
            {
                logFatal("Error instantiating task from incoming packet of type " + packetType);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Subscribes the pubsub to the given channels
     *
     * @see #pubSub
     * @param channels The channels to subscribe to
     */
    void subscribeChannels(String... channels)
    {
        pubSub.subscribe(channels);
    }

    /**
     * Fetches the {@link Class} of a task from a {@link JsonObject}
     *
     * @param object The json object
     * @param packetType The type of packet being received
     * @return The class
     */
    private Class<?> findClass(JsonObject object, String packetType)
    {
        String className = object.get("class").getAsString();

        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException ex)
        {
            logFatal("Class not found in incoming packet of type " + packetType + ": " + className);
            return null;
        }
    }

    /**
     * Determines whether to listen to a message on a certain channel
     *
     * @param channel The channel
     * @return Whether to listen to the channel's messages
     */
    private boolean channelIsApplicable(String channel)
    {
        return channel.equals(Network.GLOBAL_CHANNEL) || channel.equals(module.getServerName());
    }
}
