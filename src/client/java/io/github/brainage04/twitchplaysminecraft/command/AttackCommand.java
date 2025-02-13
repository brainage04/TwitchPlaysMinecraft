package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.MathUtils;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.Box;

import java.util.List;

import static io.github.brainage04.twitchplaysminecraft.util.PlayerUtils.getPlayerReach;

public class AttackCommand {
    private static boolean isRunning = false;
    private static LivingEntity target = null;

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (target == null) return;
            if (client.player == null) return;

            client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getPos());

            if (client.player.getItemCooldownManager().isCoolingDown(client.player.getMainHandStack())) return;
            if (!target.canTakeDamage()) return;
            if (target.isDead()) {
                isRunning = false;
                target = null;
                new ClientFeedbackBuilder().source(client)
                        .text("Mob has died.")
                        .messageType(MessageType.SUCCESS)
                        .execute();
                return;
            }

            if (!(target instanceof PassiveEntity)) {
                double distanceSquared = client.player.squaredDistanceTo(target);

                boolean forward = false;
                boolean back = false;

                if (distanceSquared < getPlayerReach(client.player) - 1) {
                    forward = true;
                } else {
                    back = true;
                }

                if (client.options.forwardKey.isPressed() != forward) {
                    new KeyBindingBuilder().keys(client.options.forwardKey)
                            .pressed(forward)
                            .execute();
                }
                if (client.options.backKey.isPressed() != back) {
                    new KeyBindingBuilder().keys(client.options.backKey)
                            .pressed(back)
                            .execute();
                }
            }

            client.player.attack(target);
        });
    }

    // todo: needs testing
    public static int execute(FabricClientCommandSource source) {
        ClientPlayerEntity player = source.getPlayer();

        // find nearest mobs (within 16 blocks)
        List<LivingEntity> nearbyLivingEntities = player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(
                        player.getX() + 16,
                        player.getY() + 16,
                        player.getZ() + 16,
                        player.getX() - 16,
                        player.getY() - 16,
                        player.getZ() - 16
                ),
                livingEntity -> true
        );

        if (nearbyLivingEntities.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .text("There are no living entities within 16 blocks!")
                    .messageType(MessageType.ERROR)
                    .execute();
            return 0;
        }

        // if mobs exist, find the closest one
        target = nearbyLivingEntities.getFirst();
        double distanceSquared = MathUtils.distanceToSquared(nearbyLivingEntities.getFirst().getPos(), target.getPos());
        for (int i = 1; i < nearbyLivingEntities.size(); i++) {
            LivingEntity tempEntity = nearbyLivingEntities.get(i);
            double tempDistanceSquared = MathUtils.distanceToSquared(tempEntity.getPos(), target.getPos());
            if (tempDistanceSquared < distanceSquared) {
                target = tempEntity;
            }
        }

        // switch to best weapon
        // priority list: sword, axe, pickaxe, shovel

        // attack entity until it is dead
            // attack each time weapon cooldown reaches 0
            // if mob is passive, hold W
            // if mob is neutral or hostile, try to maintain a distance of 3 blocks

        if (target instanceof PassiveEntity) {
            new KeyBindingBuilder().source(source).keys(source.getClient().options.forwardKey).execute();
        }

        isRunning = true;

        return 1;
    }
}
