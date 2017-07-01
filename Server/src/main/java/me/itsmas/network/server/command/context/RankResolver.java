package me.itsmas.network.server.command.context;

import me.itsmas.network.server.rank.Rank;
import me.itsmas.network.server.user.User;
import org.apache.commons.lang3.EnumUtils;

public class RankResolver implements ContextResolver<Rank>
{
    @Override
    public Rank resolve(String input)
    {
        return EnumUtils.getEnum(Rank.class, input.toUpperCase());
    }

    @Override
    public void sendError(User user, String input)
    {
        user.sendMessage("command;invalid_rank");
    }
}
