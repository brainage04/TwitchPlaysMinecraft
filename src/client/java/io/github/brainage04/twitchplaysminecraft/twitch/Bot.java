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

import java.util.concurrent.ScheduledThreadPoolExecutor;

import static io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot.commandQueue;

public class Bot {
    private static final String CLIENT_ID = "xccyzcvcaybxti6jqqq1ts3qqtlmge";

    private final TwitchIdentityProvider identityProvider;
    private final ScheduledThreadPoolExecutor executor;
    private final DeviceFlowController controller;
    private final CredentialManager credentialManager;
    private ITwitchClient client;

    public TwitchIdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public DeviceFlowController getController() {
        return controller;
    }

    public ITwitchClient getClient() {
        return client;
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

        client.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            // only process commands (messages with "!" prefix)
            if (!event.getMessage().startsWith("!")) return;

            commandQueue.add(event.getMessage().substring(1));
        });
    }
}
