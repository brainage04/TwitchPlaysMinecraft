package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.enums.ActionType;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import static io.github.brainage04.twitchplaysminecraft.util.CommandUtils.millisecondsBetweenSteps;
import static io.github.brainage04.twitchplaysminecraft.util.CommandUtils.startNewCurrentInteractionThread;
import static io.github.brainage04.twitchplaysminecraft.util.ThreadUtils.sleepSafely;

public class MoveItemCommand {
    public static int execute(FabricClientCommandSource source, int first, int second, String actionTypeString) {
        ActionType actionType = EnumUtils.getValueSafely(ActionType.class, actionTypeString);
        if (actionType == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Invalid action! Valid actions: %s.".formatted(EnumUtils.joinEnumValues(ActionType.class)))
                    .execute();
            return 0;
        }

        if (source.getClient().currentScreen == null) {
            if (first == second) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.ERROR)
                        .text("First and second slot indices must be different!")
                        .execute();
                return 0;
            }

            if (first < 0 || first > 8) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.ERROR)
                        .text("First index must range from 0-8!")
                        .execute();
                return 0;
            }

            if (second < 0 || second > 8) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.ERROR)
                        .text("Second index must range from 0-8!")
                        .execute();
                return 0;
            }

            // swap hotbar slots (using off hand)
            // todo: test without thread?
            startNewCurrentInteractionThread(new Thread(() -> {
                source.getPlayer().getInventory().selectedSlot = first;
                source.getClient().execute(() -> source.getClient().options.swapHandsKey.setPressed(true));
                sleepSafely(millisecondsBetweenSteps);
                source.getPlayer().getInventory().selectedSlot = second;
                source.getClient().execute(() -> source.getClient().options.swapHandsKey.setPressed(true));
                sleepSafely(millisecondsBetweenSteps);
                source.getPlayer().getInventory().selectedSlot = first;
                source.getClient().execute(() -> source.getClient().options.swapHandsKey.setPressed(true));
            }));
        } else if (source.getClient().currentScreen instanceof HandledScreen<? extends ScreenHandler> handledScreen) {
            // swap inventory slots
            Slot slot1 = handledScreen.getScreenHandler().getSlot(first);
            if (!slot1.hasStack()) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.ERROR)
                        .text("There is nothing in slot %d".formatted(slot1.id))
                        .execute();
                return 0;
            }

            Slot slot2 = handledScreen.getScreenHandler().getSlot(second);
            if (slot2.canInsert(slot1.getStack())) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.ERROR)
                        .text("You cannot move items to slot %d as this is an output slot!".formatted(slot1.id))
                        .execute();
                return 0;
            }
            boolean shouldSwap = slot2.hasStack();

            // todo: test without thread?
            startNewCurrentInteractionThread(new Thread(() -> {
                handledScreen.onMouseClick(slot1, slot1.id, 0, SlotActionType.PICKUP);

                if (actionType == ActionType.QUICKMOVE) return;

                sleepSafely(millisecondsBetweenSteps);
                handledScreen.onMouseClick(slot2, slot2.id, 0, SlotActionType.PICKUP);

                if (actionType != ActionType.SWAP && !shouldSwap) return;

                sleepSafely(millisecondsBetweenSteps);
                handledScreen.onMouseClick(slot1, slot1.id, 0, SlotActionType.PICKUP);
            }));
        } else {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Current screen does not have an inventory!")
                    .execute();
            return 0;
        }

        return 1;
    }
}
