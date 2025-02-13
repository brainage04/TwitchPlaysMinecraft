package io.github.brainage04.twitchplaysminecraft.hud;

import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static io.github.brainage04.twitchplaysminecraft.hud.core.HUDRenderer.renderElement;

public class AdvancementTrackingHud {
    public static void render(TextRenderer renderer, DrawContext context, ModConfig.AdvancementTrackingConfig config) {
        if (!config.coreSettings.enabled) return;
        if (AdvancementUtils.getCurrentAdvancement() == null) return;

        List<Text> lines = new ArrayList<>();

        if (AdvancementUtils.getCurrentAdvancement().getAdvancement().name().isPresent()) {
            lines.add(Text.literal("Current goal: ").append(AdvancementUtils.getCurrentAdvancement().getAdvancement().name().get()));
        }

        lines.add(Text.literal("Prerequisites: "));

        for (PlacedAdvancement prerequisite : AdvancementUtils.getPrerequisites()) {
            if (prerequisite.getAdvancement().name().isEmpty()) continue;
            lines.add(prerequisite.getAdvancement().name().get());
        }

        renderElement(renderer, context, lines, config.coreSettings);
    }
}
