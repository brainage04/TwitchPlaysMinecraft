package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdvancementUtils {
    private static PlacedAdvancement currentAdvancement = getAdvancementById(Identifier.ofVanilla("story/mine_stone"));
    private static List<PlacedAdvancement> prerequisites = getPrerequisitesFrom(currentAdvancement);

    public static PlacedAdvancement getCurrentAdvancement() {
        return currentAdvancement;
    }

    public static void setCurrentAdvancement(PlacedAdvancement placedAdvancement) {
        currentAdvancement = placedAdvancement;
        prerequisites = getPrerequisitesFrom(currentAdvancement);
    }

    public static List<PlacedAdvancement> getPrerequisites() {
        return prerequisites;
    }

    private static List<PlacedAdvancement> getPrerequisitesFrom(PlacedAdvancement advancement) {
        List<PlacedAdvancement> prerequisites = new ArrayList<>();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) {
            return prerequisites;
        }

        ClientAdvancementManager advancementManager = client.getNetworkHandler().getAdvancementHandler();

        while (advancement.getParent() != null) {
            AdvancementProgress progress = advancementManager.advancementProgresses.get(advancement.getAdvancementEntry());
            if (progress != null && !progress.isDone()) {
                prerequisites.add(advancement);
            }

            advancement = advancement.getParent();
        }
        return prerequisites;
    }

    public static PlacedAdvancement getAdvancementById(Identifier id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) {
            return null;
        }

        ClientAdvancementManager advancementManager = client.getNetworkHandler().getAdvancementHandler();
        return advancementManager.getManager().get(id);
    }

    public static Collection<PlacedAdvancement> getAllAdvancements() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) {
            return null;
        }

        ClientAdvancementManager advancementManager = client.getNetworkHandler().getAdvancementHandler();
        return advancementManager.getManager().getAdvancements();
    }
}
