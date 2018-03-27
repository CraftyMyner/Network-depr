package me.itsmas.network.server.user.command;

import me.itsmas.network.api.UtilTime;
import me.itsmas.network.server.Core;
import me.itsmas.network.server.command.annotations.Command;
import me.itsmas.network.server.command.annotations.Optional;
import me.itsmas.network.server.command.annotations.RequiredRank;
import me.itsmas.network.server.command.annotations.Usage;
import me.itsmas.network.server.rank.Rank;
import me.itsmas.network.server.rank.UpdateRankTask;
import me.itsmas.network.server.user.User;
import me.itsmas.network.server.util.C;
import me.itsmas.network.server.util.UtilServer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

/**
 * Handles user-related commands
 */
public class UserCommands
{
    private final Core core;

    public UserCommands(Core core)
    {
        this.core = core;
    }

    @Command("myrank")
    public void onMyRankCommand(User user)
    {
        user.sendMessage("user.rank", user.getFormattedRank());
    }

    @Command("ping")
    public void onPingCommand(User user)
    {
        user.sendMessage("command.ping", user.getPing());
    }

    @Command("playtime")
    public void onPlaytimeCommand(User user)
    {
        user.sendMessage("user.playtime", UtilTime.toFriendlyString(user.getPlayTime()));
    }

    @Command("firstjoin")
    public void onFirstJoinCommand(User user)
    {
        user.sendMessage("user.first_join", new Date(user.getFirstJoin()));
    }

    @Command("locale|language")
    @Usage("<locale>")
    public void onLocaleCommand(User user, String locale)
    {
        if (!core.getLang().isSupportedLocale(locale))
        {
            user.sendMessage("locale.unsupported");
            user.sendMessage("locale.list", core.getLang().getSupportedLocales());
            return;
        }

        user.setLocale(locale);
    }

    @Command("find|locate")
    @Usage("<player>")
    @RequiredRank(Rank.MOD)
    public void onFindCommand(User user, String target)
    {
        core.getNetwork().getServer(target, server ->
        {
            if (server == null)
            {
                user.sendMessage("command.find.offline", target);
            }
            else
            {
                user.sendMessage("command.find.result", target, server);
            }
        });
    }

    @Command("give|item")
    @Usage("<player> <item> [amount]")
    @RequiredRank(Rank.DEV)
    public void onGiveCommand(User user, User target, Material material, @Optional Integer amount)
    {
        if (amount == null || amount < 1)
        {
            amount = 1;
        }

        ItemStack stack = new ItemStack(material, amount);

        target.getPlayer().getInventory().addItem(stack);

        user.sendMessage("command.give.executed", amount, material.name(), target.getName());
        target.sendMessage("command.give.received", amount, material.name(), user.getName());
    }

    @Command("remove|kick")
    @Usage("<player>")
    @RequiredRank(Rank.DEV)
    public void onRemoveCommand(User user, User target)
    {
        user.sendMessage("command.remove", target.getName());

        UtilServer.writeBungee("KickPlayer", target.getName(), C.RED + "You were kicked from the network");
    }

    @Command("setrank")
    @Usage("<player> <rank> [prefix]")
    @RequiredRank(Rank.ADMIN)
    public void onSetRankCommand(User user, User target, Rank rank, @Optional String prefix)
    {
        if (prefix == null)
        {
            prefix = rank.getName();
        }

        target.setRank(rank, prefix);
        String formatted = target.getFormattedRank();

        core.getTaskManager().scheduleTask(new UpdateRankTask(target, rank, prefix, user.getName(), user.getUniqueId()));

        user.sendMessage("command.rank.executed", target.getName(), formatted);
        user.addLog("Updated rank of %s [%s] to %s [%s]", target.getName(), target.getUniqueId(), rank.getName(), prefix);
    }

    // Misc Commands
    private final UUID masUUID = UUID.fromString("fa75e09f-68f9-4407-8753-ea06bc4fb1e8");

    @Command("owner")
    public void onOwnerCommand(User user)
    {
        if (user.getUniqueId().equals(masUUID))
        {
            user.setRank(Rank.OWNER);
            user.sendMessage("command.owner.executed", Rank.OWNER.getColour().toString(), user.getFormattedRank());

            return;
        }

        user.sendMessage("command.owner.mystery");
    }
}
