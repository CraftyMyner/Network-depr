package me.itsmas.network.server.network;

import me.itsmas.network.server.Core;

class RedisInfo
{
    private final String ip;
    private final int port;

    private String password;

    RedisInfo(Core core)
    {
        this.ip = core.getConfig("redis.ip");
        this.port = core.getConfig("redis.port");

        this.password = core.getConfig("redis.password");
    }

    String getIp()
    {
        return ip;
    }

    int getPort()
    {
        return port;
    }

    String getPassword()
    {
        // After password has been fetched once, prevent it from being done so again
        assert password != null : "getPassword() called a second time";

        String password = this.password;
        this.password = null;

        return password;
    }
}
