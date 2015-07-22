package com.github.qrokodial.sparkle.plugins;

import com.github.qrokodial.sparkle.utilities.io.properties.ReadablePropertiesFile;

public abstract class Plugin {
    private ReadablePropertiesFile pluginProperties;

    /**
     * @return the 'plugin.properties' file located in the plugin's JAR file
     */
    public final ReadablePropertiesFile getPluginProperties() {
        return pluginProperties;
    }

    /**
     * Used by the plugin manager to initialize the plugin without the need for a constructor.
     *
     * @param pluginProperties
     */
    protected final void _initialize(ReadablePropertiesFile pluginProperties) {
        if (getPluginProperties() == null) {
            this.pluginProperties = pluginProperties;
        }
    }

    /**
     * Called by the plugin manager when the plugin has been loaded.
     */
    public abstract void onLoad();
}