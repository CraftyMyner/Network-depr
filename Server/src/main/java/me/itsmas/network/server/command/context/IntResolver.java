package me.itsmas.network.server.command.context;

import me.itsmas.network.server.user.User;

public class IntResolver implements ContextResolver<Integer>
{
    @Override
    public Integer resolve(String input)
    {
        try
        {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }

    @Override
    public void sendError(User user, String input)
    {
        user.sendMessage("command.invalid_number", input);
    }
}
