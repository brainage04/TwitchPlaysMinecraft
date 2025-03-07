package io.github.brainage04.twitchplaysminecraft.command.attack;

import io.github.brainage04.twitchplaysminecraft.command.key.ReleaseAllKeysCommand;
import io.github.brainage04.twitchplaysminecraft.command.core.ClientSuggestionProviders;
import io.github.brainage04.twitchplaysminecraft.util.*;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.IntStream;

public class AttackCommand {
    private static boolean isRunning = false;
    private static int ticksSinceLastAttack = 0;
    private static LivingEntity target = null;
    private static List<BlockPos> path = null;
    private static int pathIndex = 0;

    // use auto jump to avoid getting stuck if the
    // difference in Y coordinates from player and target is positive
    private static boolean prevAutoJump = false;

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (target == null) return;
            if (path == null) return;
            if (client.player == null) return;

            ticksSinceLastAttack++;
            if (ticksSinceLastAttack > 300) {
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
                // this has bugs so straight forward approach will do for now
                /*
                PathFindingUtils.visualizePath(client.player, path);

                Vec3d nextPos = path.get(pathIndex).toCenterPos();
                PathFindingUtils.guidePlayerAlongPath(client.player, new Vec3d(nextPos.getX(), client.player.getEyeY(), nextPos.getZ()));
                if (client.player.squaredDistanceTo(nextPos) < 0.5) pathIndex++;
                if (pathIndex >= path.size()) {
                    new ClientFeedbackBuilder().source(SourceUtils.getSource(client.player))
                            .messageType(MessageType.INFO)
                            .text("Finished path finding but mob is not close enough to hit. Regenerating path...")
                            .execute();

                    path = PathFindingUtils.calculatePathToMob(client.player, target);
                    pathIndex = 0;
                }
                 */

                client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
                MinecraftClient.getInstance().options.forwardKey.setPressed(true);

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
    public static int stop(FabricClientCommandSource source) {
        isRunning = false;

        ReleaseAllKeysCommand.execute(source);
        MinecraftClient.getInstance().options.getAutoJump().setValue(prevAutoJump);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Attack cancelled.")
                .execute();

        return 1;
    }

    private static double getIdealDistance(ClientPlayerEntity player, LivingEntity target) {
        double defaultValue = 1;

        if (!(target instanceof HostileEntity hostileEntity)) return defaultValue;
        if (hostileEntity.getAttacker() == null) return defaultValue;
        if (hostileEntity.getAttacker().getUuid() != player.getUuid()) return defaultValue;

        return player.getEntityInteractionRange() - 1;
    }

    public static <T extends LivingEntity> int execute(FabricClientCommandSource source, Class<T> entityClass) {
        ClientPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        if (isRunning) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You are already attacking something!")
                    .execute();

            return 0;
        }

        // find nearest mobs (within 16 blocks)
        int radius = 16;
        List<T> entities = EntityUtils.getEntities(entityClass, player, radius);

        if (entities.isEmpty()) {
            String text = entityClass == LivingEntity.class ? "living entities" : "%s entities".formatted(entityClass.getName().replace("Entity", ""));

            new ClientFeedbackBuilder().source(source)
                    .text("There are no %s within %d blocks!".formatted(text, radius))
                    .messageType(MessageType.ERROR)
                    .execute();

            return 0;
        }

        // if mobs exist, find the closest one
        target = entities.stream()
                .min(Comparator.comparingDouble(e -> MathUtils.distanceToSquared(e.getPos(), player.getPos())))
                .orElse(entities.getFirst());

        // Compute A* path
        path = PathFindingUtils.calculatePathToMob(player, target);
        if (path == null || path.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .text("Path to mob could not be found!")
                    .messageType(MessageType.ERROR)
                    .execute();

            return 0;
        }

        // attempt to find best possible weapon in hotbar
        List<ItemStack> hotbar = source.getPlayer().getInventory().main.subList(0, 9);
        int bestItemIndex = IntStream.range(0, hotbar.size())
                .boxed()
                .max(Comparator.comparingDouble(i -> ItemStackUtils.getItemDps(player, hotbar.get(i))))
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

        prevAutoJump = source.getClient().options.getAutoJump().getValue();
        source.getClient().options.getAutoJump().setValue(true);

        ticksSinceLastAttack = 0;
        pathIndex = 0;

        return 1;
    }

    public static int execute(FabricClientCommandSource source, Identifier entityClassId) {
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityClassId);
        //noinspection ConstantValue
        if (entityType == null) return 0;
        Class<? extends Entity> entityClass = Registries.ENTITY_TYPE.get(entityClassId).getBaseClass();

        if (LivingEntity.class.isAssignableFrom(entityClass)) {
            @SuppressWarnings("unchecked")
            Class<? extends LivingEntity> livingEntityClass = (Class<? extends LivingEntity>) entityClass;

            return execute(source, livingEntityClass);
        } else {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Invalid entity! Valid entities: %s.".formatted(String.join(", ", ClientSuggestionProviders.livingEntityTypeStrings)))
                    .execute();

            return 0;
        }
    }

    public static int execute(FabricClientCommandSource source) {
        return execute(source, LivingEntity.class);
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
