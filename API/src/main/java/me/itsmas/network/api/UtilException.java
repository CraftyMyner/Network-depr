package me.itsmas.network.api;

/**
 * Exception thrown when an attempt is made to instantiate a utility class
 */
public final class UtilException extends RuntimeException
{
    private UtilException()
    {
        super("Cannot instantiate utility class");
    }

    /**
     * Creates and throws a new UtilException
     */
    public static void throwNew()
    {
        throw new UtilException();
    }
}
