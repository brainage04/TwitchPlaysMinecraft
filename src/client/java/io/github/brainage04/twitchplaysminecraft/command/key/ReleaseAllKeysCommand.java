package io.github.brainage04.twitchplaysminecraft.command.key;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.KeyBinding;

public class ReleaseAllKeysCommand {
    public static void releaseAllKeys(FabricClientCommandSource source) {
        ToggleKeyCommands.removeAllKeys();

        for (KeyBinding key : source.getClient().options.allKeys) {
            key.setPressed(false);
        }
    }

    @SuppressWarnings("SameReturnValue")
    public static int execute(FabricClientCommandSource source) {
        releaseAllKeys(source);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("All keys untoggled and released.")
                .execute();

        return 1;
    }
}
