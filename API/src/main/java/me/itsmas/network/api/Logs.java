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
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * The logger to use for logging
     */
    private static final Logger logger = Logger.getLogger("Network");

    /**
     * Logs a message
     *
     * @param msg The message to log
     */
    public static void log(String msg)
    {
        logger.info(msg);
    }

    /**
     * Logs an error
     *
     * @param msg The message to log
     */
    public static void logError(String msg)
    {
        logger.severe(msg);
    }
}
