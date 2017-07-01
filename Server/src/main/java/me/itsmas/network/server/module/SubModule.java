package me.itsmas.network.server.module;

import me.itsmas.network.server.Core;
import org.bukkit.event.Listener;

/**
 * A child of a {@link Module}
 */
public abstract class SubModule<T extends Module> implements Listener
{
    /**
     * The plugin instance
     */
    protected final Core core;

    /**
     * The base module
     */
    protected final T module;

    /**
     * The name of this submodule
     */
    private final String name = getClass().getSimpleName();

    /**
     * Submodule constructor
     * @param module The base module
     */
    public SubModule(T module)
    {
        this.core = module.core;
        this.module = module;
    }

    /**
     * Method called on server shutdown
     */
    void onDisable(){}

    /**
     * @see Module#log(String)
     */
    protected void log(String msg)
    {
        module.log("[" + name + "] " + msg);
    }

    /**
     * @see Module#logFatal(String)
     */
    protected void logFatal(String msg)
    {
        module.logFatal("[" + name + "] " + msg);
    }
}

