package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@SuppressWarnings("SameReturnValue")
public class DropCommand {
    public static int execute(FabricClientCommandSource source, int count) {
        for (int i = 0; i < count; i++) {
            source.getPlayer().dropSelectedItem(false);
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Dropped %d items.".formatted(count))
                .execute();

        return 1;
    }

    public static int execute(FabricClientCommandSource source, int slotIndex, int count) {
        int oldSlot = source.getPlayer().getInventory().selectedSlot;
        source.getPlayer().getInventory().selectedSlot = slotIndex;

        execute(source, count);

        source.getPlayer().getInventory().selectedSlot = oldSlot;

        return 1;
    }

    public static int executeDropAll(FabricClientCommandSource source) {
        source.getPlayer().dropSelectedItem(true);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Dropped entire stack.")
                .execute();

        return 1;
    }

    public static int executeDropAll(FabricClientCommandSource source, int slotIndex) {
        int oldSlot = source.getPlayer().getInventory().selectedSlot;
        source.getPlayer().getInventory().selectedSlot = slotIndex;

        executeDropAll(source);

        source.getPlayer().getInventory().selectedSlot = oldSlot;

        return 1;
    }
}
