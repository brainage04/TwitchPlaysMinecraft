package io.github.brainage04.twitchplaysminecraft.command.use;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

// todo: test
public class UseItemCommand {
    private static boolean isRunning = false;
    public static int currentUses = 0;
    private static int maxUses = 0;

    private static boolean prevIsUsingItem = false;

    public static void stop() {
        isRunning = false;

    }

    private static void incrementCurrentUses(MinecraftClient client) {
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

            client.options.useKey.setPressed(false);

            stop();
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

                client.options.useKey.setPressed(false);

                stop();

                return;
            }

            int useTime = stack.getItem().getMaxUseTime(stack, player);

            if (useTime > 1) {
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
        Hand hand = source.getPlayer().preferredHand == null ? Hand.MAIN_HAND : source.getPlayer().preferredHand;
        ItemStack stack = source.getPlayer().getStackInHand(hand);
        if (stack.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text(Text.translatable(source.getClient().options.useKey.getTranslationKey())
                            .append(" pressed."))
                    .execute();

            source.getClient().options.useKey.timesPressed++;

            return 1;
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("Attempting to use ")
                        .append(source.getPlayer().getMainHandStack().getFormattedName())
                        .append(" %d times...".formatted(count)))
                .execute();

        isRunning = true;
        currentUses = 0;
        maxUses = count;

        source.getClient().options.useKey.setPressed(true);

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
