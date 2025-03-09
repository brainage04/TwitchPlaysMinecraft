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

public class GoalHud {
    public static void render(TextRenderer renderer, DrawContext context, ModConfig.GoalConfig config) {
        if (!config.coreSettings.displayEnabled) return;

        List<Text> lines = new ArrayList<>();

        lines.add(Text.literal("Previously Achieved Goals").formatted(Formatting.BOLD));

        List<PlacedAdvancement> achievedAdvancements = AdvancementUtils.getAchievedAdvancements();

        if (achievedAdvancements.isEmpty()) {
            lines.add(Text.literal("No previously achieved goals :("));
        } else {
            for (PlacedAdvancement placedAdvancement : achievedAdvancements) {
                if (placedAdvancement.getAdvancement().name().isEmpty()) continue;

                lines.add(placedAdvancement.getAdvancement().name().get());
            }
        }

        lines.add(Text.literal("Current Goal").formatted(Formatting.BOLD));

        if (AdvancementUtils.getCurrentAdvancement() == null) {
            lines.add(Text.literal("No current goal :("));
        } else {
            lines.add(AdvancementUtils.getAdvancementName(AdvancementUtils.getCurrentAdvancement()));

            Text description = AdvancementUtils.getAdvancementDescription(AdvancementUtils.getCurrentAdvancement());
            if (description != null) lines.add(description);

            lines.add(Text.literal("ID: %s".formatted(AdvancementUtils.getCurrentAdvancement().getAdvancementEntry().id().toString())));
        }

        renderElement(renderer, context, lines, config.coreSettings);
    }
}
