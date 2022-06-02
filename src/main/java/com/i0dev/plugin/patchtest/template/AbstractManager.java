package com.i0dev.plugin.patchtest.template;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Listener;

@Getter
@Setter
public abstract class AbstractManager implements Listener {

    public boolean loaded = false;
    public boolean listener = false;

    /**
     * This method is called when the manager gets registered in the main class.
     */
    public void initialize() {

    }

    /**
     * This method gets called during shutdown of the plugin.
     */
    public void deinitialize() {

    }
}
