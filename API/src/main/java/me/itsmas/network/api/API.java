package me.itsmas.network.api;

/**
 * Main API class
 */
public final class API
{
    /**
     * Private constructor; no instances of this class are needed
     */
    private API()
    {
        UtilException.throwNew();
    }

    /**
     * The API version
     */
    public static final String VERSION = "1.0";
}
