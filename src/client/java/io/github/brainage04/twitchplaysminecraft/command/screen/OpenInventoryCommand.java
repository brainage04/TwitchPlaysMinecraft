package io.github.brainage04.twitchplaysminecraft.command.screen;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class OpenInventoryCommand {
    public static int execute(FabricClientCommandSource source) {
        if (source.getClient().currentScreen != null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("You must close the current screen first!")
                    .execute();
            return 0;
        }

        source.getClient().setScreen(new InventoryScreen(source.getPlayer()));

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Opened inventory.")
                .execute();

        return 1;
    }
}
