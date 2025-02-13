package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class UseCommand {
    private static boolean isUsing = false;
    public static int currentUses = 0;
    private static int maxUses = 0;

    private static boolean prevIsUsingItem = false;

    private static void stop() {
        isUsing = false;
        currentUses = 0;
    }

    private static void incrementCurrentUses(MinecraftClient client) {
        if (client.player == null) return;

        currentUses++;

        if (currentUses < maxUses) {
            new ClientFeedbackBuilder().source(client)
                    .messageType(MessageType.INFO)
                    .text("Used %d/%d...".formatted(currentUses, maxUses))
                    .execute();
        } else {
            stop();
            new KeyBindingBuilder().keys(client.options.useKey).pressed(false).execute();
            new ClientFeedbackBuilder().source(client)
                    .messageType(MessageType.SUCCESS)
                    .text("Used item %d times.".formatted(maxUses))
                    .execute();
        }
    }

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isUsing) return;
            if (client.player == null) return;

            Hand hand = client.player.preferredHand == null ? Hand.MAIN_HAND : client.player.preferredHand;
            ItemStack stack = client.player.getStackInHand(hand);
            if (stack.isEmpty()) {
                new ClientFeedbackBuilder().source(client)
                        .messageType(MessageType.ERROR)
                        .text("No item in hand to use!")
                        .execute();
                stop();
                return;
            }

            int useTime = stack.getItem().getMaxUseTime(stack, client.player);

            if (useTime > 1) {
                // just simulate right click for long use items
                new KeyBindingBuilder().keys(client.options.useKey).execute();

                // monitor for item use start
                if (!prevIsUsingItem && client.player.isUsingItem()) {
                    prevIsUsingItem = true;
                }

                // monitor for item use end
                if (prevIsUsingItem && !client.player.isUsingItem()) {
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
        isUsing = true;
        maxUses = count;

        Hand hand = source.getPlayer().preferredHand == null ? Hand.MAIN_HAND : source.getPlayer().preferredHand;
        ItemStack stack = source.getPlayer().getStackInHand(hand);
        if (stack.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No item in hand to use!")
                    .execute();
            stop();
            return 0;
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("Attempting to use ")
                        .append(source.getPlayer().getMainHandStack().getFormattedName())
                        .append(" %d times...".formatted(count)))
                .execute();

        return 1;
    }
}
