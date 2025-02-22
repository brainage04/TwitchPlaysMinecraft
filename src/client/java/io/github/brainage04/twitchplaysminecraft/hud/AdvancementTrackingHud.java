package io.github.brainage04.twitchplaysminecraft.hud;

import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static io.github.brainage04.twitchplaysminecraft.hud.core.HUDRenderer.renderElement;

public class AdvancementTrackingHud {
    public static void render(TextRenderer renderer, DrawContext context, ModConfig.AdvancementTrackingConfig config) {
        if (!config.coreSettings.enabled) return;
        if (AdvancementUtils.currentAdvancement == null) return;

        List<Text> lines = new ArrayList<>();

        lines.add(Text.literal("Current goal: ").append(AdvancementUtils.getAdvancementName(AdvancementUtils.currentAdvancement)));
        if (AdvancementUtils.currentAdvancement.getParent() != null) {
            lines.add(Text.literal("Prerequisites: ").append(AdvancementUtils.getAdvancementName(AdvancementUtils.currentAdvancement.getParent())));
        }

        renderElement(renderer, context, lines, config.coreSettings);
    }
}
