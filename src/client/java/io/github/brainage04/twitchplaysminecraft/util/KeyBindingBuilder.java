package io.github.brainage04.twitchplaysminecraft.util;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

import java.util.Arrays;

public class KeyBindingBuilder {
    private FabricClientCommandSource source = SourceUtils.getSource();
    private boolean pressed = true;
    private int extraTickDelay = 0;
    private boolean printLogs = true;
    private KeyBinding[] keys = new KeyBinding[0];

    public KeyBindingBuilder source(FabricClientCommandSource source) {
        this.source = source;
        return this;
    }

    public KeyBindingBuilder pressed(boolean pressed) {
        this.pressed = pressed;
        return this;
    }

    public KeyBindingBuilder extraTickDelay(int extraTickDelay) {
        this.extraTickDelay = extraTickDelay;
        return this;
    }

    public KeyBindingBuilder printLogs(boolean printLogs) {
        this.printLogs = printLogs;
        return this;
    }

    public KeyBindingBuilder keys(KeyBinding... keys) {
        this.keys = keys;
        return this;
    }

    public void execute() {
        String message = pressed ? "now" : "no longer";
        String message2 = keys.length > 1 ? "keys" : "key";

        RunnableScheduler.scheduleTask(() -> {
            GameOptions options = source.getClient().options;
            for (KeyBinding key : keys) {
                if (key.equals(options.inventoryKey)
                        || key.equals(options.swapHandsKey)
                        || key.equals(options.pickItemKey)
                        || key.equals(options.dropKey)) {
                    Text text = Text.literal("The ")
                            .append(Text.translatable(key.getTranslationKey()))
                            .append(" key cannot be held, so it will be pressed instead. This message probably shouldn't be appearing!");

                    TwitchPlaysMinecraft.LOGGER.info(text.getString());

                    key.timesPressed++;
                } else {
                    key.setPressed(pressed);
                }
            }

            if (printLogs) {
                Text[] keyNames = Arrays.stream(keys).map(key -> Text.translatable(key.getTranslationKey())).toArray(Text[]::new);

                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.SUCCESS)
                        .text(Text.literal("Player is %s holding the ".formatted(message))
                                .append(TextUtils.combineTextArray(keyNames))
                                .append(" %s.".formatted(message2)))
                        .execute();
            }
        }, 1 + extraTickDelay);
    }
}
