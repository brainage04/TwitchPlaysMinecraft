package io.github.brainage04.twitchplaysminecraft.command.screen;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class QuickMoveCommand {
    public static int execute(FabricClientCommandSource source, int slotIndex) {
        ClientPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        if (source.getClient().currentScreen instanceof HandledScreen<? extends ScreenHandler> handledScreen) {
            Slot slot = handledScreen.getScreenHandler().getSlot(slotIndex);
            if (!slot.hasStack()) {
                new ClientFeedbackBuilder().source(player)
                        .messageType(MessageType.ERROR)
                        .text("There is nothing in slot %d!".formatted(slotIndex))
                        .execute();

                return 0;
            }

            handledScreen.onMouseClick(slot, slot.getIndex(), 0, SlotActionType.QUICK_MOVE);

            new ClientFeedbackBuilder().source(player)
                    .messageType(MessageType.SUCCESS)
                    .text("Quick moved slot %d.".formatted(slot.id))
                    .execute();
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
