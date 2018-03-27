package me.itsmas.network.server.user;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.module.Module;
import me.itsmas.network.server.user.command.UserCommands;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserManager extends Module
{
    public UserManager(Core core)
    {
        super(core, "Users");
    }

    @Override
    public void onEnable()
    {
        addSubModule(new UserListener(this));

        registerCommands(new UserCommands(core));
    }

    /**
     * Recently saved users
     */
    private final Set<User> recentlySaved = new HashSet<>();

    /**
     * Updates the time a {@link User} was saved
     *
     * @see #recentlySaved
     * @param user The user
     */
    void updateSaved(User user)
    {
        recentlySaved.add(user);
    }

    /**
     * Fetches whether a {@link User} was recently saved
     *
     * @param user The user
     * @return Whether the user was recently saved
     */
    boolean recentlySaved(User user)
    {
        return recentlySaved.remove(user);
    }

    /**
     * Map linking {@link Player} {@link UUID} instances to {@link User} objects
     */
    private final Map<UUID, User> users = new HashMap<>();

    /**
     * Fetches a player's {@link User} object
     *
     * @param player The player
     * @return The user object
     */
    public User getUser(Player player)
    {
        return getUser(player.getUniqueId());
    }

    /**
     * Fetches a {@link User} object from a {@link UUID}
     *
     * @param uuid The user's UUID
     * @return The user
     */
    public User getUser(UUID uuid)
    {
        return users.get(uuid);
    }

    /**
     * Fetches all online users
     *
     * @return The users
     */
    public Collection<User> getUsers()
    {
        return users.values();
    }

    /**
     * Inserts a user into the user map
     *
     * @see #users
     * @param user The user to insert
     */
    void insertUser(User user)
    {
        users.put(user.getUniqueId(), user);
    }

    /**
     * Removes a {@link User} from the user map
     *
     * @see #users
     * @param player The {@link Player} of the user to remove
     */
    void removeUser(Player player)
    {
        users.remove(player.getUniqueId());
    }
}
