package me.itsmas.network.server.command.context;

import me.itsmas.network.server.user.User;

/**
 * Resolves an argument from a command
 *
 * @param <T> The type of object to resolve
 */
public interface ContextResolver<T>
{
    /**
     * Resolves a command argument
     *
     * @param input The input argument
     * @return The resolved object, or null if not able to resolve
     */
    T resolve(String input);

    /**
     * Sends an error to a {@link User} when an argument could not be resolved
     *
     * @param user The user
     * @param input The argument the user used
     */
    void sendError(User user, String input);
}
