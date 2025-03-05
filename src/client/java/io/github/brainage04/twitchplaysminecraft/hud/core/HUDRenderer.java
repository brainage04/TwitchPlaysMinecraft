package io.github.brainage04.twitchplaysminecraft.hud.core;

import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.hud.GoalHud;
import io.github.brainage04.twitchplaysminecraft.hud.CommandQueueHud;
import io.github.brainage04.twitchplaysminecraft.util.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import java.util.List;

import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.getConfig;

// TODO: find out why this is deprecated
public class HUDRenderer implements HudRenderCallback {
    // for rendering HUD elements
    public static void renderElement(TextRenderer renderer, DrawContext context, List<Text> lines, ModConfig.CoreSettings coreSettings) {
        int elementWidth = 0;

        int lineHeight = renderer.fontHeight + getConfig().elementPadding;
        int elementHeight = lineHeight * lines.size() + getConfig().elementPadding;

        // vertical adjustments
        int posY = switch (coreSettings.elementAnchor) {
            case BOTTOMLEFT, BOTTOM, BOTTOMRIGHT -> coreSettings.y + (RenderUtils.getScaledHeight() - elementHeight);
            case LEFT, CENTER, RIGHT -> coreSettings.y + (RenderUtils.getScaledHeight() - elementHeight) / 2 + getConfig().elementPadding;
            default -> coreSettings.y + getConfig().elementPadding * 2;
        };

        for (int i = 0; i < lines.size(); i++) {
            int lineWidth = renderer.getWidth(lines.get(i));

            elementWidth = Math.max(elementWidth, lineWidth);

            // horizontal adjustments (for line)
            int posX = switch (coreSettings.elementAnchor) {
                case TOPRIGHT, RIGHT, BOTTOMRIGHT -> coreSettings.x + (RenderUtils.getScaledWidth() - lineWidth) - getConfig().elementPadding * 2;
                case TOP, CENTER, BOTTOM -> coreSettings.x + (RenderUtils.getScaledWidth() - lineWidth) / 2;
                default -> coreSettings.x + getConfig().elementPadding * 2;
            };

            context.drawText(
                    renderer,
                    lines.get(i),
                    posX,
                    posY + (lineHeight * i),
                    getConfig().primaryTextColour,
                    getConfig().textShadows
            );
        }

        // horizontal adjustments (for element)
        int posX = switch (coreSettings.elementAnchor) {
            case TOPRIGHT, RIGHT, BOTTOMRIGHT -> coreSettings.x + (RenderUtils.getScaledWidth() - elementWidth) - getConfig().elementPadding * 2;
            case TOP, CENTER, BOTTOM -> coreSettings.x + (RenderUtils.getScaledWidth() - elementWidth) / 2;
            default -> coreSettings.x + getConfig().elementPadding * 2;
        };

        // adjust for padding
        int[] corners = RenderUtils.getCornersWithPadding(posX, posY, posX + elementWidth, posY + elementHeight);

        // store corners for use in BrainageHUDElementEditor
        RenderUtils.elementCorners.get(coreSettings.elementId)[0] = corners[0];
        RenderUtils.elementCorners.get(coreSettings.elementId)[1] = corners[1];
        RenderUtils.elementCorners.get(coreSettings.elementId)[2] = corners[2];
        RenderUtils.elementCorners.get(coreSettings.elementId)[3] = corners[3];

        // render backdrop
        int backdropOpacity;

        if (coreSettings.overrideGlobalBackdropOpacity) {
            backdropOpacity = coreSettings.backdropOpacity;
        } else {
            backdropOpacity = getConfig().backdropOpacity;
        }

        if (backdropOpacity > 0) {
            context.fill(
                    corners[0],
                    corners[1],
                    corners[2],
                    corners[3],
                    -1,
                    backdropOpacity << 24
            );
        }
    }

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        CommandQueueHud.render(renderer, context, getConfig().commandQueueConfig);
        GoalHud.render(renderer, context, getConfig().goalConfig);
    }
}