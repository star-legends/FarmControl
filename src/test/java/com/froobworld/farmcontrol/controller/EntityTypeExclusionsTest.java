package com.froobworld.farmcontrol.controller;

import org.bukkit.entity.EntityType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class EntityTypeExclusionsTest {
    @Test
    public void oldNamesProtectEveryModernChestBoatAndTheChestMinecart() {
        List<String> unknown = new ArrayList<>();
        Set<EntityType> types = EntityTypeExclusions.resolve(List.of("chest_boat", "minecart_chest", "trident"), unknown::add);
        for (EntityType type : EntityType.values()) {
            if (type.name().endsWith("_CHEST_BOAT") || type == EntityType.BAMBOO_CHEST_RAFT) {
                assertTrue("Missing protection for " + type, types.contains(type));
            }
        }
        assertTrue(types.contains(EntityType.CHEST_MINECART));
        assertTrue(types.contains(EntityType.TRIDENT));
        assertFalse(types.contains(EntityType.OAK_BOAT));
        assertTrue(unknown.isEmpty());
    }

    @Test
    public void supportsNamespacedTypesAndReportsMistakes() {
        List<String> unknown = new ArrayList<>();
        Set<EntityType> types = EntityTypeExclusions.resolve(List.of(" minecraft:oak_chest_boat ", "CHEST_MINECART", "typo_cow"), unknown::add);
        assertEquals(Set.of(EntityType.OAK_CHEST_BOAT, EntityType.CHEST_MINECART), types);
        assertEquals(List.of("typo_cow"), unknown);
    }
}
