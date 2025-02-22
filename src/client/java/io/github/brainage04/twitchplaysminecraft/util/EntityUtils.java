package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.List;

public class EntityUtils {
    // the type that the List contains should be equal to the Class paramater
    public static List<LivingEntity> getLivingEntities(PlayerEntity player, int radius) {
        return player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(
                        player.getX() + radius,
                        player.getY() + radius,
                        player.getZ() + radius,
                        player.getX() - radius,
                        player.getY() - radius,
                        player.getZ() - radius
                ),
                livingEntity -> livingEntity.getUuid() != player.getUuid()
        );
    }
}
