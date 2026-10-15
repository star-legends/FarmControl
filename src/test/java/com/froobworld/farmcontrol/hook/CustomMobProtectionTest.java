package com.froobworld.farmcontrol.hook;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.Test;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CustomMobProtectionTest {
    public static class Registry {
        public UUID protectedId;
        public boolean broken;
        public boolean isActiveMob(UUID uuid) {
            if (broken) throw new IllegalStateException("API failure");
            return uuid.equals(protectedId);
        }
    }

    @Test
    public void noOptionalPluginsDoesNotExcludeVanillaEntities() {
        CustomMobProtection protection = new CustomMobProtection(mock(PluginManager.class), mock(Logger.class));
        assertFalse(protection.isProtected(mock(Entity.class)));
    }

    @Test
    public void publicApiChecksUuidAndFailureNeverExposesCustomMobs() throws Exception {
        Plugin plugin = mock(Plugin.class);
        Logger logger = mock(Logger.class);
        when(plugin.isEnabled()).thenReturn(true);
        when(plugin.getName()).thenReturn("MythicMobs");
        Registry registry = new Registry();
        registry.protectedId = UUID.randomUUID();
        CustomMobProtection.Provider provider = new CustomMobProtection.Provider(plugin, logger);
        provider.bind(registry, Registry.class.getMethod("isActiveMob", UUID.class));
        assertTrue(provider.isProtected(registry.protectedId));
        assertFalse(provider.isProtected(UUID.randomUUID()));
        registry.broken = true;
        assertTrue(provider.isProtected(UUID.randomUUID()));
        assertTrue(provider.isProtected(UUID.randomUUID()));
        verify(logger, times(1)).severe(anyString());
        when(plugin.isEnabled()).thenReturn(false);
        assertFalse(provider.isProtected(registry.protectedId));
    }
}
