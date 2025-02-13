package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;

public class PlayerUtils {
    public static double getPlayerReach(ClientPlayerEntity player) {
        double reach = player.getAttributeValue(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (reach != 0) return reach;
        if (player.isCreative()) return 3.5;
        return 3.0;
    }
}
