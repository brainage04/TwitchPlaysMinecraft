package io.github.brainage04.twitchplaysminecraft.util.feedback;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class ClientFeedbackBuilder extends FeedbackBuilder<FabricClientCommandSource> {
    public ClientFeedbackBuilder() {
        source = SourceUtils.getSourceFromClient();
    }

    @Override
    protected void sendFeedback() {
        source.sendFeedback(text);

        if (source.getClient().player == null) return;
        source.getClient().player.playSound(soundEvent, volume, pitch);

        if (!sendInTwitchChat) return;
        InstalledChatbot.getBot().sendChatMessage(text.getString());
    }

    public FeedbackBuilder<FabricClientCommandSource> source(FabricClientCommandSource source) {
        this.source = source;
        return this;
    }

    public FeedbackBuilder<FabricClientCommandSource> source(MinecraftClient client) {
        source = SourceUtils.getSourceFromClient(client);
        return this;
    }
}
