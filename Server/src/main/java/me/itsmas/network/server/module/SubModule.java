package me.itsmas.network.server.module;

import me.itsmas.network.server.Core;
import me.itsmas.network.server.util.UtilServer;
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

        UtilServer.registerListener(this);
    }

    /**
     * Method called on server shutdown
     */
    void onDisable(){}

    /**
     * @see Module#log(String, Object...)
     */
    protected void log(String msg, Object... params)
    {
        module.log("[" + name + "] " + String.format(msg, params));
    }

    /**
     * @see Module#logFatal(String, Object...)
     */
    protected void logFatal(String msg, Object... params)
    {
        module.logFatal("[" + name + "] " + String.format(msg, params));
    }
}

