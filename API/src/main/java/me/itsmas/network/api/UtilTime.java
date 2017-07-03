package me.itsmas.network.api;

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
        String string = "";
        long cumulative = 0L;

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        cumulative += TimeUnit.DAYS.toMillis(days);

        if (days != 0L)
        {
            string += days + "d";
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis - cumulative);
        cumulative += TimeUnit.HOURS.toMillis(hours);

        if (hours != 0L)
        {
            string += hours + "h";
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis - cumulative);
        cumulative += TimeUnit.MINUTES.toMillis(minutes);

        if (minutes != 0L)
        {
            string += minutes + "m";
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis - cumulative);

        if (seconds != 0L)
        {
            string += seconds + "s";
        }

        return string;
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
