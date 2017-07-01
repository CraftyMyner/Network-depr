package me.itsmas.network.server.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import me.itsmas.network.server.Core;
import me.itsmas.network.server.module.Module;
import me.itsmas.network.server.user.User;
import me.itsmas.network.server.util.UtilServer;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.mapping.DefaultCreator;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoDB extends Module implements Database
{
    public MongoDB(Core core)
    {
        super(core, "Mongo Database");

        connect();
    }

    /**
     * The database credentials
     */
    private DatabaseInfo info;

    /**
     * The {@link MongoClient} instance
     *
     */
    private MongoClient client;

    /**
     * The {@link MongoDatabase} instance
     */
    private MongoDatabase database;

    /**
     * The collection storing {@link User} data
     */
    private UserDAO userDAO;

    @Override
    public void connect()
    {
        log("Attempting to connect to MongoDB database");

        info = new DatabaseInfo(core);

        client = new MongoClient(
                new ServerAddress(info.getIp(), info.getPort()),
                Collections.singletonList(MongoCredential.createCredential(info.getUsername(), info.getDatabase(), info.getPassword().toCharArray()))
        );

        database = client.getDatabase(info.getDatabase());

        createCollections();
        setupMorphia();

        log("Connected to MongoDB database");
    }

    /**
     * Initialises the Morphia settings
     */
    private void setupMorphia()
    {
        Morphia morphia = new Morphia();

        morphia.map(User.class);

        Datastore datastore = morphia.createDatastore(client, info.getDatabase());
        datastore.ensureIndexes();

        userDAO = new UserDAO(User.class, datastore);

        morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator()
        {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return core.getClass().getClassLoader();
            }
        });
    }

    /**
     * Creates all necessary collections
     */
    private void createCollections()
    {
        database.getCollection(User.COLLECTION);
    }

    @Override
    public void getUser(UUID uuid, Consumer<User> consumer)
    {
        UtilServer.runAsync(() ->
        {
            User user = getUser(uuid);

            UtilServer.runSync(() -> consumer.accept(user));
        });
    }

    @Override
    public User getUser(UUID uuid)
    {
        return userDAO.findOne("uuid", uuid);
    }

    @Override
    public void saveUser(User user)
    {
        UtilServer.runAsync(() -> userDAO.save(user));
    }

    /**
     * Wrapper for {@link BasicDAO} for storing {@link User} objects
     */
    private class UserDAO extends BasicDAO<User, String>
    {
        UserDAO(Class<User> entityClass, Datastore datastore)
        {
            super(entityClass, datastore);
        }
    }
}
