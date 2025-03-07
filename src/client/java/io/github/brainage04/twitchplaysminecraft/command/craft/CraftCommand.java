package io.github.brainage04.twitchplaysminecraft.command.craft;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

import static io.github.brainage04.twitchplaysminecraft.util.CommandUtils.millisecondsBetweenSteps;
import static io.github.brainage04.twitchplaysminecraft.util.CommandUtils.startNewCurrentInteractionThread;
import static io.github.brainage04.twitchplaysminecraft.util.ThreadUtils.sleepSafely;

// todo: convert from thread-based to tick-based
@SuppressWarnings("SameReturnValue")
public class CraftCommand {
    private static boolean isRunning = false;

    private static List<AnimatedResultButton> visibleButtons;

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {

        });
    }

    public static int stop() {
        isRunning = false;

        return 1;
    }

    public static int execute(FabricClientCommandSource source, String itemName, int count) {
        if (!(source.getClient().currentScreen instanceof CraftingScreen craftingScreen)) {
            new ClientFeedbackBuilder().source(source)
                    .text("Crafting Table GUI is not open!")
                    .messageType(MessageType.ERROR)
                    .execute();
            return 0;
        }

        // open recipe book widget
        if (!craftingScreen.recipeBook.isOpen()) {
            startNewCurrentInteractionThread(new Thread(() -> {
                craftingScreen.recipeBook.setOpen(true);
                sleepSafely(millisecondsBetweenSteps);
                executeStepTwo(source, itemName, count);
            }));
        } else {
            executeStepTwo(source, itemName, count);
        }

        return 1;
    }

    public static void executeStepTwo(FabricClientCommandSource source, String itemName, int count) {
        if (!(source.getClient().currentScreen instanceof CraftingScreen craftingScreen)) {
            new ClientFeedbackBuilder().source(source)
                    .text("Crafting Table GUI is not open!")
                    .messageType(MessageType.ERROR)
                    .execute();
            return;
        }

        // craftable recipes only
        craftingScreen.recipeBook.toggleCraftableButton.setToggled(true);

        // enter item name and refresh results
        if (craftingScreen.recipeBook.searchField == null) {
            new ClientFeedbackBuilder().source(source)
                    .text("Recipe book search field does not exist!")
                    .messageType(MessageType.ERROR)
                    .execute();
            return;
        }
        craftingScreen.recipeBook.searchField.setText(itemName);
        craftingScreen.recipeBook.refresh();

        // get results
        visibleButtons = craftingScreen.recipeBook.recipesArea.resultButtons.stream().filter(button -> button.visible).toList();
        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Craftable recipes found: %d".formatted(visibleButtons.size()))
                .execute();

        switch (visibleButtons.size()) {
            case 0 -> new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No recipes found! Please try again.")
                    .execute();
            case 1 -> {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.SUCCESS)
                        .text("One recipe found! Crafting recipe...")
                        .execute();
                executeRecipe(source, 0, count);
            }
            default -> new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text("More than one recipe found! Please use !craft recipe <index> [<count>] to pick a recipe.")
                    .execute();
        }
    }

    private static void takeItemFromOutputSlot(FabricClientCommandSource source, CraftingScreen craftingScreen) {
        Slot outputSlot = craftingScreen.getScreenHandler().getOutputSlot();
        craftingScreen.onMouseClick(outputSlot, outputSlot.id, 0, SlotActionType.QUICK_MOVE);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Crafting complete.")
                .execute();
    }

    public static int executeRecipe(FabricClientCommandSource source, int recipeIndex, int count) {
        if (!(source.getClient().currentScreen instanceof CraftingScreen craftingScreen)) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Crafting Table GUI is not open!")
                    .execute();
            return 0;
        }

        // get appropriate button
        if (recipeIndex < 0 || recipeIndex > visibleButtons.size() - 1) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Index is out of bounds! Please choose an index between %d and %d.".formatted(1, visibleButtons.size()))
                    .execute();
            return 0;
        }
        AnimatedResultButton button = visibleButtons.get(recipeIndex);

        double mouseX = button.getX() + (double) button.getWidth() / 2;
        double mouseY = button.getY() + (double) button.getHeight() / 2;

        // if there is only one entry, simulate "count" number of clicks on button and then a shift click on the output slot
        // if there is more than one, let viewers choose which one to craft
        if (button.hasSingleResult()) {
            startNewCurrentInteractionThread(new Thread(() -> {
                for (int i = 0; i < count; i++) {
                    craftingScreen.recipeBook.mouseClicked(mouseX, mouseY, 0);
                    sleepSafely(millisecondsBetweenSteps);
                    takeItemFromOutputSlot(source, craftingScreen);
                    if (i != count - 1) {
                        sleepSafely(millisecondsBetweenSteps);
                    }
                }
            }));
        } else {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("More than one entry for this recipe! Please use !craft entry <index> [<count>] to pick an entry.")
                    .execute();

            // right click button to populate alternative buttons
            craftingScreen.recipeBook.mouseClicked(mouseX, mouseY, 1);
        }

        return 1;
    }

    public static int executeEntry(FabricClientCommandSource source, int entryIndex, int count) {
        if (!(source.getClient().currentScreen instanceof CraftingScreen craftingScreen)) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Crafting Table GUI is not open!")
                    .execute();
            return 0;
        }

        // get appropriate entry
        List<RecipeAlternativesWidget.AlternativeButtonWidget> alternativeButtons = craftingScreen.recipeBook.recipesArea.alternatesWidget.alternativeButtons;
        if (entryIndex < 0 || entryIndex > alternativeButtons.size() - 1) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Index is out of bounds! Please choose an index between %d and %d.".formatted(1, alternativeButtons.size()))
                    .execute();
            return 0;
        }
        RecipeAlternativesWidget.CraftingAlternativeButtonWidget alternativeButton = (RecipeAlternativesWidget.CraftingAlternativeButtonWidget) craftingScreen.recipeBook.recipesArea.alternatesWidget.alternativeButtons.get(entryIndex);

        double mouseX = alternativeButton.getX() + (double) alternativeButton.getWidth() / 2;
        double mouseY = alternativeButton.getY() + (double) alternativeButton.getHeight() / 2;

        // simulate clicks on entry and output slot
        startNewCurrentInteractionThread(new Thread(() -> {
            for (int i = 0; i < count; i++) {
                craftingScreen.recipeBook.mouseClicked(mouseX, mouseY, 0);
                sleepSafely(millisecondsBetweenSteps);
                takeItemFromOutputSlot(source, craftingScreen);
                if (i != count - 1) {
                    sleepSafely(millisecondsBetweenSteps);
                }
            }
        }));

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
