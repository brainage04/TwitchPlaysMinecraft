package io.github.brainage04.twitchplaysminecraft.command.core;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.ActionType;
import io.github.brainage04.twitchplaysminecraft.util.enums.CardinalDirection;
import io.github.brainage04.twitchplaysminecraft.util.enums.LookDirection;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ClientSuggestionProviders {
    private static SuggestionProvider<FabricClientCommandSource> register(String key, String[] values) {
        return SuggestionProviders.register(
                Identifier.of(TwitchPlaysMinecraft.MOD_ID, key),
                (context, builder) -> CommandSource.suggestMatching(
                        values,
                        builder
                )
        );
    }

    public static final String[] moveActionSuggestionStrings = EnumUtils.getEnumNames(ActionType.class);
    public static final SuggestionProvider<FabricClientCommandSource> MOVE_ACTION_SUGGESTIONS = register("move_action_suggestions", moveActionSuggestionStrings);

    public static final String[] cardinalDirectionSuggestionStrings = EnumUtils.getEnumNames(CardinalDirection.class);
    public static final SuggestionProvider<FabricClientCommandSource> CARDINAL_DIRECTION_SUGGESTIONS = register("cardinal_direction_suggestions", cardinalDirectionSuggestionStrings);

    public static final String[] lookDirectionSuggestionStrings = EnumUtils.getEnumNames(LookDirection.class);
    public static final SuggestionProvider<FabricClientCommandSource> LOOK_DIRECTION_SUGGESTIONS = register("look_direction_suggestions", lookDirectionSuggestionStrings);

    public static final String[] entityTypeStrings = Registries.ENTITY_TYPE.stream().map(entityType -> EntityType.getId(entityType).toString()).toArray(String[]::new);
    public static final SuggestionProvider<FabricClientCommandSource> ENTITY_TYPES = register("entity_types", entityTypeStrings);
}
