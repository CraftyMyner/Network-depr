package me.itsmas.network.api;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Time utility methods
 */
public final class UtilTime
{
    /**
     * Private constructor; no instances of this class are needed
     */
    private UtilTime()
    {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Determines whether an amount of time has elapsed
     *
     * @param time The amount that may have elapsed (ms)
     * @param since The time
     * @return Whether the time has elapsed
     */
    public static boolean elapsed(long time, long since)
    {
        return System.currentTimeMillis() - since > time;
    }

    /**
     * Converts a length of time in milliseconds to a friendly string
     *
     * @param millis The time to convert (ms)
     * @return The friendly string
     */
    public static String toFriendlyString(long millis)
    {
        String duration =  DurationFormatUtils.formatDuration(millis, "dd/ HH/ mm/ ss/");

        for (TimeUnit unit : TimeUnit.values())
        {
            if (unit.ordinal() > 2)
            {
                duration = UtilString.replaceLast(duration, "/", unit.name().substring(1, 1).toLowerCase());
            }
        }

        return duration;
    }

    /**
     * Constant date format for logging
     */
    private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy@HH:mm:ss/z");

    /**
     * Fetches the current time and date for logging
     *
     * @see #LOG_DATE_FORMAT
     * @return The current date and time
     */
    public static String now()
    {
        return LOG_DATE_FORMAT.format(Calendar.getInstance().getTime());
    }
}
