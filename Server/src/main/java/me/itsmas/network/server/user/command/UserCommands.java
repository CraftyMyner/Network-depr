package me.itsmas.network.server.user.command;

import me.itsmas.network.api.UtilTime;
import me.itsmas.network.server.Core;
import me.itsmas.network.server.command.annotations.Command;
import me.itsmas.network.server.command.annotations.Optional;
import me.itsmas.network.server.command.annotations.RequiredRank;
import me.itsmas.network.server.command.annotations.Usage;
import me.itsmas.network.server.rank.Rank;
import me.itsmas.network.server.user.User;

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
        user.sendMessage("user;rank", user.getFormattedRank());
    }

    @Command("ping")
    public void onPingCommand(User user)
    {
        user.sendMessage("command;ping", user.getPing());
    }

    @Command("playtime")
    public void onPlaytimeCommand(User user)
    {
        user.updatePlayTime();

        user.sendMessage("user;playtime", UtilTime.toFriendlyString(user.getPlayTime()));
    }

    @Command("firstjoin")
    public void onFirstJoinCommand(User user)
    {
        user.sendMessage("user;first_join", new Date(user.getFirstJoin()));
    }

    @Command("locale|language")
    @Usage("<locale>")
    public void onLocaleCommand(User user, String locale)
    {
        if (!core.getLang().isSupportedLocale(locale))
        {
            user.sendMessage("locale;unsupported");
            user.sendMessage("locale;list", core.getLang().getSupportedLocales());
            return;
        }

        user.setLocale(locale);
    }

    @Command("setrank")
    @Usage("<player> <rank> [prefix]")
    @RequiredRank(Rank.ADMIN)
    public void onSetRankCommand(User user, User target, Rank rank, @Optional String prefix)
    {
        target.setRank(rank, prefix == null ? rank.getName() : prefix);

        String formatted = target.getFormattedRank();

        user.sendMessage("updated_rank", target.getName(), formatted);
        target.sendMessage("rank_updated", formatted);
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
                user.sendMessage("command;find_offline", target);
            }
            else
            {
                user.sendMessage("command;find_result", target, server);
            }
        });
    }

    // Misc Commands
    private final UUID masUUID = UUID.fromString("fa75e09f-68f9-4407-8753-ea06bc4fb1e8");

    @Command("owner")
    public void onOwnerCommand(User user)
    {
        if (user.getUniqueId().equals(masUUID))
        {
            user.setRank(Rank.OWNER);
            user.sendMessage("command;owner", user.getFormattedRank());

            return;
        }

        user.sendMessage("command;owner_mystery");
    }
}
