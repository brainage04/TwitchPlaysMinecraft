package io.github.brainage04.twitchplaysminecraft.command.screen;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.enums.MoveItemType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class MoveItemCommand {
    private static boolean isRunning = false;
    private static int ticks = 0;
    private static int first = -1;
    private static int second = -1;
    private static MoveItemType moveItemType = null;
    private static int prevSelectedSlot = -1;

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (first < 0) return;
            if (second < 0) return;
            if (moveItemType == null) return;
            if (client.player == null) return;

            switch (moveItemType) {
                case HOTBAR -> {
                    if (client.currentScreen != null) {
                        stop();
                    }

                    switch (ticks) {
                        case 1 -> client.player.getInventory().setSelectedSlot(first);
                        case 2 -> client.options.swapHandsKey.timesPressed++;
                        case 3 -> client.player.getInventory().setSelectedSlot(second);
                        case 4 -> client.options.swapHandsKey.timesPressed++;
                        case 5 -> client.player.getInventory().setSelectedSlot(first);
                        case 6 -> client.options.swapHandsKey.timesPressed++;
                        case 7 -> {
                            client.player.getInventory().setSelectedSlot(prevSelectedSlot);

                            new ClientFeedbackBuilder().source(client.player)
                                    .messageType(MessageType.SUCCESS)
                                    .text("Swapped contents of slots %d and %d.".formatted(first, second))
                                    .execute();

                            stop();
                        }
                    }
                }
                case INVENTORY -> {
                    if (client.currentScreen == null) stop();
                    else if (!(client.currentScreen instanceof HandledScreen<? extends ScreenHandler>)) stop();

                    HandledScreen<? extends ScreenHandler> handledScreen = (HandledScreen<? extends ScreenHandler>) client.currentScreen;

                    Slot slot1 = handledScreen.getScreenHandler().getSlot(first);
                    Slot slot2 = handledScreen.getScreenHandler().getSlot(second);

                    switch (ticks) {
                        case 1 -> handledScreen.onMouseClick(slot1, slot1.getIndex(), 0, SlotActionType.PICKUP);
                        case 2 -> {
                            handledScreen.onMouseClick(slot2, slot2.getIndex(), 0, SlotActionType.PICKUP);

                            if (handledScreen.getScreenHandler().getCursorStack().isEmpty()) {
                                new ClientFeedbackBuilder().source(client.player)
                                        .messageType(MessageType.SUCCESS)
                                        .text("Moved contents of slot %d to slot %d.".formatted(first, second))
                                        .execute();

                                stop();
                            }
                        }
                        case 3 -> {
                            handledScreen.onMouseClick(slot1, slot1.getIndex(), 0, SlotActionType.PICKUP);

                            new ClientFeedbackBuilder().source(client.player)
                                    .messageType(MessageType.SUCCESS)
                                    .text("Swapped contents of slots %d and %d.".formatted(first, second))
                                    .execute();

                            stop();
                        }
                    }
                }
            }

            ticks++;
        });
    }

    public static void stop() {
        isRunning = false;

    }

    private static boolean isHotbarSlotInvalid(FabricClientCommandSource source, int slotIndex, String slotString) {
        if (slotIndex < 0 || slotIndex > 8) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("%s index must range from 0-8!".formatted(slotString))
                    .execute();

            return true;
        }

        return false;
    }

    private static boolean isScreenSlotInvalid(FabricClientCommandSource source, HandledScreen<? extends ScreenHandler> handledScreen, int slotIndex, boolean checkContents) {
        if (slotIndex >= handledScreen.getScreenHandler().slots.size()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Slot %d is out of bounds! (Valid range: 0-%d)".formatted(slotIndex, handledScreen.getScreenHandler().slots.size() - 1))
                    .execute();

            return true;
        }

        if (checkContents) {
            Slot slot = handledScreen.getScreenHandler().getSlot(slotIndex);
            if (!slot.hasStack()) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.ERROR)
                        .text("There is nothing in slot %d!".formatted(slotIndex))
                        .execute();

                return true;
            }
        }

        return false;
    }

    public static int execute(FabricClientCommandSource source, int first, int second) {
        ClientPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        if (source.getClient().currentScreen == null) {
            if (first == second) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.ERROR)
                        .text("First and second slot indices must be different!")
                        .execute();

                return 0;
            }

            if (isHotbarSlotInvalid(source, first, "First")) return 0;
            if (isHotbarSlotInvalid(source, second, "Second")) return 0;

            moveItemType = MoveItemType.HOTBAR;
            prevSelectedSlot = player.getInventory().selectedSlot;
        } else if (source.getClient().currentScreen instanceof HandledScreen<? extends ScreenHandler> handledScreen) {
            if (isScreenSlotInvalid(source, handledScreen, first, true)) return 0;
            if (isScreenSlotInvalid(source, handledScreen, second, false)) return 0;

            moveItemType = MoveItemType.INVENTORY;
        } else {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Current screen does not have an inventory!")
                    .execute();

            return 0;
        }

        isRunning = true;

        ticks = 0;

        MoveItemCommand.first = first;
        MoveItemCommand.second = second;

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
