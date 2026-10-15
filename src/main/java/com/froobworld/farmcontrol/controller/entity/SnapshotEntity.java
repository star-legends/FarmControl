package com.froobworld.farmcontrol.controller.entity;

import com.froobworld.farmcontrol.data.FcData;
import org.bukkit.DyeColor;
import org.bukkit.entity.*;
import org.bukkit.material.Colorable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SnapshotEntity {
    private final Entity entity;
    private final EntityType entityType;
    private final Class<? extends Entity> entityClass;
    private final Set<String> metadata;
    private final boolean pluginProtected;
    private final int entityId;
    private final Vector location;
    private final FcData fcData;
    private final boolean leashed;
    private final boolean loveMode;
    private final boolean customName;
    private final boolean tamed;
    private final boolean isPatrolLeader;
    private final int ticksLived;
    private final boolean pickupable;
    private final boolean mounted;
    private final List<Object> classifications = new ArrayList<>();

    public SnapshotEntity(Entity entity, List<String> metadataKeys, boolean pluginProtected) {
        this.entity = entity;
        this.entityType = entity.getType();
        this.entityClass = entity.getClass();
        this.metadata = metadataKeys.stream().filter(entity::hasMetadata).collect(Collectors.toUnmodifiableSet());
        this.pluginProtected = pluginProtected;
        this.entityId = entity.getEntityId();
        this.location = entity.getLocation().toVector();
        this.fcData = FcData.get(entity);
        this.leashed = entity instanceof Mob && ((Mob) entity).isLeashed();
        this.loveMode = entity instanceof Animals && ((Animals) entity).isLoveMode();
        this.customName = entity.getCustomName() != null;
        this.tamed = entity instanceof Tameable && ((Tameable) entity).isTamed();
        this.isPatrolLeader = entity instanceof Raider && ((Raider) entity).isPatrolLeader();
        this.pickupable = entity instanceof AbstractArrow && ((AbstractArrow) entity).getPickupStatus() == AbstractArrow.PickupStatus.ALLOWED;
        this.ticksLived = entity.getTicksLived();
        this.mounted = entity.isInsideVehicle() || !entity.getPassengers().isEmpty();
        classifications.add(entity.getType());
        if (entity instanceof Colorable) {
            DyeColor colour = ((Colorable) entity).getColor();
            if (colour != null) {
                classifications.add(colour);
            }
        }
        if (entity instanceof Villager) {
            classifications.add(((Villager) entity).getProfession());
        }
        if (entity instanceof Item) {
            classifications.add(((Item) entity).getItemStack().getType());
        }
        if (entity instanceof Boat boat) {
            classifications.add(boat.getBoatType());
        }
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    public int getEntityId() {
        return entityId;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Vector getLocation() {
        return location;
    }

    public FcData getFcData() {
        return fcData;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean hasMetadata(String key) {
        return metadata.contains(key);
    }

    public boolean isPluginProtected() {
        return pluginProtected;
    }

    public boolean isLeashed() {
        return leashed;
    }

    public boolean isLoveMode() {
        return loveMode;
    }

    public boolean hasCustomName() {
        return customName;
    }

    public boolean isTamed() {
        return tamed;
    }

    public boolean isPatrolLeader() {
        return isPatrolLeader;
    }

    public boolean isPickupable() {
        return pickupable;
    }

    public boolean isMounted() {
        return mounted;
    }

    public int getTicksLived() {
        return ticksLived;
    }

    public List<Object> getClassifications() {
        return classifications;
    }

}
