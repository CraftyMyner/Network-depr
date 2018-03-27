package me.itsmas.network.server.task;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.module.Module;
import me.itsmas.network.server.network.NetworkPacket;
import me.itsmas.network.server.user.User;
import me.itsmas.network.server.util.UtilServer;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Handles cross-server tasks
 */
public class TaskManager extends Module
{
    public TaskManager(Core core)
    {
        super(core, "Tasks");

        UtilServer.runRepeating(this::processNext, 20L, 2L);
    }

    /**
     * The queue of tasks to process
     */
    private final Queue<NetworkTask> taskQueue = new PriorityQueue<>();

    /**
     * Processes the next {@link NetworkTask} in the task queue if available
     *
     * @see #taskQueue
     */
    private void processNext()
    {
        if (taskQueue.size() > 0)
        {
            processTask(taskQueue.remove());
        }
    }

    /**
     * Processes a {@link NetworkTask}
     *
     * @param task The task
     */
    private void processTask(NetworkTask task)
    {
        User user = task.getUser();

        if (user.isOnline())
        {
            task.executeOnline(user);
            return;
        }

        core.getNetwork().getServer(user.getName(), server ->
        {
            if (server == null)
            {
                task.executeOffline(user);
                user.save(true);
            }
            else
            {
                NetworkPacket packet = task.toPacket();

                core.getNetwork().sendPacket(packet, server);
            }
        });
    }

    /**
     * Adds a {@link NetworkTask} to the task queue
     *
     * @see #taskQueue
     * @param task The task
     */
    public void scheduleTask(NetworkTask task)
    {
        taskQueue.add(task);
    }
}
