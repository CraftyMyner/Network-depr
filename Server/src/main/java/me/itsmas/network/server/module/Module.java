package me.itsmas.network.server.module;

import me.itsmas.network.api.Logs;
import me.itsmas.network.server.Core;
import me.itsmas.network.server.rank.Rank;
import me.itsmas.network.server.util.UtilServer;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

/**
 * Representation of a server module
 */
public abstract class Module implements Listener
{
    /**
     * The plugin instance
     */
    protected final Core core;

    /**
     * The name of this module
     */
    private final String name;

    /**
     * Module constructor
     *
     * @param core The plugin instance
     * @param name This module's name
     */
    public Module(Core core, String name)
    {
        this.core = core;
        this.name = name;

        log("Initialising...");

        core.getModuleManager().addModule(this);
        UtilServer.registerListener(this);

        onEnable();
    }

    /**
     * Method called when this module is instantiated
     */
    public void onEnable(){}

    /**
     * This module's submodules
     */
    private final Set<SubModule> subModules = new HashSet<>();

    /**
     * Adds a submodule to the set of submodules
     *
     * @see #subModules
     * @param subModule The submodule to add
     * @return The added submodule
     */
    @SuppressWarnings("unchecked")
    protected <T extends SubModule> T addSubModule(SubModule subModule)
    {
        UtilServer.registerListener(subModule);
        return (T) subModule;
    }

    /**
     * Registers a command class
     *
     * @param object The class containing commands
     */
    protected void registerCommands(Object object)
    {
        core.getCommandManager().registerCommands(object);
    }

    /**
     * Disables this module and all submodules
     */
    final void disable()
    {
        disableSubModules();
        onDisable();
    }

    /**
     * Disables this module's submodules
     */
    private void disableSubModules()
    {
        subModules.forEach(SubModule::onDisable);
    }

    /**
     * Method called on server shutdown
     */
    public void onDisable(){}

    /**
     * Logs a message to the console
     *
     * @param msg The message to log
     */
    protected void log(String msg)
    {
        Logs.log(name + " > " + msg);
    }

    /**
     * Logs a fatal error to the console
     *
     * @param msg The message to log
     */
    protected void logFatal(String msg)
    {
        Logs.logError(name + " > " + msg);

        UtilServer.broadcastInternal(msg, Rank.DEV, true);
    }
}
