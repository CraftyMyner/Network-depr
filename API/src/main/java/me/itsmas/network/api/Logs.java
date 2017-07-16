package me.itsmas.network.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

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

    static
    {
        LOGGER.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());

        LOGGER.addHandler(handler);
    }

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

    /**
     * The {@link LogFormatter} to use when logging
     */
    private static final class LogFormatter extends Formatter
    {
        /**
         * The prefix to use in log messages
         */
        private static final String LOG_PREFIX = "[Network]";

        /**
         * The logging date format
         */
        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy@hh:mm:ss");

        @Override
        public String format(LogRecord record)
        {
            String formatted = "";

            formatted += LOG_PREFIX + "[" + DATE_FORMAT.format(new Date()) + "]";

            if (record.getLevel() != Level.INFO)
            {
                formatted += "[" + record.getLevel().getName() + "]";
            }

            formatted += " " + record.getMessage() + System.lineSeparator();

            return formatted;
        }
    }
}
