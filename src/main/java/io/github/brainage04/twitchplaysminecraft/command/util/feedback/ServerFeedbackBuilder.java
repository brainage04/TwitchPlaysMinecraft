package io.github.brainage04.twitchplaysminecraft.command.util.feedback;

import net.minecraft.server.command.ServerCommandSource;

public class ServerFeedbackBuilder extends FeedbackBuilder<ServerCommandSource> {
    @Override
    protected void sendFeedback() {
        source.sendFeedback(() -> text, false);
    }
}
