# About
TwitchPlaysMinecraft is a Minecraft mod for Fabric 1.21.4 that lets Twitch streamers give control of the player to their viewers by letting them execute Minecraft commands sent through Twitch chat.


# Setup
This mod contains what is called an [Installed Chatbot](https://dev.twitch.tv/docs/chat/#deciding-what-kind-of-chatbot-to-build), which is a Twitch bot that is hosted on your system while Minecraft is running with this mod loaded.
This bot is required to send Minecraft command feedback through Twitch chat.

This is done because the command feedback displayed via Minecraft chat will either be pushed up past the chat window height (if enough messages are send afterwards to do so) or if that doesn't happen, then it will fade away after a set period of time.

In order to authorise this bot to send messages in your channel's Twitch chat, the mod will prompt you via a chat message with a Twitch URL when you first join a world. When that chat message appears, click the URL.

The URL should lead to a webpage asking you to "Activate Your Device". Click "Activate".

This will then lead to an OAuth2 prompt to authorise "TPM Application" to send/view live Stream Chat and Rooms messages. Click "Authorise".

Once a message appears in your Minecraft chat confirming that you have properly authorised "TPM Application", you have finished setup!

# Commands
Note: In-game, the commands are prefixed with `/`, but viewers typing commands into Twitch chat should prefix their commands with `!` instead.

## Admin Commands
`/commandqueue add <command>` - Adds a command to the command queue.
Note: This is mostly for testing purposes - Twitch viewers can add a command to the command queue 
by simply typing the command but replacing the `/` with a `!`.
However, if you are going to use this command, and the command contains spaces, 
it needs to be enclosed in double quotation marks to avoid "malformed JSON" command errors. 

`/commandqueue clear` - Removes all commands from the command queue.

`/commandqueue process` - Picks the most popular command from the command queue, 
runs the command, clears the command queue and resets the cooldown for processing the command queue.

`/regenerateauthurl` - Regenerates the auth URL used to authorize TPM Bot to send messages in your Twitch chat.
Use this if you get an "INCORRECT CODE!" message when trying to authorize TPM Bot.

## Attack Commands
`/attack` - If there are any mobs within 16 blocks, pathfind towards the nearest one and attack it until it is dead.
If the mob is neutral/hostile, the player will attempt to maintain a distance of ~2-3 blocks while attacking.

`/attack <entityId>` - Functions identically to `/attack` but only looks for entities with the specified `entityId`.

// todo
`/shoot` - If there are any mobs (that can be shot) within 128 blocks, shoot them with a bow until they are dead.

// todo
`/shoot <entityId>` - Functions identically to `/shoot` but only looks for entities with the specified `entityId`.

## Goal Commands
`/availablegoals` - Lists all of the available advancements that have all prerequisites met (e.g. the parent advancement has been achieved)

`/clearcurrentgoal` - Clears the current goal.

`/getcurrentgoal` - Prints information about the current goal, including name, description and ID.

`/getgoal <advancementId>` - Prints information about the advancement with the given `advancementId`, including name, description and ID.

`/setcurrentgoal <advancementId>` - Sets the current goal to the advancement with the given `advancementId`.

## Mine Commands
`/mine <blockName> [<count>]` - Looks for `blockName` blocks within the player's reach mines them until there are no more left or until `count` of them have been mined (once if `count` is not specified).
Fails if there are no `blockName` blocks within the player's reach.

`/stripmine [<count>]` - Mines at 25 pitch while holding forward and shift until `count` blocks have been broken (infinitely if `count` is not specified).
Fails if the player is moving significantly slower than expected (less than 0.1 blocks/second) for more than 10 seconds.

## Move Commands
`/move <direction> <amount> <seconds|blocks>` - Moves in the given `direction` for `amount` number of seconds or blocks (depending on which one is specified.)
Valid direction values: `forward, back, left, right`

// todo
`/moveto <pos>` - Causes the player to pathfind towards the specified position.

## Screen Commands
`/closescreen` - Exits the screen the player is currently using (if they are using one).
Functions identically to pressing Escape.

`/openinventory` - Opens the player's inventory (if it is not open already).
Functions identically to pressing E.

`/moveitem <first> <second> <action>` - Moves the item in the slot with index `first` to the slot with index `second`, depending on the action specified.

## Craft Commands
`/craft item <itemName> [<count>]` - If a crafting table GUI is currently open, attempts to search for the `itemName` in the recipe book. If there is exactly 1 result, that recipe is crafted `count` times.
Fails if there are no results or more than 1 page's worth of results (20+ results).

`/craft recipe <index> [<count>]` - Used when there is more than 1 result after using `/craft item`. If there is only 1 entry for the recipe at index `index`, that recipe is crafted `count` times (once if `count` is not specified).

`/craft entry <index> [<count>]` - Used when there is more than 1 entry for a given recipe where a crafting attempt was made with either `/craft item` or `/craft recipe`. Crafts the recipe entry at index `index` `count` times (once if `count` is not specified).

# Drop Commands
`/drop <slot> [<count>]` - Selects the item at slot `slot` in the hotbar (or screen, if a handled screen/screen with an inventory is open) and drops it `count` times (once if `count` is not specified).

`/drop <slot> all` - Functions identically to `/drop <slot> [<count>]` but drops the entire stack.

`/drophelditem [<count>]` - Drops the currently held hotbar item `count` times (once if `count` is not specified).

`/drophelditem all` - Functions identically to `/drophelditem [<count>]` but drops the entire stack.

## Key Commands
Valid key names: attack, use, forward, left, back, right, jump, sneak, sprint, drop, inventory, pickItem, togglePerspective, swapHands.

`/presskey <keyName>` - Presses the key with the specified `keyName` and releases it after 0.25 seconds.

`/holdkey <keyName> [<seconds>]` - Starts holding the key with the specified `keyName` for `seconds` seconds (or infinitely if `seconds` is not specified).

`/releasekey <keyName>` - Stops holding the key with the specified name.

`/releaseallkeys` - Releases all currently held keys.

## Look Commands
`/lookatblock <blockId>` - Looks at the nearest block with the specified `blockId`.

`/lookatentity <entityId>` - Looks at the nearest entity with the specified `entityId`.

`/look <up/down/left/right> <degrees>` - Rotates the player's camera up/down/left/right by `degrees` degrees.

## Use Commands
`/use <slot> [<count>]` - Selects the item at slot `slot` in the hotbar and uses it `count` times (once if `count` is not specified).

`/use <slot> all` - Functions identically to `/use <slot> [<count>]` but uses the entire stack.

`/usehelditem [<count>]` - Uses the currently held item `count` times (once if `count` is not specified).

`/usehelditem all` - Functions identically to `/usehelditem [<count>]` but uses the entire stack.

`/bridge <direction> <count>` - Bridges for `count` blocks in the given `direction`.
Valid direction values: `north, northeast, east, southeast, south, southwest, west, northwest`

`/jumpplace [<count>]` - Causes the player to jump in place and attempt to place blocks below them
(if they are holding blocks) until they have either been placed `count` times
(or once if `count` is not specified) or the player runs out of blocks.

## Other Commands
`/hotbar <slot>` - Selects Hotbar Slot `slot` where `slot` is the index of the hotbar slot as seen on the in-game overlay.

# Goals
One of the core parts of this mod is setting goals for viewers to collectively achieve, such as (but not limited to):
- Building a house
- Crafting a full set of diamond tools/armor
- Exploring all biomes
- Defeating the Ender Dragon

As you may have noticed, some of these goals already exist as advancements (e.g. Adventuring Time, The End.) in the base game.
For this reason, advancements are used for goals, and goals that do not have advancements in the base game are added by this mod.

# Todo
Implement stopit command (requires command refactor - all commands must share a single `isRunning` variable)
Remove server-side stuff (mod should be client-side only)