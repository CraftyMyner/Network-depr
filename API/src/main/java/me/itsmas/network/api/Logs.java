package me.itsmas.network.api;

import java.util.logging.Logger;

/**
 * Logging utility methods
 */
public final class Logs
{
    /**
     * Private constructor; no instances of this class are needed
     */
    private Logs()
    {
        UtilException.throwNew();
    }

    /**
     * The logger to use for logging
     */
    private static final Logger LOGGER = Logger.getLogger("Network");

    /**
     * The logging prefix
     */
    private static final String PREFIX = "[Network] ";

    /**
     * Logs a message
     *
     * @param msg The message to log
     */
    public static void log(String msg)
    {
        LOGGER.info(PREFIX + msg);
    }

    /**
     * Logs an error
     *
     * @param msg The message to log
     */
    public static void logError(String msg)
    {
        LOGGER.severe(PREFIX + msg);
    }
}
