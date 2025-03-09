package io.github.brainage04.twitchplaysminecraft.command.core;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.*;
import io.github.brainage04.twitchplaysminecraft.command.admin.RegenerateAuthUrlCommand;
import io.github.brainage04.twitchplaysminecraft.command.admin.CommandQueueCommand;
import io.github.brainage04.twitchplaysminecraft.command.admin.StopItCommand;
import io.github.brainage04.twitchplaysminecraft.command.argument.ClientBlockPosArgumentType;
import io.github.brainage04.twitchplaysminecraft.command.argument.ClientIdentifierArgumentType;
import io.github.brainage04.twitchplaysminecraft.command.attack.KillMobCommands;
import io.github.brainage04.twitchplaysminecraft.command.craft.CraftCommand;
import io.github.brainage04.twitchplaysminecraft.command.drop.DropCommand;
import io.github.brainage04.twitchplaysminecraft.command.goal.*;
import io.github.brainage04.twitchplaysminecraft.command.key.*;
import io.github.brainage04.twitchplaysminecraft.command.look.FaceBlockCommand;
import io.github.brainage04.twitchplaysminecraft.command.look.FaceCommand;
import io.github.brainage04.twitchplaysminecraft.command.look.FaceEntityCommand;
import io.github.brainage04.twitchplaysminecraft.command.look.LookCommand;
import io.github.brainage04.twitchplaysminecraft.command.mine.MineCommand;
import io.github.brainage04.twitchplaysminecraft.command.mine.StripMineCommand;
import io.github.brainage04.twitchplaysminecraft.command.move.MoveToCommand;
import io.github.brainage04.twitchplaysminecraft.command.move.MoveDirectionCommands;
import io.github.brainage04.twitchplaysminecraft.command.move.FindPathCommand;
import io.github.brainage04.twitchplaysminecraft.command.screen.CloseScreenCommand;
import io.github.brainage04.twitchplaysminecraft.command.screen.MoveItemCommand;
import io.github.brainage04.twitchplaysminecraft.command.screen.QuickMoveCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.BridgeCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.JumpPlaceCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.UseItemCommand;
import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.hud.core.HUDElementEditor;
import io.github.brainage04.twitchplaysminecraft.util.enums.LookDirection;
import io.github.brainage04.twitchplaysminecraft.util.enums.key.IfKey;
import io.github.brainage04.twitchplaysminecraft.util.enums.key.MoveDirectionKeys;
import io.github.brainage04.twitchplaysminecraft.util.enums.key.WhileKey;
import io.github.brainage04.twitchplaysminecraft.util.enums.key.ToggleKey;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClientCommands {
    public static final String[] closeScreenCommands = new String[]{"esc", "escape", "close", "closescreen"};

    public static void initialize() {
        // config commands
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal(TwitchPlaysMinecraft.MOD_ID_SHORT + "config")
                        .executes(context -> {
                            MinecraftClient.getInstance().send(() -> context.getSource().getClient().setScreen(
                                    AutoConfig.getConfigScreen(ModConfig.class, context.getSource().getClient().currentScreen).get()
                            ));
                            return 1;
                        })
                )
        ));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal(TwitchPlaysMinecraft.MOD_ID_SHORT + "gui")
                        .executes(context -> {
                            MinecraftClient.getInstance().send(() -> context.getSource().getClient().setScreen(
                                    new HUDElementEditor()
                            ));
                            return 1;
                        })
                )
        ));

        // admin commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("commandqueue")
                                .then(literal("add")
                                        .then(argument("command", StringArgumentType.string())
                                                .executes(context ->
                                                        CommandQueueCommand.executeAdd(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "command")
                                                        )
                                                )
                                        )
                                )
                                .then(literal("clear")
                                        .executes(context ->
                                                CommandQueueCommand.executeClear(
                                                        context.getSource()
                                                )
                                        )
                                )
                                .then(literal("process")
                                        .executes(context ->
                                                CommandQueueCommand.executeProcess(
                                                        context.getSource()
                                                )
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("regenerateauthurl")
                                .executes(context ->
                                        RegenerateAuthUrlCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("stopit")
                                .executes(context ->
                                        StopItCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );

        // attack commands
        KillMobCommands.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("killnearestmob")
                                .executes(context ->
                                        KillMobCommands.executeMelee(
                                                context.getSource()
                                        )
                                )
                                .then(argument("entity", ClientIdentifierArgumentType.identifier())
                                        .suggests(ClientSuggestionProviders.LIVING_ENTITY_TYPES)
                                        .executes(context ->
                                                KillMobCommands.executeMelee(
                                                        context.getSource(),
                                                        ClientIdentifierArgumentType.getIdentifier(context, "entity")
                                                )
                                        )
                                )
                                // todo: implement this
                                /*
                                .then(literal("melee")

                                )
                                .then(literal("ranged")

                                )
                                 */
                )
        );

        // craft commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("craft")
                                .then(literal("item")
                                        .then(argument("item", StringArgumentType.string())
                                                .executes(context ->
                                                        CraftCommand.execute(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "item"),
                                                                1
                                                        )
                                                )
                                                .then(argument("count", IntegerArgumentType.integer(1, 64))
                                                        .executes(context ->
                                                                CraftCommand.execute(
                                                                        context.getSource(),
                                                                        StringArgumentType.getString(context, "item"),
                                                                        IntegerArgumentType.getInteger(context, "count")
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(literal("recipe")
                                        .then(argument("index", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        CraftCommand.executeRecipe(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "index") - 1,
                                                                1
                                                        )
                                                )
                                                .then(argument("count", IntegerArgumentType.integer(1, 64))
                                                        .executes(context ->
                                                                CraftCommand.executeRecipe(
                                                                        context.getSource(),
                                                                        IntegerArgumentType.getInteger(context, "index") - 1,
                                                                        IntegerArgumentType.getInteger(context, "count")
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(literal("entry")
                                        .then(argument("index", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        CraftCommand.executeEntry(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "index") - 1,
                                                                1
                                                        )
                                                )
                                                .then(argument("count", IntegerArgumentType.integer(1, 64))
                                                        .executes(context ->
                                                                CraftCommand.executeEntry(
                                                                        context.getSource(),
                                                                        IntegerArgumentType.getInteger(context, "index") - 1,
                                                                        IntegerArgumentType.getInteger(context, "count")
                                                                )
                                                        )
                                                )
                                        )
                                )
                )
        );

        // drop commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("drop")
                                .then(argument("slot", IntegerArgumentType.integer())
                                        .then(argument("count", IntegerArgumentType.integer(1, 64))
                                                .executes(context ->
                                                        DropCommand.execute(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "slot"),
                                                                IntegerArgumentType.getInteger(context, "count")
                                                        )
                                                )
                                        )
                                        .then(literal("all")
                                                .executes(context ->
                                                        DropCommand.executeDropAll(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "slot")
                                                        )
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("drophelditem")
                                .then(argument("count", IntegerArgumentType.integer())
                                        .executes(context ->
                                                DropCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "count")
                                                )
                                        )
                                )
                                .then(literal("all")
                                        .executes(context ->
                                                DropCommand.executeDropAll(
                                                        context.getSource()
                                                )
                                        )
                                )
                )
        );

        // goal commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("clearcurrentgoal")
                                .executes(context ->
                                        ClearCurrentGoalCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("getcurrentgoal")
                                .executes(context ->
                                        GetCurrentGoalCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("getgoal")
                                .then(argument("advancementId", ClientIdentifierArgumentType.identifier())
                                        .suggests(ClientIdentifierArgumentType::suggestAllAdvancements)
                                        .executes(context ->
                                                GetGoalCommand.execute(
                                                        context.getSource(),
                                                        ClientIdentifierArgumentType.getIdentifier(context, "advancementId")
                                                )
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("selectablegoals")
                                .executes(context ->
                                        SelectableGoalsCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("selectcurrentgoal")
                                .then(argument("advancementId", ClientIdentifierArgumentType.identifier())
                                        .suggests(ClientIdentifierArgumentType::suggestSelectableAdvancements)
                                        .executes(context ->
                                                SelectCurrentGoalCommand.execute(
                                                        context.getSource(),
                                                        ClientIdentifierArgumentType.getIdentifier(context, "advancementId")
                                                )
                                        )
                                )
                )
        );

        // key commands
        for (IfKey ifKey : IfKey.values()) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                            literal(ifKey.getName())
                                    .executes(context ->
                                            IfKeyCommands.execute(
                                                    context.getSource(),
                                                    ifKey.function.apply(context.getSource().getClient().options)
                                            )
                                    )
                    )
            );
        }

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("releaseallkeys")
                                .executes(context ->
                                        ReleaseAllKeysCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );

        ToggleKeyCommands.initialize();
        for (ToggleKey toggleKey : ToggleKey.values()) {
            List<String> names = new ArrayList<>(List.of(toggleKey.getName()));
            names.addAll(List.of(toggleKey.otherNames));

            for (String name : names) {
                for (String prefix : new String[]{"toggle", "hold"}) {
                    ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                                    literal(prefix + name)
                                            .executes(context ->
                                                    ToggleKeyCommands.execute(
                                                            context.getSource(),
                                                            toggleKey.function.apply(context.getSource().getClient().options)
                                                    )
                                            )
                            )
                    );
                }
            }
        }

        for (WhileKey whileKey : WhileKey.values()) {
            List<String> names = new ArrayList<>(List.of(whileKey.getName()));
            names.addAll(List.of(whileKey.otherNames));

            for (String name : names) {
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                                literal(name)
                                        .executes(context ->
                                                WhileKeyCommands.execute(
                                                        context.getSource(),
                                                        whileKey.function.apply(context.getSource().getClient().options)
                                                )
                                        )
                        )
                );
            }
        }

        // look commands
        for (String name : new String[]{"face", "look"}) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                            literal(name)
                                    .then(argument("direction", StringArgumentType.string())
                                            .suggests(ClientSuggestionProviders.CARDINAL_DIRECTION_SUGGESTIONS)
                                            .executes(context ->
                                                    FaceCommand.execute(
                                                            context.getSource(),
                                                            StringArgumentType.getString(context, "direction")
                                                    )
                                            )
                                    )
                    )
            );
        }

        for (String prefix : new String[]{"face", "lookat"}) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                            literal(prefix + "block")
                                    .then(argument("blockName", StringArgumentType.string())
                                            .executes(context ->
                                                    FaceBlockCommand.execute(
                                                            context.getSource(),
                                                            StringArgumentType.getString(context, "blockName")
                                                    )
                                            )
                                    )
                    )
            );

            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                            literal(prefix + "entity")
                                    .then(argument("entityName", StringArgumentType.string())
                                            .executes(context ->
                                                    FaceEntityCommand.execute(
                                                            context.getSource(),
                                                            StringArgumentType.getString(context, "entityName")
                                                    )
                                            )
                                    )
                    )
            );
        }

        for (LookDirection lookDirection : LookDirection.values()) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                            literal("look" + lookDirection.getName())
                                    .then(argument("degrees", IntegerArgumentType.integer(1))
                                            .executes(context ->
                                                    LookCommand.execute(
                                                            context.getSource(),
                                                            lookDirection,
                                                            IntegerArgumentType.getInteger(context, "degrees")
                                                    )
                                            )
                                    )
                    )
            );
        }

        // mine commands
        MineCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("mine")
                                .then(argument("block", StringArgumentType.string())
                                        .executes(context ->
                                                MineCommand.execute(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "block"),
                                                        1
                                                )
                                        )
                                        .then(argument("count", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        MineCommand.execute(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "block"),
                                                                IntegerArgumentType.getInteger(context, "count")
                                                        )
                                                )
                                        )
                                )
                )
        );

        StripMineCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("stripmine")
                                .executes(context ->
                                        StripMineCommand.execute(
                                                context.getSource()
                                        )
                                )
                                .then(argument("count", IntegerArgumentType.integer())
                                        .executes(context ->
                                                StripMineCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "count")
                                                )
                                        )
                                )
                )
        );

        // move commands
        MoveDirectionCommands.initialize();
        for (MoveDirectionKeys moveDirectionKeys : MoveDirectionKeys.values()) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                            literal(moveDirectionKeys.getName())
                                    .then(argument("amount", IntegerArgumentType.integer())
                                            .then(literal("blocks")
                                                    .executes(context ->
                                                            MoveDirectionCommands.executeDistance(
                                                                    context.getSource(),
                                                                    moveDirectionKeys.function.apply(context.getSource().getClient().options),
                                                                    IntegerArgumentType.getInteger(context, "amount")
                                                            )
                                                    )
                                            )
                                            .then(literal("ticks")
                                                    .executes(context ->
                                                            MoveDirectionCommands.executeTime(
                                                                    context.getSource(),
                                                                    moveDirectionKeys.function.apply(context.getSource().getClient().options),
                                                                    IntegerArgumentType.getInteger(context, "amount")
                                                            )
                                                    )
                                            )
                                    )
                    )
            );
        }

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("findpath")
                        .then(argument("pos", ClientBlockPosArgumentType.blockPos())
                                .executes(context ->
                                        FindPathCommand.execute(
                                                context.getSource(),
                                                ClientBlockPosArgumentType.getBlockPos(context, "pos")
                                        )
                                )
                        )
                )
        );

        MoveToCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("moveto")
                        .then(argument("pos", ClientBlockPosArgumentType.blockPos())
                                .executes(context ->
                                        MoveToCommand.execute(
                                                context.getSource(),
                                                ClientBlockPosArgumentType.getBlockPos(context, "pos")
                                        )
                                )
                        )
                )
        );

        // screen commands
        for (String string : closeScreenCommands) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                            literal(string)
                                    .executes(context ->
                                            CloseScreenCommand.execute(
                                                    context.getSource()
                                            )
                                    )
                    )
            );
        }

        MoveItemCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("moveitem")
                                .then(argument("first", IntegerArgumentType.integer())
                                        .then(argument("second", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        MoveItemCommand.execute(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "first"),
                                                                IntegerArgumentType.getInteger(context, "second")
                                                        )
                                                )
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("quickmove")
                                .then(argument("slot", IntegerArgumentType.integer())
                                        .executes(context ->
                                                QuickMoveCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "slot")
                                                )
                                        )
                                )
                )
        );

        // use commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("bridge")
                                .then(argument("direction", StringArgumentType.string())
                                        .executes(context ->
                                                BridgeCommand.execute(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "direction"),
                                                        1
                                                )
                                        )
                                        .suggests(ClientSuggestionProviders.CARDINAL_DIRECTION_SUGGESTIONS)
                                        .then(argument("count", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        BridgeCommand.execute(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "direction"),
                                                                IntegerArgumentType.getInteger(context, "count")
                                                        )
                                                )
                                        )
                                )

                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("jumpplace")
                                .executes(context ->
                                        JumpPlaceCommand.execute(
                                                context.getSource(),
                                                1
                                        )
                                )
                                .then(argument("count", IntegerArgumentType.integer())
                                        .executes(context ->
                                                JumpPlaceCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "count")
                                                )
                                        )
                                )
                )
        );

        UseItemCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("useitem")
                                .executes(context ->
                                        UseItemCommand.execute(
                                                context.getSource(),
                                                1
                                        )
                                )
                                .then(argument("count", IntegerArgumentType.integer())
                                        .executes(context ->
                                                UseItemCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "count")
                                                )
                                        )
                                )
                )
        );

        // other commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("hotbar")
                                .then(argument("slot", IntegerArgumentType.integer())
                                        .executes(context ->
                                                HotbarCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "slot")
                                                )
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("locatestructure")
                                .then(argument("structure", StringArgumentType.string())
                                        .suggests(ClientSuggestionProviders.IMPORTANT_STRUCTURES)
                                        .executes(context ->
                                                LocateStructureCommand.execute(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "structure")
                                                )
                                        )
                                )
                )
        );
    }
}
