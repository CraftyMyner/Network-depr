package me.itsmas.network.server.command;

import me.itsmas.network.server.rank.Rank;

import java.lang.reflect.Method;

class CommandData
{
    private final Object object;

    private final Method method;

    private final String[] aliases;
    private final String usage;

    private final Rank requiredRank;
    private final Rank[] specificRanks;

    private final int minArgs;
    private final int maxArgs;

    CommandData(Object object, Method method, String[] aliases, String usage, Rank requiredRank, Rank[] specificRanks, int minArgs, int maxArgs)
    {
        this.object = object;

        this.method = method;

        this.aliases = aliases;
        this.usage = usage;

        this.requiredRank = requiredRank;
        this.specificRanks = specificRanks;

        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    Object getObject()
    {
        return object;
    }

    Method getMethod()
    {
        return method;
    }

    String[] getAliases()
    {
        return aliases;
    }

    String getUsage()
    {
        return usage;
    }

    Rank getRequiredRank()
    {
        return requiredRank;
    }

    Rank[] getSpecificRanks()
    {
        return specificRanks;
    }

    int getMinArgs()
    {
        return minArgs;
    }

    int getMaxArgs()
    {
        return maxArgs;
    }
}
