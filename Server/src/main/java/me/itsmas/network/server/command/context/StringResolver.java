package me.itsmas.network.server.command.context;

import me.itsmas.network.server.user.User;

public class StringResolver implements ContextResolver<String>
{
    @Override
    public String resolve(String input)
    {
        return input;
    }

    @Override
    public void sendError(User user, String input)
    {

    }
}
