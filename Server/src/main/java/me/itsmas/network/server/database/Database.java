package me.itsmas.network.server.database;

import me.itsmas.network.server.user.User;

import java.util.UUID;
import java.util.function.Consumer;

public interface Database
{
    /**
     * Connects to the database
     */
    void connect();

    /**
     * Fetches a {@link User} from the database asynchronously
     *
     * @param uuid The {@link UUID} of the user to fetch
     * @param consumer The {@link Consumer} called when the user is found
     */
    void getUser(UUID uuid, Consumer<User> consumer);

    /**
     * Fetches a {@link User} from the database
     *
     * @param uuid The {@link UUID} of the user to fetch
     * @return The user
     */
    User getUser(UUID uuid);

    /**
     * Saves a user to the database
     *
     * @param user The user to save
     */
    void saveUser(User user);
}
