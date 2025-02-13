package io.github.brainage04.twitchplaysminecraft.util.feedback;

import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FeedbackBuilder {
    private CommandSource source = SourceUtils.getSourceFromClient();
    private MutableText text = Text.literal("");
    private MessageType messageType = MessageType.NONE;

    public FeedbackBuilder source(FabricClientCommandSource source) {
        this.source = source;
        return this;
    }

    public FeedbackBuilder source(MinecraftClient client) {
        this.source = SourceUtils.getSourceFromClient(client);
        return this;
    }

    public FeedbackBuilder source(ServerCommandSource source) {
        this.source = source;
        return this;
    }

    public FeedbackBuilder text(String text) {
        this.text = Text.literal(text);
        return this;
    }

    public FeedbackBuilder text(MutableText text) {
        this.text = text;
        return this;
    }

    public FeedbackBuilder messageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    public void execute() {
        switch (messageType) {
            case INFO -> text = text.formatted(Formatting.YELLOW);
            case ERROR -> text = text.formatted(Formatting.RED);
            case SUCCESS -> text = text.formatted(Formatting.GREEN);
        }

        if (source instanceof FabricClientCommandSource clientSource) {
            clientSource.sendFeedback(text);
        } else if (source instanceof ServerCommandSource serverSource) {
            serverSource.sendFeedback(() -> text, false);
        }

        // todo: send feedback in twitch chat as well
    }
}
