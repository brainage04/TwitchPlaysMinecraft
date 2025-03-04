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

# Important Command Info
Valid key names:
attack,
use,
forward,
left,
back,
right,
jump,
sneak,
sprint,
drop,
inventory,
pickItem,
togglePerspective,
swapHands.

# Commands
Note: In-game, the commands are prefixed with `/`, but viewers typing commands into Twitch chat should prefix their commands with `!` instead.

`/attack` - If there are any mobs within 16 blocks, attack the nearest one until it is dead.
If the mob is neutral/hostile, the player will attempt to maintain a distance of 3 blocks while attacking.

`/closescreen` - Exits the screen the player is currently using.
Functions identically to pressing Escape.
Fails if the player is not currently using a screen.

`/craft item <itemName> [<count>]` - If a crafting table GUI is currently open, attempts to search for the `itemName` in the recipe book. If there is exactly 1 result, that recipe is crafted `count` times.
Fails if there are no results or more than 1 page's worth of results (20+ results).

`/craft recipe <index> [<count>]` - Used when there is more than 1 result after using `/craft item`. If there is only 1 entry for the recipe at index `index`, that recipe is crafted `count` times (once if `count` is not specified).

`/craft entry <index> [<count>]` - Used when there is more than 1 entry for a given recipe where a crafting attempt was made with either `/craft item` or `/craft recipe`. Crafts the recipe entry at index `index` `count` times (once if `count` is not specified).

`/drop <slot> [<count>]` - Selects the item at slot `slot` in the hotbar (or screen, if a handled screen/screen with an inventory is open) and drops it `count` times (once if `count` is not specified).

`/drop <slot> all` - Functions identically to `/drop <slot> [<count>]` but drops the entire stack.

`/drophelditem [<count>]` - Drops the currently held hotbar item `count` times (once if `count` is not specified).

`/drophelditem all` - Functions identically to `/drophelditem [<count>]` but drops the entire stack.

`/jumpplace [<count>]` - Causes the player to jump in place and attempt to place the blocks until they have either been placed `count` times (once if `count` is not specified) or the player runs out of blocks.
Fails if the currently held hotbar item is not placeable.

`/presskey <keyName>` - Presses the key with the specified name and releases it after 0.25 seconds.

`/holdkey <keyName> [<seconds>]` - Starts holding the key with the specified name for `seconds` seconds (infinitely if `seconds` is not specified).

`/releasekey <keyName>` - Stops holding the key with the specified name.

`/mine <blockName> [<count>]` - Looks for `blockName` blocks within the player's reach mines them until there are no more left or until `count` of them have been mined (once if `count` is not specified).
Fails if there are no `blockName` blocks within the player's reach.

`/moveto <pos>` - Causes the player to move in a straight line towards the specified position.
Fails if the player is moving significantly slower than expected (less than 0.1 blocks/second) for more than 10 seconds.

`/look <up/down/left/right> <degrees>` - Rotates the player's camera by `degrees` degrees up/down/left/right.

`/openinventory` - Opens the player's inventory. Functions identically to pressing E.
Fails if the player is currently using a screen.

`/releaseallkeys` - Releases all currently held keys.

`/stripmine [<count>]` - Mines at 25 pitch while holding forward and shift until `count` blocks have been broken (infinitely if `count` is not specified).
Fails if the player is moving significantly slower than expected (less than 0.1 blocks/second) for more than 10 seconds.

`/use <slot> [<count>]` - Selects the item at slot `slot` in the hotbar and uses it `count` times (once if `count` is not specified).

`/use <slot> all` - Functions identically to `/use <slot> [<count>]` but uses the entire stack.

`/usehelditem [<count>]` - Uses the currently held item `count` times (once if `count` is not specified).

`/usehelditem all` - Functions identically to `/usehelditem [<count>]` but uses the entire stack.

# Goals
One of the core parts of this mod is setting goals for viewers to collectively achieve, such as (but not limited to):
- Building a house
- Crafting a full set of diamond tools/armor
- Exploring all biomes
- Defeating the Ender Dragon

As you may have noticed, some of these goals already exist as advancements (e.g. Adventuring Time, The End.) in the base game.
For this reason, advancements are used for goals, and goals that do not have advancements in the base game are added by this mod.

# Todo
Allow disabling of individual commands (they can still be executed via Minecraft chat, but not Twitch chat)
Remove server-side stuff (mod should be client-side only)