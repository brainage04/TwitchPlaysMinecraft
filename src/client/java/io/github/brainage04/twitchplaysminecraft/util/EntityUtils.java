package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityUtils {
    // todo: either finish this map or replace it with something better
    //  it's supposed to be a more efficient way of getting LivingEntity classes from strings
    //  (e.g. in the attack command)
    private static final Map<String, Class<? extends LivingEntity>> entityMap = Map.ofEntries(

    );

    public static <T extends Entity> List<T> getEntities(Class<T> entityClass, PlayerEntity player, int radius) {
        return player.getWorld().getEntitiesByClass(
                entityClass,
                new Box(
                        player.getX() + radius,
                        player.getY() + radius,
                        player.getZ() + radius,
                        player.getX() - radius,
                        player.getY() - radius,
                        player.getZ() - radius
                ),
                entity -> entity.getUuid() != player.getUuid()
        );
    }

    public static <T extends Entity> List<T> getEntities(Class<T> entityClass, ClientWorld world) {
        Iterable<Entity> entities = world.getEntities();
        List<T> desiredEntities = new ArrayList<>();

        for (Entity entity : entities) {
            if (entity.getClass().isAssignableFrom(entityClass)) {
                desiredEntities.add((T) entity);
            }
        }

        return desiredEntities;
    }
}
