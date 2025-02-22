package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdvancementUtils {
    public static PlacedAdvancement currentAdvancement = getAdvancementById(Identifier.ofVanilla("story/mine_stone"));

    public static Text getAdvancementName(PlacedAdvancement placedAdvancement) {
        return placedAdvancement.getAdvancement().name()
                .orElse(Text.literal(AdvancementUtils.currentAdvancement.getAdvancementEntry().id().toString()));
    }

    public static PlacedAdvancement getAdvancementById(Identifier id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) return null;

        return client.getNetworkHandler().getAdvancementHandler().getManager().get(id);
    }

    public static List<PlacedAdvancement> getAvailableAdvancements() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || client.getNetworkHandler() == null) return List.of();

        return client.getNetworkHandler().getAdvancementHandler().getManager().getAdvancements().stream()
                .filter(advancement -> canSelectAdvancement(player, advancement))
                .collect(Collectors.toList());
    }

    public static boolean canSelectAdvancement(ClientPlayerEntity player, PlacedAdvancement placedAdvancement) {
        Map<AdvancementEntry, AdvancementProgress> advancementProgresses = player.networkHandler.getAdvancementHandler().advancementProgresses;

        // check if advancement is already complete
        if (advancementProgresses.get(placedAdvancement.getAdvancementEntry()).isDone()) return false;
        // if advancement has no parent then there are no prerequisites
        PlacedAdvancement parent = placedAdvancement.getParent();
        if (parent == null) return true;
        // only the parent's progress needs to be checked (it is assumed that all previous advancements before the parent are done)
        return advancementProgresses.get(parent.getAdvancementEntry()).isDone();
    }
}
