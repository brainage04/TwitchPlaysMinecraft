package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.UUID;

public class EntityUtils {
    public static <T extends Entity> List<T> getEntities(Class<T> entityClass, ClientWorld world, Vec3d center, int radius, UUID playerUuid) {
        return world.getEntitiesByClass(
                entityClass,
                new Box(
                        center.getX() + radius,
                        center.getY() + radius,
                        center.getZ() + radius,
                        center.getX() - radius,
                        center.getY() - radius,
                        center.getZ() - radius
                ),
                entity -> !entity.getUuid().equals(playerUuid)
        );
    }

    public static <T extends Entity> List<T> getEntities(EntityType<T> entityType, ClientWorld world, Vec3d center, int radius, UUID playerUuid) {
        return world.getEntitiesByType(
                entityType,
                new Box(
                        center.getX() + radius,
                        center.getY() + radius,
                        center.getZ() + radius,
                        center.getX() - radius,
                        center.getY() - radius,
                        center.getZ() - radius
                ),
                entity -> !entity.getUuid().equals(playerUuid)
        );
    }
}
