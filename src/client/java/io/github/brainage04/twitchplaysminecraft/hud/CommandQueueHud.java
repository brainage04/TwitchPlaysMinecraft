package io.github.brainage04.twitchplaysminecraft.hud;

import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.brainage04.twitchplaysminecraft.hud.core.HUDRenderer.renderElement;
import static io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot.getCommandQueue;

public class CommandQueueHud {
    public static void render(TextRenderer renderer, DrawContext context, ModConfig.CommandQueueConfig config) {
        if (!config.coreSettings.enabled) return;

        List<Text> lines = new ArrayList<>(getCommandQueue().size() + 1);
        lines.add(Text.literal("Command Queue:"));
        lines.addAll(getCommandQueue().stream().map(Text::literal).collect(Collectors.toSet()));

        renderElement(renderer, context, lines, config.coreSettings);
    }
}
