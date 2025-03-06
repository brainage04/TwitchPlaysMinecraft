package io.github.brainage04.twitchplaysminecraft.command.core;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.*;
import io.github.brainage04.twitchplaysminecraft.command.admin.RegenerateAuthUrlCommand;
import io.github.brainage04.twitchplaysminecraft.command.admin.CommandQueueCommand;
import io.github.brainage04.twitchplaysminecraft.command.admin.StopItCommand;
import io.github.brainage04.twitchplaysminecraft.command.argument.ClientIdentifierArgumentType;
import io.github.brainage04.twitchplaysminecraft.command.attack.AttackCommand;
import io.github.brainage04.twitchplaysminecraft.command.craft.CraftCommand;
import io.github.brainage04.twitchplaysminecraft.command.drop.DropCommand;
import io.github.brainage04.twitchplaysminecraft.command.goal.*;
import io.github.brainage04.twitchplaysminecraft.command.key.KeyBindingCommands;
import io.github.brainage04.twitchplaysminecraft.command.key.ReleaseAllKeysCommand;
import io.github.brainage04.twitchplaysminecraft.command.look.LookAtBlockCommand;
import io.github.brainage04.twitchplaysminecraft.command.look.LookAtEntityCommand;
import io.github.brainage04.twitchplaysminecraft.command.look.LookCommand;
import io.github.brainage04.twitchplaysminecraft.command.mine.MineCommand;
import io.github.brainage04.twitchplaysminecraft.command.mine.StripMineCommand;
import io.github.brainage04.twitchplaysminecraft.command.move.MoveCommand;
import io.github.brainage04.twitchplaysminecraft.command.screen.CloseScreenCommand;
import io.github.brainage04.twitchplaysminecraft.command.screen.MoveItemCommand;
import io.github.brainage04.twitchplaysminecraft.command.screen.OpenInventoryCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.BridgeCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.JumpPlaceCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.UseCommand;
import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.hud.core.HUDElementEditor;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClientCommands {
    // todo: organise similar to readme
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

        MoveCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("move")
                                .then(argument("direction", StringArgumentType.string())
                                        .suggests(ClientSuggestionProviders.MOVE_DIRECTION_SUGGESTIONS)
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .then(literal("blocks")
                                                        .executes(context ->
                                                                MoveCommand.executeDistance(
                                                                        context.getSource(),
                                                                        StringArgumentType.getString(context, "direction"),
                                                                        IntegerArgumentType.getInteger(context, "amount")
                                                                )
                                                        )
                                                )
                                                .then(literal("seconds")
                                                        .executes(context ->
                                                                MoveCommand.executeTime(
                                                                        context.getSource(),
                                                                        StringArgumentType.getString(context, "direction"),
                                                                        IntegerArgumentType.getInteger(context, "amount")
                                                                )
                                                        )
                                                )
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

        // world interaction commands
        AttackCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("attack")
                                .executes(context ->
                                        AttackCommand.execute(
                                                context.getSource()
                                        )
                                )
                                .then(argument("entity", ClientIdentifierArgumentType.identifier())
                                        .suggests(ClientSuggestionProviders.LIVING_ENTITY_TYPES)
                                        .executes(context ->
                                                AttackCommand.execute(
                                                        context.getSource(),
                                                        ClientIdentifierArgumentType.getIdentifier(context, "entity")
                                                )
                                        )
                                )
                )
        );

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

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("jumpplace")
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
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("bridge")
                                .then(argument("direction", StringArgumentType.string())
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

        UseCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("use")
                                .then(argument("count", IntegerArgumentType.integer())
                                        .executes(context ->
                                                UseCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "count")
                                                )
                                        )
                                )
                )
        );

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

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("look")
                                .then(argument("direction", StringArgumentType.string())
                                        .suggests(ClientSuggestionProviders.LOOK_DIRECTION_SUGGESTIONS)
                                        .then(argument("degrees", IntegerArgumentType.integer(1))
                                                .executes(context ->
                                                        LookCommand.execute(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "direction"),
                                                                IntegerArgumentType.getInteger(context, "degrees")
                                                        )
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("lookatblock")
                                .then(argument("blockName", StringArgumentType.string())
                                        .executes(context ->
                                                LookAtBlockCommand.execute(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "blockName")
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("lookatentity")
                                .then(argument("entityName", StringArgumentType.string())
                                        .executes(context ->
                                                LookAtEntityCommand.execute(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "entityName")
                                                )
                                        )
                                )
                )
        );

        // screen commands
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
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("moveitem")
                                .then(argument("first", IntegerArgumentType.integer())
                                        .then(argument("second", IntegerArgumentType.integer())
                                                .then(argument("action", StringArgumentType.string())
                                                        .suggests(ClientSuggestionProviders.MOVE_ACTION_SUGGESTIONS)
                                                        .executes(context ->
                                                                MoveItemCommand.execute(
                                                                        context.getSource(),
                                                                        IntegerArgumentType.getInteger(context, "first"),
                                                                        IntegerArgumentType.getInteger(context, "second"),
                                                                        StringArgumentType.getString(context, "action")
                                                                )
                                                        )
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("openinventory")
                                .executes(context ->
                                        OpenInventoryCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("closescreen")
                                .executes(context ->
                                        CloseScreenCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );
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

        // key commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("move")
                                .then(argument("direction", StringArgumentType.string())
                                        .suggests(ClientSuggestionProviders.MOVE_DIRECTION_SUGGESTIONS)
                                        .executes(context ->
                                                KeyBindingCommands.executeHold(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "direction")
                                                )
                                        )
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .then(literal("blocks")
                                                        .executes(context ->
                                                                KeyBindingCommands.executeTimedHold(
                                                                        context.getSource(),
                                                                        StringArgumentType.getString(context, "direction"),
                                                                        IntegerArgumentType.getInteger(context, "amount")
                                                                )
                                                        )
                                                )
                                                .then(literal("seconds")
                                                        .executes(context ->
                                                                KeyBindingCommands.executeTimedHold(
                                                                        context.getSource(),
                                                                        StringArgumentType.getString(context, "direction"),
                                                                        IntegerArgumentType.getInteger(context, "amount")
                                                                )
                                                        )
                                                )
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("presskey")
                                .then(argument("key", StringArgumentType.string())
                                        .executes(context ->
                                                KeyBindingCommands.executeTimedHold(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "key"),
                                                        250
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("holdkey")
                                .then(argument("key", StringArgumentType.string())
                                        .executes(context ->
                                                KeyBindingCommands.executeHold(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "key")
                                                )
                                        )
                                        .then(argument("seconds", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        KeyBindingCommands.executeTimedHold(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "key"),
                                                                IntegerArgumentType.getInteger(context, "seconds") * 1000
                                                        )
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("releasekey")
                                .then(argument("key", StringArgumentType.string())
                                        .executes(context ->
                                                KeyBindingCommands.executeRelease(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "key")
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("releaseallkeys")
                                .executes(context ->
                                        ReleaseAllKeysCommand.execute(
                                                context.getSource(),
                                                true
                                        )
                                )
                )
        );

        // voting commands
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
                        literal("getcurrentgoal")
                                .executes(context ->
                                        GetCurrentGoalCommand.execute(
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
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("clearcurrentgoal")
                                .executes(context ->
                                        ClearCurrentGoalCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );

        // locate commands
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

        // other commands
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
    }
}
