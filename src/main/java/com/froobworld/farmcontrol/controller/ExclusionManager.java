package com.froobworld.farmcontrol.controller;

import com.froobworld.farmcontrol.FarmControl;
import com.froobworld.farmcontrol.config.FcConfig;
import com.froobworld.farmcontrol.controller.entity.SnapshotEntity;
import com.froobworld.farmcontrol.hook.CustomMobProtection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class ExclusionManager {
    private final FarmControl farmControl;
    private final CustomMobProtection customMobProtection;
    private final Set<String> warnedTypes = ConcurrentHashMap.newKeySet();

    public ExclusionManager(FarmControl farmControl) {
        this.farmControl = farmControl;
        this.customMobProtection = new CustomMobProtection(Bukkit.getPluginManager(), farmControl.getLogger());
    }

    public SnapshotEntity createSnapshot(Entity entity) {
        List<String> metadataKeys = farmControl.getFcConfig().worldSettings.of(entity.getWorld()).exclusionSettings.metadata.get();
        return new SnapshotEntity(entity, metadataKeys, isCustomMob(entity));
    }

    public boolean isCustomMob(Entity entity) {
        return customMobProtection.isProtected(entity);
    }

    public Predicate<SnapshotEntity> getExclusionPredicate(World world) {
        FcConfig.WorldSettings.ExclusionSettings exclusionSettings = farmControl.getFcConfig().worldSettings.of(world).exclusionSettings;
        boolean excludeLeashed = exclusionSettings.leashed.get();
        boolean excludeLoveMode = exclusionSettings.loveMode.get();
        List<String> excludeMeta = exclusionSettings.metadata.get();
        boolean excludeNamed = exclusionSettings.named.get();
        boolean excludeTamed = exclusionSettings.tamed.get();
        boolean excludePatrolLeaders = exclusionSettings.patrolLeader.get();
        Set<EntityType> excludeType = EntityTypeExclusions.resolve(exclusionSettings.type.get(), type -> {
            if (warnedTypes.add(type)) {
                farmControl.getLogger().warning("Unknown entity type in exclusion-settings.type: '" + type + "'");
            }
        });
        long excludeTicksLived = exclusionSettings.youngerThan.get();
        boolean excludePickupable = exclusionSettings.pickupable.get();
        boolean excludeMounted = exclusionSettings.mounted.get();
        return snapshotEntity -> {
            if (snapshotEntity.isPluginProtected()) {
                return true;
            }
            if (excludeLeashed && snapshotEntity.isLeashed()) {
                return true;
            }
            if (excludeLoveMode && snapshotEntity.isLoveMode()) {
                return true;
            }
            if (excludeNamed && snapshotEntity.hasCustomName()) {
                return true;
            }
            if (excludeTamed && snapshotEntity.isTamed()) {
                return true;
            }
            if (excludePatrolLeaders && snapshotEntity.isPatrolLeader()) {
                return true;
            }
            if (excludePickupable && snapshotEntity.isPickupable()) {
                return true;
            }
            if (excludeMounted && snapshotEntity.isMounted()) {
                return true;
            }
            if (snapshotEntity.getTicksLived() < excludeTicksLived) {
                return true;
            }
            for (String meta : excludeMeta) {
                if (snapshotEntity.hasMetadata(meta)) {
                    return true;
                }
            }
            return excludeType.contains(snapshotEntity.getEntityType());
        };
    }

}
