package io.github.brainage04.twitchplaysminecraft.twitch;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.authcontroller.DeviceFlowController;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.util.ThreadUtils;
import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.ToggleableCommand;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import static io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot.addToCommandQueue;
import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.getConfig;

public class Bot {
    private static final String CLIENT_ID = "xccyzcvcaybxti6jqqq1ts3qqtlmge";

    private final TwitchIdentityProvider identityProvider;
    private final ScheduledThreadPoolExecutor executor;
    private final DeviceFlowController controller;
    private final CredentialManager credentialManager;
    private ITwitchClient client;
    private String username;

    public TwitchIdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public DeviceFlowController getController() {
        return controller;
    }

    public Bot() {
        identityProvider = new TwitchIdentityProvider(CLIENT_ID, null, null);
        executor = ThreadUtils.getDefaultScheduledThreadPoolExecutor("t4j-bot", Runtime.getRuntime().availableProcessors());
        controller = new DeviceFlowController(executor, 0);
        credentialManager = CredentialManagerBuilder.builder().withAuthenticationController(controller).build();
        credentialManager.registerIdentityProvider(identityProvider);
    }

    public void start(OAuth2Credential credential) {
        client = TwitchClientBuilder.builder()
                .withClientId(CLIENT_ID)
                .withScheduledThreadPoolExecutor(executor)
                .withCredentialManager(credentialManager)
                .withEnableChat(true)
                .withChatAccount(credential)
                .withEnableHelix(true)
                .withDefaultAuthToken(credential)
                .build();
        username = credential.getUserName();

        client.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return;

            // only process commands (messages with "!" prefix)
            if (!event.getMessage().startsWith("!")) return;

            String command = event.getMessage().substring(1);

            String commandName = command.split(" ")[0];
            ToggleableCommand toggleableCommand = EnumUtils.getValueSafely(ToggleableCommand.class, commandName);
            if (toggleableCommand != null) {
                if (!toggleableCommand.enabled) {
                    new ClientFeedbackBuilder().source(networkHandler)
                            .messageType(MessageType.ERROR)
                            .text("The \"%s\" command has been disabled by the streamer!")
                            .execute();

                    return;
                }
            }

            if (getConfig().commandQueueConfig.enabled) {
                addToCommandQueue(command);
            } else {
                networkHandler.sendChatCommand(command);
            }
        });
    }

    public void sendChatMessage(String message) {
        if (username.isEmpty()) {
            TwitchPlaysMinecraft.LOGGER.error("Username is empty - this shouldn't happen!");
            return;
        }

        if (!client.getChat().isChannelJoined(username)) {
            client.getChat().joinChannel(username);
        }
        client.getChat().sendMessage(username, message);
    }
}
