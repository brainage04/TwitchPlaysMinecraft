package io.github.brainage04.twitchplaysminecraft.util.feedback;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public class ClientFeedbackBuilder extends FeedbackBuilder<FabricClientCommandSource> {
    public ClientFeedbackBuilder() {
        source = SourceUtils.getSource();
    }

    private void sendMinecraftFeedback() {
        if (source != null) {
            source.sendFeedback(text);

            if (source.getClient().player == null) return;
            source.getClient().player.playSound(soundEvent, volume, pitch);
        }
    }

    private void sendTwitchFeedback() {
        InstalledChatbot.getBot().sendChatMessage(text.getString());
    }

    @Override
    protected void sendFeedback() {
        switch (messageDestination) {
            case MINECRAFT -> sendMinecraftFeedback();
            case TWITCH -> sendTwitchFeedback();
            case ALL -> {
                sendTwitchFeedback();
                sendMinecraftFeedback();
            }
        }
    }

    public FeedbackBuilder<FabricClientCommandSource> source(FabricClientCommandSource source) {
        this.source = source;
        return this;
    }

    public FeedbackBuilder<FabricClientCommandSource> source(MinecraftClient client) {
        source = SourceUtils.getSource(client);
        return this;
    }

    public FeedbackBuilder<FabricClientCommandSource> source(ClientPlayerEntity player) {
        source = SourceUtils.getSource(player);
        return this;
    }

    public FeedbackBuilder<FabricClientCommandSource> source(ClientPlayNetworkHandler networkHandler) {
        source = SourceUtils.getSource(networkHandler);
        return this;
    }

}
