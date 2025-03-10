package io.github.brainage04.twitchplaysminecraft.command.key;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.RunnableScheduler;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

@SuppressWarnings("SameReturnValue")
// "if" keys are processed in if blocks as the name suggests
// they tend to function in a binary manner (pressed/held down = pressed, not pressed/held down = not pressed)
// holding the key down will not repeat the action like "while" keys
public class IfKeyCommands {
    public static int execute(FabricClientCommandSource source, KeyBinding key) {
        // key.timesPressed++; does not work. I have tried.
        RunnableScheduler.scheduleTask(() -> {
            if (key.equals(source.getClient().options.attackKey)) {
                source.getClient().doAttack();
            }

            key.setPressed(true);
        });
        RunnableScheduler.scheduleTask(() -> key.setPressed(false), 2);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text(Text.translatable(key.getTranslationKey())
                        .append(" pressed."))
                .execute();

        return 1;
    }
}
