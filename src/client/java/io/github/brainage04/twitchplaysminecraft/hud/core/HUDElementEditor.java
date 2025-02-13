package io.github.brainage04.twitchplaysminecraft.hud.core;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.util.MathUtils;
import io.github.brainage04.twitchplaysminecraft.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.*;

public class HUDElementEditor extends Screen {
    public final List<ModConfig.CoreSettings> elementList;

    public int selectedElementIndex = -1;

    public double selectedElementX = -1;
    public double selectedElementY = -1;

    public double selectedMouseX = -1;
    public double selectedMouseY = -1;

    public int highlightedElementIndex = -1;

    private Integer previousMenuBackgroundBlurriness;

    public HUDElementEditor() {
        super(Text.of(TwitchPlaysMinecraft.MOD_NAME + " Element Editor"));

        this.elementList = loadElementSettings();

        // refresh element corners
        RenderUtils.elementCorners.clear();
        while (RenderUtils.elementCorners.size() < elementList.size()) {
            RenderUtils.elementCorners.add(new int[]{-1, -1, -1, -1});
        }

        // disable blur while editing elements
        disableBlur();
    }

    public void disableBlur() {
        previousMenuBackgroundBlurriness = MinecraftClient.getInstance().options.getMenuBackgroundBlurriness().getValue();
        MinecraftClient.getInstance().options.getMenuBackgroundBlurriness().setValue(0);
    }

    public void revertBlur() {
        MinecraftClient.getInstance().options.getMenuBackgroundBlurriness().setValue(previousMenuBackgroundBlurriness);
    }

    private List<ModConfig.CoreSettings> loadElementSettings() {
        return new ArrayList<>(List.of(
                getConfig().commandQueueConfig.coreSettings,
                getConfig().advancementTrackingConfig.coreSettings
        ));
    }

    public int mouseInElement(double mouseX, double mouseY) {
        for (int i = 0; i < elementList.size(); i++) {
            int[] corners = RenderUtils.elementCorners.get(i);

            if (RenderUtils.mouseInRect(
                    corners[0],
                    corners[1],
                    corners[2],
                    corners[3],
                    mouseX,
                    mouseY
            )) {
                return i;
            }
        }

        return -1;
    }

    public final ButtonWidget button1 = ButtonWidget.builder(Text.literal("Undo & Close"), button -> closeWithoutSaving())
            .dimensions(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 - 210, MinecraftClient.getInstance().getWindow().getScaledHeight() - 40, 200, 20)
            .tooltip(Tooltip.of(Text.literal("Reverts the current positions to what they were before and closes the screen.")))
            .build();
    public final ButtonWidget button2 = ButtonWidget.builder(Text.literal("Save & Close"), button -> this.close())
            .dimensions(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 + 10, MinecraftClient.getInstance().getWindow().getScaledHeight() - 40, 200, 20)
            .tooltip(Tooltip.of(Text.literal("Saves the current positions and closes the screen.")))
            .build();

    public void updateElementPosition(int deltaX, int deltaY) {
        int[] selectedElementCorners = RenderUtils.elementCorners.get(selectedElementIndex);
        int elementWidth = selectedElementCorners[2] - selectedElementCorners[0];
        int elementHeight = selectedElementCorners[3] - selectedElementCorners[1];

        int minX = switch (elementList.get(selectedElementIndex).elementAnchor) {
            case TOPRIGHT, RIGHT, BOTTOMRIGHT -> getConfig().screenMargin - (RenderUtils.getScaledWidth() - elementWidth);
            case TOP, CENTER, BOTTOM -> getConfig().screenMargin - (RenderUtils.getScaledWidth() - elementWidth) / 2;
            default -> getConfig().screenMargin;
        };
        int minY = switch (elementList.get(selectedElementIndex).elementAnchor) {
            case BOTTOMLEFT, BOTTOM, BOTTOMRIGHT -> getConfig().screenMargin - (RenderUtils.getScaledHeight() - elementHeight);
            case LEFT, CENTER, RIGHT -> getConfig().screenMargin - (RenderUtils.getScaledHeight() - elementHeight) / 2;
            default -> getConfig().screenMargin;
        };

        elementList.get(selectedElementIndex).x = (int) (MathHelper.clamp(
                selectedElementX - deltaX,
                minX,
                minX + RenderUtils.getScaledWidth() - elementWidth - getConfig().screenMargin * 2
        ));
        elementList.get(selectedElementIndex).y = (int) (MathHelper.clamp(
                selectedElementY - deltaY,
                minY,
                minY + RenderUtils.getScaledHeight() - elementHeight - getConfig().screenMargin * 2
        ));
    }

    @Override
    protected void init() {
        addDrawableChild(button1);
        addDrawableChild(button2);
    }

