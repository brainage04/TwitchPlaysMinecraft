package io.github.brainage04.twitchplaysminecraft.command.core;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.LookCommand;
import io.github.brainage04.twitchplaysminecraft.command.MoveItemCommand;
import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModSuggestionProviders {
    public static final String[] moveActionSuggestionsStringArray = Arrays.stream(MoveItemCommand.ActionType.values()).map(value -> value.name().toLowerCase()).toArray(String[]::new);
    public static final SuggestionProvider<FabricClientCommandSource> MOVE_ACTION_SUGGESTIONS = SuggestionProviders.register(
            Identifier.of(TwitchPlaysMinecraft.MOD_ID, "move_action_suggestions"),
            (context, builder) -> CommandSource.suggestMatching(
                    moveActionSuggestionsStringArray,
                    builder
            )
    );

    public static final String[] lookDirectionSuggestionsStringArray = Arrays.stream(LookCommand.LookDirection.values()).map(value -> value.name().toLowerCase()).toArray(String[]::new);
    public static final SuggestionProvider<FabricClientCommandSource> LOOK_DIRECTION_SUGGESTIONS = SuggestionProviders.register(
            Identifier.of(TwitchPlaysMinecraft.MOD_ID, "look_direction_suggestions"),
            (context, builder) -> CommandSource.suggestMatching(
                    lookDirectionSuggestionsStringArray,
                    builder
            )
    );

    public static final SuggestionProvider<ServerCommandSource> ADVANCEMENT_SUGGESTIONS = SuggestionProviders.register(
            Identifier.of(TwitchPlaysMinecraft.MOD_ID, "advancement_suggestions"),
            (context, builder) -> CommandSource.suggestMatching(
                    AdvancementUtils.getAllAdvancements().stream().map(placedAdvancement -> placedAdvancement.getAdvancementEntry().id().toString()).collect(Collectors.toSet()),
                    builder
            )
    );
}
