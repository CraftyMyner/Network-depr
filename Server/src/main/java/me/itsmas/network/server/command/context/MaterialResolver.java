package me.itsmas.network.server.command.context;

import me.itsmas.network.server.user.User;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Material;

public class MaterialResolver implements ContextResolver<Material>
{
    @Override
    public Material resolve(String input)
    {
        return EnumUtils.getEnum(Material.class, input.toUpperCase());
    }

    @Override
    public void sendError(User user, String input)
    {
        user.sendMessage("command;invalid_material");
    }
}
