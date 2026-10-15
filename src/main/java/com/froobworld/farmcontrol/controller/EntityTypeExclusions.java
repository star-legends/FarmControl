package com.froobworld.farmcontrol.controller;

import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

/** Resolves old configuration names without rewriting user configuration. */
public final class EntityTypeExclusions {
    private EntityTypeExclusions() {}

    public static Set<EntityType> resolve(List<String> names, Consumer<String> unknownType) {
        Set<EntityType> types = EnumSet.noneOf(EntityType.class);
        for (String name : names) {
            String normalized = name.trim().toUpperCase(Locale.ROOT);
            if (normalized.startsWith("MINECRAFT:")) {
                normalized = normalized.substring("MINECRAFT:".length());
            }
            if (normalized.equals("CHEST_BOAT")) {
                for (EntityType type : EntityType.values()) {
                    if (type.name().endsWith("_CHEST_BOAT") || type.name().equals("BAMBOO_CHEST_RAFT")) {
                        types.add(type);
                    }
                }
            } else {
                if (normalized.equals("MINECART_CHEST")) {
                    normalized = "CHEST_MINECART";
                }
                try {
                    types.add(EntityType.valueOf(normalized));
                } catch (IllegalArgumentException ex) {
                    unknownType.accept(name);
                }
            }
        }
        return Set.copyOf(types);
    }
}
