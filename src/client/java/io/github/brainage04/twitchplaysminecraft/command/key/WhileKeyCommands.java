package io.github.brainage04.twitchplaysminecraft.command.key;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

// "while" keys are processed in while blocks as the name suggests
// they tend to function as the operating system determines
// (e.g. holding down will perform the action once, then ~20 times/second after a short delay until released)
@SuppressWarnings("SameReturnValue")
public class WhileKeyCommands {
    public static int execute(FabricClientCommandSource source, KeyBinding key) {
        source.getClient().execute(() -> key.timesPressed++);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text(Text.translatable(key.getTranslationKey())
                        .append(" pressed."))
                .execute();

        return 1;
    }
}
