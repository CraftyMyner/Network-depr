package me.itsmas.network.server.rank;

import com.google.gson.JsonObject;
import me.itsmas.network.server.task.NetworkTask;
import me.itsmas.network.server.user.User;

import java.util.UUID;

/**
 * The task for updating a user's rank
 */
public class UpdateRankTask extends NetworkTask
{
    public UpdateRankTask(User user, Rank rank, String prefix, String executorName, UUID executorId)
    {
        this(user);

        this.rank = rank;
        this.prefix = prefix;
        this.executorName = executorName;
        this.executorId = executorId.toString();
    }

    private UpdateRankTask(User user)
    {
        super(user);
    }

    private Rank rank;
    private String prefix;
    private String executorName;
    private String executorId;

    @Override
    public void executeOnline(User user)
    {
        executeOffline(user);
        user.sendMessage("user.rank_updated", user.getFormattedRank());
    }

    @Override
    public void executeOffline(User user)
    {
        user.setRank(rank, prefix);
        user.addLog("Rank updated to %s [%s] by %s [%s]", rank.getName(), prefix, executorName, executorId);
    }

    @Override
    public void parseArguments(JsonObject object)
    {
        rank = Rank.valueOf(object.get("rank").getAsString());
        prefix = object.get("prefix").getAsString();
        executorName = object.get("executorName").getAsString();
        executorId = object.get("executorId").getAsString();
    }

    @Override
    public String getType()
    {
        return "UpdateRank";
    }
}
