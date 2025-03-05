package io.github.brainage04.twitchplaysminecraft.hud;

import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static io.github.brainage04.twitchplaysminecraft.hud.core.HUDRenderer.renderElement;

// todo: text.empty.append? why...
public class GoalHud {
    private static final List<Text> lines = new ArrayList<>();

    public static void updateLines() {
        lines.clear();

        lines.add(Text.literal("Previously Achieved Goals").formatted(Formatting.BOLD));

        for (PlacedAdvancement placedAdvancement : AdvancementUtils.getAchievedAdvancements()) {
            lines.add(Text.empty().append(AdvancementUtils.getAdvancementName(placedAdvancement)));
        }

        lines.add(Text.literal("Current Goal").formatted(Formatting.BOLD));

        if (AdvancementUtils.getCurrentAdvancement() == null) {
            lines.add(Text.literal("No current goal :("));
            return;
        }

        lines.add(Text.empty().append(AdvancementUtils.getAdvancementName(AdvancementUtils.getCurrentAdvancement())));

        Text description = AdvancementUtils.getAdvancementDescription(AdvancementUtils.getCurrentAdvancement());
        if (description != null) lines.add(description);

        lines.add(Text.literal("ID: %s".formatted(AdvancementUtils.getCurrentAdvancement().getAdvancementEntry().id().toString())));
    }

    public static void render(TextRenderer renderer, DrawContext context, ModConfig.GoalConfig config) {
        if (!config.coreSettings.displayEnabled) return;

        if (lines.isEmpty()) updateLines();

        renderElement(renderer, context, lines, config.coreSettings);
    }
}
