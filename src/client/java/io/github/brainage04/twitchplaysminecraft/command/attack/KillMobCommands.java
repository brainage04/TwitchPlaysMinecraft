package io.github.brainage04.twitchplaysminecraft.command.attack;

import io.github.brainage04.twitchplaysminecraft.command.key.ReleaseAllKeysCommand;
import io.github.brainage04.twitchplaysminecraft.command.core.ClientSuggestionProviders;
import io.github.brainage04.twitchplaysminecraft.util.*;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.IntStream;

public class KillMobCommands {
    private static boolean isRunning = false;
    private static int ticksSinceLastAttack = 0;
    private static final int secondsSinceLastAttackLimit = 15;
    private static LivingEntity target = null;

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (target == null) return;
            if (client.player == null) return;

            ticksSinceLastAttack++;
            if (ticksSinceLastAttack > secondsSinceLastAttackLimit * 20) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.ERROR)
                        .text("No hits landed for 15 seconds! Cancelling attack...")
                        .execute();

                stop(SourceUtils.getSource(client.player));

                return;
            }

            if (target.isDead()) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.SUCCESS)
                        .text("Mob has died.")
                        .execute();

                stop(SourceUtils.getSource(client.player));

                return;
            }

            double distanceSquared = client.player.squaredDistanceTo(target);

            if (distanceSquared > client.player.getEntityInteractionRange()) {
                client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
                client.options.forwardKey.setPressed(true);

                return;
            }

            double idealDistanceSquared = Math.pow(getIdealDistance(client.player, target), 2);

            boolean forward = false;
            boolean back = false;

            if (distanceSquared > idealDistanceSquared) {
                forward = true;
            } else {
                back = true;
            }

            client.options.forwardKey.setPressed(forward);
            client.options.backKey.setPressed(back);

            client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());

            // will not attack entity if it is out of reach
            if (client.targetedEntity == null) return;
            // will not attack entity if player attack is on cooldown
            if (client.player.getAttackCooldownProgress(0.0F) < 1.0F) return;
            // will not attack entity if entity is still invincible from previous attacks
            if (target.hurtTime > 0) return;

            client.doAttack();
        });
    }

    @SuppressWarnings("SameReturnValue")
    public static void stop(FabricClientCommandSource source) {
        isRunning = false;

        ReleaseAllKeysCommand.releaseAllKeys(source);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Attack cancelled.")
                .execute();

    }

    private static double getIdealDistance(ClientPlayerEntity player, LivingEntity target) {
        double defaultValue = 1;

        if (!(target instanceof HostileEntity hostileEntity)) return defaultValue;
        if (hostileEntity.getAttacker() == null) return defaultValue;
        if (hostileEntity.getAttacker().getUuid() != player.getUuid()) return defaultValue;

        return player.getEntityInteractionRange() - 1;
    }

    public static int executeMelee(FabricClientCommandSource source, EntityType<? extends Entity> entityType) {
        if (isRunning) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You are already attacking something!")
                    .execute();

            return 0;
        }

        // find nearest mobs (within 16 blocks)
        int radius = 16;
        List<? extends Entity> entities;
        Text entityName;
        if (entityType == null) {
            entities = EntityUtils.getEntities(Entity.class, source.getWorld(), source.getPosition(), radius, source.getPlayer().getUuid());
            entityName = Text.literal("living");
        } else {
            entities = EntityUtils.getEntities(entityType, source.getWorld(), source.getPosition(), radius, source.getPlayer().getUuid());
            entityName = entityType.getName();
        }

        if (entities.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text(Text.literal("There are no ")
                            .append(entityName)
                            .append(" entities within %d blocks!".formatted(radius)))
                    .execute();

            return 0;
        }

        // if mobs exist, find the closest one
        Optional<? extends Entity> potentialTarget = entities.stream()
                .min(Comparator.comparingDouble(e -> MathUtils.distanceToSquared(e.getPos(), source.getPlayer().getPos())));

        if (!(potentialTarget.get() instanceof LivingEntity)) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text(Text.empty().append(entityName)
                            .append(" cannot be killed because it is not living!"))
                    .execute();

            return 0;
        }

        target = (LivingEntity) potentialTarget.get();

        // Compute A* path

        // attempt to find best possible weapon in hotbar
        List<ItemStack> hotbar = source.getPlayer().getInventory().main.subList(0, 9);
        int bestItemIndex = IntStream.range(0, hotbar.size())
                .boxed()
                .max(Comparator.comparingDouble(i -> ItemStackUtils.getItemDps(source.getPlayer(), hotbar.get(i))))
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

        //source.getClient().options.sprintKey.setPressed(true);
        source.getClient().options.getAutoJump().setValue(true);

        isRunning = true;
        ticksSinceLastAttack = 0;

        return 1;
    }

    public static int executeMelee(FabricClientCommandSource source, Identifier entityClassId) {
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityClassId);
        if (entityType == EntityType.PIG && !entityClassId.getPath().equals("pig")) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Invalid entity! Valid entities: %s.".formatted(String.join(", ", ClientSuggestionProviders.livingEntityTypeStrings)))
                    .execute();

            return 0;
        }

        // todo: fix
        return executeMelee(source, entityType);
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
