package me.itsmas.network.server.task;

import com.google.gson.JsonObject;
import me.itsmas.network.api.JsonBuilder;
import me.itsmas.network.server.network.NetworkPacket;
import me.itsmas.network.server.user.User;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Task abstraction
 */
public abstract class NetworkTask
{
    /**
     * The user relating to this task
     */
    private final transient User user;

    /**
     * NetworkTask constructor
     *
     * @param user The user relating to this task
     */
    public NetworkTask(User user)
    {
        this.user = user;
    }

    /**
     * Fetches the user relating to this task
     *
     * @return The user
     */
    public User getUser()
    {
        return user;
    }

    /**
     * Execute the task for the user while they are online
     *
     * @param user The user
     */
    public abstract void executeOnline(User user);

    /**
     * Execute the task for the user while they are offline
     *
     * @param user The user
     */
    public abstract void executeOffline(User user);

    /**
     * Parses the arguments for the task
     *
     * @param object The {@link JsonObject} containing the data
     */
    public abstract void parseArguments(JsonObject object);

    /**
     * Gets the task type
     *
     * @return The type
     */
    public abstract String getType();

    /**
     * Fetches the task as a {@link NetworkPacket}
     *
     * @return The packet
     */
    NetworkPacket toPacket()
    {
        return new NetworkPacket(this);
    }

    /**
     * Retrieves this task's data as a {@link JsonObject}
     *
     * @return The json object
     */
    public final JsonObject toJson()
    {
        JsonBuilder builder = JsonBuilder.newBuilder();

        for (Field field : getClass().getFields())
        {
            if (!field.isAccessible())
            {
                field.setAccessible(true);
            }

            if (!Modifier.isTransient(field.getModifiers()))
            {
                try
                {
                    builder.add(field.getName(), field.get(this).toString());
                }
                catch (IllegalAccessException ignored) {}
            }
        }

        return builder.build();
    }
}
