package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.EntityUtils;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.MathUtils;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class AttackCommand {
    private static boolean isRunning = false;
    private static int ticksSinceLastAttack = 0;
    private static LivingEntity target = null;

    private static void stop() {
        isRunning = false;
        ticksSinceLastAttack = 0;
        target = null;
    }

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (target == null) return;
            if (client.player == null) return;

            ticksSinceLastAttack++;
            if (ticksSinceLastAttack > 300) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.ERROR)
                        .text("No hits landed for 15 seconds! Cancelling attack...")
                        .execute();

                stop();

                return;
            }

            client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());

            if (target.isDead()) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.SUCCESS)
                        .text("Mob has died.")
                        .execute();

                ReleaseAllKeysCommand.execute(SourceUtils.getSourceFromClient(client));

                stop();

                return;
            }

            double distanceSquared = client.player.squaredDistanceTo(target);
            double idealDistanceSquared = Math.pow(client.player.getEntityInteractionRange() - 1, 2);

            boolean forward = false;
            boolean back = false;

            if (distanceSquared > idealDistanceSquared) {
                forward = true;
            } else {
                back = true;
            }

            if (client.options.forwardKey.isPressed() != forward) {
                new KeyBindingBuilder().keys(client.options.forwardKey)
                        .pressed(forward)
                        .printLogs(false)
                        .execute();
            }
            if (client.options.backKey.isPressed() != back) {
                new KeyBindingBuilder().keys(client.options.backKey)
                        .pressed(back)
                        .printLogs(false)
                        .execute();
            }

            // will not attack entity if it is out of reach
            if (client.targetedEntity == null) return;
            // will not attack entity if player attack is on cooldown
            if (client.player.getAttackCooldownProgress(0.0F) < 1.0F) return;
            // will not attack entity if entity is still invincible from previous attacks
            if (target.hurtTime > 0) return;

            client.doAttack();
        });
    }

    // todo: does this work with damage enchantments? (e.g. sharpness, bane, smite)
    public static double getAttackDamage(ItemStack stack) {
        double baseDamage = 0.0;
        if (stack.isEmpty()) return baseDamage;

        AttributeModifiersComponent attributeModifierComponent = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        for (AttributeModifiersComponent.Entry entry : attributeModifierComponent.modifiers()) {
            if (entry.attribute() == EntityAttributes.ATTACK_DAMAGE) {
                baseDamage += entry.modifier().value();
            }
        }

        return baseDamage;
    }

    public static int execute(FabricClientCommandSource source) {
        ClientPlayerEntity player = source.getPlayer();

        // find nearest mobs (within 16 blocks)
        int radius = 16;
        List<LivingEntity> nearbyLivingEntities = EntityUtils.getLivingEntities(player, radius);

        if (nearbyLivingEntities.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .text("There are no living entities within %d blocks!".formatted(radius))
                    .messageType(MessageType.ERROR)
                    .execute();
            return 0;
        }

        // if mobs exist, find the closest one
        target = nearbyLivingEntities.stream()
                .min(Comparator.comparingDouble(e -> MathUtils.distanceToSquared(e.getPos(), player.getPos())))
                .orElse(nearbyLivingEntities.getFirst());

        // attempt to find best possible weapon in hotbar
        List<ItemStack> hotbar = source.getPlayer().getInventory().main.subList(0, 9);
        int bestItemIndex = IntStream.range(0, hotbar.size())
                .boxed()
                .max(Comparator.comparingDouble(i -> getAttackDamage(hotbar.get(i))))
                .orElse(-1);

        if (bestItemIndex != -1) {
            source.getPlayer().getInventory().selectedSlot = bestItemIndex;

            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.literal("Switching to ")
                            .append(Text.translatable(source.getClient().options.hotbarKeys[bestItemIndex].getTranslationKey()))
                            .append(" (best weapon)..."))
                    .execute();
        }

        isRunning = true;

        return 1;
    }
}
