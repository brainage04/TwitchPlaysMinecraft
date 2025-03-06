package io.github.brainage04.twitchplaysminecraft.command.screen;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.ActionType;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.MoveItemType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class MoveItemCommand {
    private static boolean isRunning = false;
    private static int ticks = 0;
    private static int first = -1;
    private static int second = -1;

    private static MoveItemType moveItemType = null;
    private static ActionType actionType = null;

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (client.player == null) return;
            if (first < 0 || first > 8) return;
            if (second < 0 || second > 8) return;
            if (moveItemType == null) return;

            // todo: test without delay for funsies
            switch (moveItemType) {
                case HOTBAR -> {
                    // swap hotbar slots
                    client.player.getInventory().selectedSlot = first;
                    client.execute(() -> client.options.swapHandsKey.setPressed(true));

                    client.player.getInventory().selectedSlot = second;
                    client.execute(() -> client.options.swapHandsKey.setPressed(true));

                    client.player.getInventory().selectedSlot = first;
                    client.execute(() -> client.options.swapHandsKey.setPressed(true));

                    stop(SourceUtils.getSource(client.player));
                }
                case INVENTORY -> {
                    if (actionType == null) return;
                    if (!(client.currentScreen instanceof HandledScreen<? extends ScreenHandler> handledScreen)) return;

                    // swap inventory slots
                    Slot slot1 = handledScreen.getScreenHandler().getSlot(first);
                    if (!slot1.hasStack()) {
                        new ClientFeedbackBuilder().source(client.player)
                                .messageType(MessageType.ERROR)
                                .text("There is nothing in slot %d".formatted(slot1.id))
                                .execute();

                        return;
                    }

                    Slot slot2 = handledScreen.getScreenHandler().getSlot(second);
                    if (slot2.canInsert(slot1.getStack())) {
                        new ClientFeedbackBuilder().source(client.player)
                                .messageType(MessageType.ERROR)
                                .text("You cannot move items to slot %d as this is an output slot!".formatted(slot1.id))
                                .execute();

                        return;
                    }
                    boolean shouldSwap = slot2.hasStack();

                    if (actionType == ActionType.QUICKMOVE) {
                        handledScreen.onMouseClick(slot1, slot1.id, 0, SlotActionType.QUICK_MOVE);

                        return;
                    } else {
                        handledScreen.onMouseClick(slot1, slot1.id, 0, SlotActionType.PICKUP);
                    }

                    handledScreen.onMouseClick(slot2, slot2.id, 0, SlotActionType.PICKUP);

                    if (actionType == ActionType.SWAP && shouldSwap) {
                        handledScreen.onMouseClick(slot1, slot1.id, 0, SlotActionType.PICKUP);
                    }
                }
            }
        });
    }

    public static int stop(FabricClientCommandSource source) {
        isRunning = false;
        ticks = 0;
        first = -1;
        second = -1;
        moveItemType = null;
        actionType = null;

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Stopped moving items.")
                .execute();

        return 1;
    }

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

            isRunning = true;

            MoveItemCommand.first = first;
            MoveItemCommand.second = second;

            moveItemType = MoveItemType.HOTBAR;
        } else if (source.getClient().currentScreen instanceof HandledScreen<? extends ScreenHandler>) {
            isRunning = true;

            MoveItemCommand.first = first;
            MoveItemCommand.second = second;

            moveItemType = MoveItemType.INVENTORY;
            MoveItemCommand.actionType = actionType;
        } else {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Current screen does not have an inventory!")
                    .execute();

            return 0;
        }

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
