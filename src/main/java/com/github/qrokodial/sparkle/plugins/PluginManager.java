package com.github.qrokodial.sparkle.plugins;

import com.github.qrokodial.sparkle.utilities.io.properties.ReadablePropertiesFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PluginManager<P extends Plugin> {
    private Map<Class, P> pluginMap;

    /**
     * Instantiates the class.
     */
    public PluginManager() {
        pluginMap = new ConcurrentHashMap<>();
    }

    /**
     * @return a collection of all currently registered plugins
     */
    public Collection<P> getPlugins() {
        return pluginMap.values();
    }

    /**
     * Attempts to find the plugin matching the given class.
     *
     * @param pluginClass
     * @return the plugin, if a match was found
     */
    public Optional<P> getPlugin(Class<P> pluginClass) {
        return Optional.ofNullable(pluginMap.get(pluginClass));
    }

    /**
     * @param plugin
     * @return true if the provided plugin is registered, false otherwise
     */
    public boolean isRegistered(P plugin) {
        return pluginMap.containsKey(plugin.getClass());
    }

    /**
     * Registers the plugin.
     *
     * @param plugin
     */
    public void registerPlugin(P plugin) {
        pluginMap.put(plugin.getClass(), plugin);
    }

    /**
     * Attempts to register the plugin via a provided JAR file.
     *
     * @param pluginFile
     * @return the newly registered plugin, if successful
     */
    public Optional<P> registerPlugin(File pluginFile) throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
        try {
            URLClassLoader loader = new URLClassLoader(new URL[] { pluginFile.toURI().toURL() }, getClass().getClassLoader());

            ReadablePropertiesFile pluginProperties = new ReadablePropertiesFile(loader.getResourceAsStream("plugin.properties"));
            if (!pluginProperties.hasKey("entrypoint")) {
                throw new ClassNotFoundException("No entrypoint specified in plugin properties");
            }

            Class<?> entryClass = Class.forName(pluginProperties.getString("entrypoint").get(), true, loader);

            if (!Plugin.class.isAssignableFrom(entryClass)) {
                throw new ClassCastException("Plugin entrypoint must be of type Plugin");
            }

            P plugin = (P)entryClass.newInstance();
            plugin._initialize(pluginProperties);
            plugin.onLoad();

            return Optional.of(plugin);
        } catch (MalformedURLException thisShouldNeverHappen) {}

        return Optional.empty();
    }

    /**
     * Attempts to remove a plugin.
     *
     * @param plugin
     */
    public void removePlugin(P plugin) {
        pluginMap.remove(plugin.getClass());
    }
}
