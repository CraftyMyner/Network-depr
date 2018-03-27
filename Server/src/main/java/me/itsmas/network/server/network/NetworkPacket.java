package me.itsmas.network.server.network;

import com.google.gson.JsonObject;
import me.itsmas.network.api.JsonBuilder;
import me.itsmas.network.server.task.NetworkTask;
import me.itsmas.network.server.user.User;

/**
 * A packet to be sent across servers
 */
public class NetworkPacket
{
    /**
     * The user relating to this packet
     */
    private final User user;

    /**
     * The task type of the packet
     */
    private final String type;

    /**
     * The data stored in this packet
     */
    private final JsonObject data;

    /**
     * The {@link NetworkTask} class of this packet
     */
    private final Class<? extends NetworkTask> clazz;

    public NetworkPacket(NetworkTask task)
    {
        this.user = task.getUser();
        this.type = task.getType();
        this.data = task.toJson();
        this.clazz = task.getClass();
    }

    @Override
    public String toString()
    {
        JsonBuilder builder = JsonBuilder.newBuilder()
                .add("type", type)
                .add("class", clazz.getCanonicalName())
                .add("user", user.getUniqueId())
                .add("data", data);

        return builder.build().toString();
    }
}
