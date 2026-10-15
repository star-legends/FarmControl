package com.froobworld.farmcontrol.controller.entity;

import com.froobworld.farmcontrol.FarmControl;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SnapshotEntityTest {
    @Test
    public void capturedTypeAndMetadataDoNotReadLiveEntityFromWorker() {
        try (MockedStatic<JavaPlugin> plugins = mockStatic(JavaPlugin.class)) {
            FarmControl plugin = mock(FarmControl.class);
            when(plugin.getName()).thenReturn("FarmControl");
            when(plugin.namespace()).thenReturn("farmcontrol");
            plugins.when(() -> JavaPlugin.getPlugin(FarmControl.class)).thenReturn(plugin);
            Entity entity = mock(Entity.class);
            when(entity.getType()).thenReturn(EntityType.COW);
            when(entity.getLocation()).thenReturn(new Location(null, 1, 2, 3));
            when(entity.getPersistentDataContainer()).thenReturn(mock(PersistentDataContainer.class));
            when(entity.hasMetadata("NPC")).thenReturn(true);
            when(entity.isInsideVehicle()).thenReturn(true);
            SnapshotEntity snapshot = new SnapshotEntity(entity, List.of("NPC", "other"), true);
            clearInvocations(entity);
            assertEquals(EntityType.COW, snapshot.getEntityType());
            assertEquals(entity.getClass(), snapshot.getEntityClass());
            assertTrue(snapshot.hasMetadata("NPC"));
            assertFalse(snapshot.hasMetadata("other"));
            assertTrue(snapshot.isPluginProtected());
            assertTrue(snapshot.isMounted());
            verifyNoInteractions(entity);
        }
    }
}