    @Override
    public void close() {
        // update config values
        getConfig().commandQueueConfig.coreSettings = elementList.get(0);
        getConfig().advancementTrackingConfig.coreSettings = elementList.get(1);

        saveConfig();

        // return blur to previous value
        revertBlur();

        super.close();
    }

    public void closeWithoutSaving() {
        loadConfig();

        // return blur to previous value
        revertBlur();

        super.close();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // from package net.minecraft.client.gui.screen.Screen;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            closeWithoutSaving();
            return true;
        }

        if (selectedElementIndex != -1) {
            int xDirection = 0;
            int yDirection = 0;

            switch (keyCode) {
                case GLFW.GLFW_KEY_UP:
                    yDirection = 1;
                    break;
                case GLFW.GLFW_KEY_DOWN:
                    yDirection = -1;
                    break;
                case GLFW.GLFW_KEY_LEFT:
                    xDirection = 1;
                    break;
                case GLFW.GLFW_KEY_RIGHT:
                    xDirection = -1;
                    break;
            }

            // if no arrow keys were pressed, return
            if (xDirection == 0 && yDirection == 0) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            // deal with modifiers here
            // https://www.glfw.org/docs/3.3/group__mods.html
            // shift keys
            if (MathUtils.isBitOn(modifiers, 0)) {
                xDirection *= 10;
                yDirection *= 10;
            }
            // ctrl keys
            if (MathUtils.isBitOn(modifiers, 1)) {
                xDirection *= 5;
                yDirection *= 5;
            }

            // update selected element position
            selectedElementX = elementList.get(selectedElementIndex).x;
            selectedElementY = elementList.get(selectedElementIndex).y;

            // update position in config
            updateElementPosition(xDirection, yDirection);
        }

        //return super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        highlightedElementIndex = mouseInElement(mouseX, mouseY);

        List<Text> lines = new ArrayList<>(List.of(title));

        // render backdrops
        // if both indices are the same, use highlighted
        // otherwise, render separately
        if (highlightedElementIndex != -1 && highlightedElementIndex == selectedElementIndex) {
            context.fill(
                    RenderUtils.elementCorners.get(highlightedElementIndex)[0],
                    RenderUtils.elementCorners.get(highlightedElementIndex)[1],
                    RenderUtils.elementCorners.get(highlightedElementIndex)[2],
                    RenderUtils.elementCorners.get(highlightedElementIndex)[3],
                    0x7fffffff
            );
        } else {
            if (highlightedElementIndex != -1) {
                context.fill(
                        RenderUtils.elementCorners.get(highlightedElementIndex)[0],
                        RenderUtils.elementCorners.get(highlightedElementIndex)[1],
                        RenderUtils.elementCorners.get(highlightedElementIndex)[2],
                        RenderUtils.elementCorners.get(highlightedElementIndex)[3],
                        0x7fffffff
                );
            }

            if (selectedElementIndex != -1) {
                context.fill(
                        RenderUtils.elementCorners.get(selectedElementIndex)[0],
                        RenderUtils.elementCorners.get(selectedElementIndex)[1],
                        RenderUtils.elementCorners.get(selectedElementIndex)[2],
                        RenderUtils.elementCorners.get(selectedElementIndex)[3],
                        0x7fffffff
                );
            }
        }

        // render element information
        if (highlightedElementIndex != -1) {
            lines.add(Text.of(elementList.get(highlightedElementIndex).elementName));
            lines.add(Text.of("X: %d Y: %d".formatted(elementList.get(highlightedElementIndex).x, elementList.get(highlightedElementIndex).y)));
            lines.add(Text.of("Anchor: %s".formatted(elementList.get(highlightedElementIndex).elementAnchor.name())));

            if (highlightedElementIndex == selectedElementIndex) {
                lines.add(Text.of("Highlighted & Selected"));
            } else {
                lines.add(Text.of("Highlighted"));
            }
        } else if (selectedElementIndex != -1) {
            lines.add(Text.of(elementList.get(selectedElementIndex).elementName));
            lines.add(Text.of("X: %d Y: %d".formatted(elementList.get(selectedElementIndex).x, elementList.get(selectedElementIndex).y)));
            lines.add(Text.of("Anchor: %s".formatted(elementList.get(selectedElementIndex).elementAnchor.name())));
            lines.add(Text.of("Selected"));
        }

        for (int i = 0; i < lines.size(); i++) {
            context.drawCenteredTextWithShadow(textRenderer, lines.get(i), width / 2, 10 + (textRenderer.fontHeight + 2) * i, 0xffffff);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        selectedElementIndex = mouseInElement(mouseX, mouseY);

        if (selectedElementIndex != -1) {
            selectedElementX = elementList.get(selectedElementIndex).x;
            selectedElementY = elementList.get(selectedElementIndex).y;

            selectedMouseX = mouseX;
            selectedMouseY = mouseY;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (selectedElementIndex != -1) {
            updateElementPosition((int) (selectedMouseX - mouseX), (int) (selectedMouseY - mouseY));
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
