package me.itsmas.network.server.database;

import me.itsmas.network.server.Core;

class DatabaseInfo
{
    private final String ip;
    private final int port;
    private final String database;
    private final String username;

    private String password;

    DatabaseInfo(Core core)
    {
        this.ip = core.getConfig("database.ip");
        this.port = core.getConfig("database.port");
        this.database = core.getConfig("database.database");
        this.username = core.getConfig("database.username");

        this.password = core.getConfig("database.password");
    }

    String getIp()
    {
        return ip;
    }

    int getPort()
    {
        return port;
    }

    String getDatabase()
    {
        return database;
    }

    String getUsername()
    {
        return username;
    }

    String getPassword()
    {
        // After password has been fetched once, prevent it from being done so again
        assert password != null : "getPassword() called multiple times";

        String password = this.password;
        this.password = null;

        return password;
    }
}
