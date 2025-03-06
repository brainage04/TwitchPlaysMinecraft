package io.github.brainage04.twitchplaysminecraft.command.use;

import io.github.brainage04.twitchplaysminecraft.command.key.ReleaseAllKeysCommand;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class UseCommand {
    private static boolean isRunning = false;
    public static int currentUses = 0;
    private static int maxUses = 0;

    private static boolean prevIsUsingItem = false;

    public static int stop(FabricClientCommandSource source) {
        ReleaseAllKeysCommand.execute(source);

        isRunning = false;
        currentUses = 0;

        return 1;
    }

    private static void incrementCurrentUses(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        currentUses++;

        if (currentUses < maxUses) {
            new ClientFeedbackBuilder().source(client)
                    .messageType(MessageType.INFO)
                    .text("Used %d/%d...".formatted(currentUses, maxUses))
                    .execute();
        } else {
            new ClientFeedbackBuilder().source(client)
                    .messageType(MessageType.SUCCESS)
                    .text("Used %d/%d.".formatted(currentUses, maxUses))
                    .execute();

            stop(SourceUtils.getSource(player));
        }
    }

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;

            ClientPlayerEntity player = client.player;
            if (player == null) return;

            Hand hand = player.preferredHand == null ? Hand.MAIN_HAND : player.preferredHand;
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isEmpty()) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.ERROR)
                        .text("No item in hand to use!")
                        .execute();

                stop(SourceUtils.getSource(player));

                return;
            }

            int useTime = stack.getItem().getMaxUseTime(stack, player);

            if (useTime > 1) {
                // just simulate right click for long use items
                new KeyBindingBuilder().keys(client.options.useKey).execute();

                // monitor for item use start
                if (!prevIsUsingItem && player.isUsingItem()) {
                    prevIsUsingItem = true;
                }

                // monitor for item use end
                if (prevIsUsingItem && !player.isUsingItem()) {
                    prevIsUsingItem = false;
                    incrementCurrentUses(client);
                }
            } else {
                client.doItemUse();
                incrementCurrentUses(client);
            }
        });
    }

    public static int execute(FabricClientCommandSource source, int count) {
        ClientPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        Hand hand = player.preferredHand == null ? Hand.MAIN_HAND : player.preferredHand;
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No item in hand to use!")
                    .execute();

            return 0;
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("Attempting to use ")
                        .append(player.getMainHandStack().getFormattedName())
                        .append(" %d times...".formatted(count)))
                .execute();

        isRunning = true;
        maxUses = count;

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
