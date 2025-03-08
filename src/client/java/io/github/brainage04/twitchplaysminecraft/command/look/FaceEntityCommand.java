package io.github.brainage04.twitchplaysminecraft.command.look;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.EntityUtils;
import io.github.brainage04.twitchplaysminecraft.util.MathUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;

public class FaceEntityCommand {
    public static int execute(FabricClientCommandSource source, String entityString) {
        entityString = entityString.toLowerCase();
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(Identifier.of(entityString));
        if (entityType == EntityType.PIG && !entityString.equals("pig")) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No such entity \"%s\" exists!".formatted(entityString))
                    .execute();
            return 0;
        }

        // find nearest mobs (within 20 blocks)
        int radius = 20;
        List<LivingEntity> nearbyLivingEntities = EntityUtils.getEntities(LivingEntity.class, source.getPlayer(), radius)
                .stream().filter(entity -> entity.getType() == entityType).toList();

        if (nearbyLivingEntities.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .text("There are no %ss within %d blocks!".formatted(entityString, radius))
                    .messageType(MessageType.ERROR)
                    .execute();

            return 0;
        }

        // if mobs exist, find the closest one
        LivingEntity target = nearbyLivingEntities.stream()
                .min(Comparator.comparingDouble(e -> MathUtils.distanceToSquared(e.getPos(), source.getPlayer().getPos())))
                .orElse(nearbyLivingEntities.getFirst());

        source.getPlayer().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getPos());

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text(Text.literal("Now looking at ")
                        .append(entityType.getName())
                        .append("."))
                .execute();

        return 1;
    }
}
