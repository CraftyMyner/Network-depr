package me.itsmas.network.server.module;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles all {@link Module} instances
 */
public class ModuleManager
{
    /**
     * The registered modules
     */
    private final Set<Module> modules = new HashSet<>();

    /**
     * Adds a module to the modules set
     * @see #modules
     * @param module The module to add
     */
    void addModule(Module module)
    {
        modules.add(module);
    }

    /**
     * Disables all active modules
     */
    public void onDisable()
    {
        modules.forEach(Module::disable);
    }
}

