package com.froobworld.farmcontrol.hook;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/** Optional public API adapters. Call only from the entity's owning thread. */
public final class CustomMobProtection {
    private final List<Provider> providers = new ArrayList<>();

    public CustomMobProtection(PluginManager plugins, Logger logger) {
        for (String name : List.of("MythicMobs", "ModelEngine")) {
            Plugin plugin = plugins.getPlugin(name);
            if (plugin == null || !plugin.isEnabled()) {
                continue;
            }
            Provider provider = new Provider(plugin, logger);
            providers.add(provider);
            try {
                if (name.equals("MythicMobs")) {
                    Object manager = plugin.getClass().getMethod("getMobManager").invoke(plugin);
                    provider.bind(manager, manager.getClass().getMethod("isActiveMob", UUID.class));
                } else {
                    Class<?> api = Class.forName("com.ticxo.modelengine.api.ModelEngineAPI", true,
                            plugin.getClass().getClassLoader());
                    provider.bind(null, api.getMethod("isModeledEntity", UUID.class));
                }
                logger.info("Protecting custom entities from " + name + ".");
            } catch (ReflectiveOperationException | LinkageError | RuntimeException ex) {
                provider.fail(ex);
            }
        }
    }

    public boolean isProtected(Entity entity) {
        if (providers.isEmpty()) {
            return false;
        }
        UUID uuid = entity.getUniqueId();
        for (Provider provider : providers) {
            if (provider.isProtected(uuid)) {
                return true;
            }
        }
        return false;
    }

    static final class Provider {
        private final Plugin plugin;
        private final Logger logger;
        private final AtomicBoolean failed = new AtomicBoolean();
        private Object target;
        private Method method;

        Provider(Plugin plugin, Logger logger) {
            this.plugin = plugin;
            this.logger = logger;
        }

        void bind(Object target, Method method) {
            if (method.getReturnType() != boolean.class) {
                throw new IllegalArgumentException("Expected a boolean public API method");
            }
            this.target = target;
            this.method = method;
        }

        boolean isProtected(UUID uuid) {
            if (!plugin.isEnabled()) {
                return false;
            }
            if (failed.get()) {
                return true;
            }
            try {
                return (boolean) method.invoke(target, uuid);
            } catch (ReflectiveOperationException | LinkageError | RuntimeException ex) {
                fail(ex);
                return true;
            }
        }

        void fail(Throwable cause) {
            if (failed.compareAndSet(false, true)) {
                logger.severe("Cannot query " + plugin.getName() + " entity protection (" + cause
                        + "). FarmControl will skip new actions on ALL entities while this plugin is enabled; "
                        + "check API compatibility and restart. Existing actions can still be undone.");
            }
        }
    }
}
