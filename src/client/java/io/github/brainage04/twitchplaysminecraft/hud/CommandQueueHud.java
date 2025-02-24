package io.github.brainage04.twitchplaysminecraft.hud;

import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.brainage04.twitchplaysminecraft.hud.core.HUDRenderer.renderElement;
import static io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot.getCommandQueue;

public class CommandQueueHud {
    private static final List<Text> lines = new ArrayList<>();

    public static void updateLines() {
        lines.clear();

        lines.add(Text.literal("Command Queue").formatted(Formatting.BOLD));

        Set<MutableText> commands = getCommandQueue().stream().map(Text::literal).collect(Collectors.toSet());
        if (commands.isEmpty()) {
            lines.add(Text.literal("No commands in queue :("));
        } else {
            lines.addAll(commands);
        }
    }

    public static void render(TextRenderer renderer, DrawContext context, ModConfig.CommandQueueConfig config) {
        if (!config.coreSettings.enabled) return;

        if (lines.isEmpty()) updateLines();

        renderElement(renderer, context, lines, config.coreSettings);
    }
}
