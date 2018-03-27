package me.itsmas.network.api;

import com.google.gson.JsonObject;

/**
 * Builder for a {@link JsonBuilder}
 */
public final class JsonBuilder
{
    private JsonBuilder() {}

    /**
     * Fetches a new JsonBuilder
     *
     * @return A new builder instance
     */
    public static JsonBuilder newBuilder()
    {
        return new JsonBuilder();
    }

    /**
     * The {@link JsonObject}
     */
    private final JsonObject object = new JsonObject();

    /**
     * Adds a property to the {@link JsonObject}
     *
     * @see #object
     * @param key The property key
     * @param value The property value
     * @return The class instance
     */
    public JsonBuilder add(String key, Object value)
    {
        object.addProperty(key, value.toString());
        return this;
    }

    /**
     * Fetches the {@link JsonObject}
     *
     * @see #object
     * @return The json object
     */
    public JsonObject build()
    {
        return object;
    }
}
