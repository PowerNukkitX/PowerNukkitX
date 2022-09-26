# Changelog

All notable changes to this project will be documented in this file.

Note: The format of this document is based on the [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) second
revision,
and this project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html) and prefixes the major version with
the upstream major version number so that we can better distinguish it from Nukkit 1.X and 2.X.

## CataLogs

1. <a href="#CataLogs-Swlang">🌐 Switch Languages / 切换语言 </a>
2. <a href="#CataLogs-Join-the-community">💬 Join the Community / 加入我们 </a>
3. <a href="#CataLogs-Version-history">🔖 Version history / 历史版本 </a>

## [1.19.21-r4-PNX](https://github.com/PowerNukkitX/PowerNukkitX/actions) - Dev

This work in progress version supports Minecraft:BE `1.19.21 (Protocol Version 545)`.

## Added

- [#613] Support for Deep Dark biomes (stage #1).

## Changed

- [#620] Remove `StringArrayTag.java`.
- [#621] Example anti-xray configuration added to the default configuration.
- [#623] Correct anti-xray configuration.

## Fixes

- [#612] Fix content with NPC plugin.
- [#617] Fix hopper.

## [1.19.21-r3-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r3) - 2022-9-2

This work in progress version supports Minecraft:BE `1.19.21 (Protocol Version 545)`.

## Added

- [#610] Implement mine cart (InventoryHolder) + funnel.

## Changed

- [#599] Refactor entity registration.
- [#601] js-java interoperability enhancements.
- [#602] Anti-Mineral Perspective improvements.
- [#611] Optimized player movement.

## Fixes

- [#603] Fix funnel furnace item swiping bug.
- [#605] Fix wrong arrow initial speed.
- [#607] fix entity death smoke + potion effect cloud + explosion.
- [#615] fix /effect command.

## [1.19.21-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r2) - 2022-8-24

This work in progress version supports Minecraft:BE `1.19.21 (Protocol Version 545)`.

### Added

- [#572] Add many structure generation.

### Fixes

- [#591]/[#592] Fix recipe for repairing fence,wool->wool carpet,glass->glass plate (fixed in [#596]).

## [1.19.21-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r1) - 2022-8-23

This work in progress version supports Minecraft:BE `1.19.21 (Protocol Version 545)`.

### Added

- [#594] 1.19.21-r1 Release.
- [#587] Add villagers trading api.
- [#586] Add anti-xray.

### Changed

- [#586] Parallelized Chunk Sending.
- [#593] Implemented compatibility with 1.19.21 (protocol version 545).

### Fixes

- [#575] Fix the bug that custom blocks are not saved after they turn into air (fixed in [#585]).
- [#584] Fix some bugs in custom blocks.

## [1.19.20-r5-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r5) - 2022-8-18

This work in progress version supports Minecraft:BE `1.19.20 (Protocol Version 544)`.

### Added

- [#576] 1.19.20-r5 Release.
- [#571] Added preliminary energy system implementation.
- [#574] Add shaded warning.

### Changed

- [#537] Improve custom blocks.
- [#550] Improve recipe.
- [#562] Get resource files via Module instead of ClassLoader.
- [#564] Optimize Terra memory usage.

### Fixes

- [#552] Fix the bug that the client crashes in hell.
- [#554] Fix the bug that chunkSectionCount cannot be written to block nbt.
- [#556] Fix the status bug of mangrove leaves.
- [#557] Fix Teera memory overflow bug.
- [#563] Fixed the bug that bamboo could be pushed by pistons.
- [#565] Fix 3D biome reading and writing
- [#568] Fixed the bug that the anvil would drop infinitely on watery blocks.
- [#569] Fixed abnormal damage when entity y<0.
- [#570] Fixed azalea drop chance.
- [#573] Fix furnace recipe.

## [1.19.20-r4-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r4) - 2022-8-13 -（Pre Release）

This work in progress version supports Minecraft:BE `1.19.20 (Protocol Version 544)`.

### Added

- [#542] 1.19.20-r4 (Pre Release) released.
- [#536] New getMaxHeight and getMinHeight methods in Chunk.

### Changed

- [#542] Update terra version to 6.2.0-Release.

### Fixes

- [#536] Fix the dimension-related method NPE in Chunk.

## [1.19.20-r3-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r3) - 2022-8-13

This work in progress version supports Minecraft:BE `1.19.20 (Protocol Version 544)`.

### Added

- [#524] `Support for 3D biomes` and custom dimension API (TODO).

### Changed

- [#524] Anvil format reading and writing method changed to improve performance.

### Fixes

- [#427] Fix `"this.skyLight" is null` bug (fixed in [#524]).
- [#520] Fixed the issue that PowerNukkitX could not be started in termux (fixed in[#532]).

## [1.19.20-r2-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r2) - 2022-8-12

This work in progress version supports Minecraft:BE `1.19.20 (Protocol Version 544)`.

### Changed

- [#519] New /execute format.
- [#523] Optimize the mutual call between JS plugin and ava.

### Fixes

- [#525] Fix Azalea leaves aren't disappearing (fix in [#528]).
- [#526] Fix Items cannot be recolored (fix in [#527]).

## [1.19.20-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r1) - 2022-8-10 - (Pre Release)

This work in progress version supports Minecraft:BE `1.19.20 (Protocol Version 544)`.

### Changed

- [#515] Implemented compatibility with 1.19.20 (protocol version 544).

### Fixes

- [#511] Fix the problem of not being able to grow large spruce trees.
- [#512] Fix fix compatibility with jar-in-jar multi-level plugins.
- [#514] Fix the issue that when the player is too fast while riding, it will be mistakenly checked as instantaneous.

## [1.19.10-r1-PNX](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.10-r1) - 2022-8-7

This work in progress version supports Minecraft:BE `1.19.10 (Protocol Version 534)`.

### Added

- [#510] 1.19.10-r1 Release。

### Changed

- [#506] 1.19.10-r1 Version Updates。

## [1.6.0.0-PNX](https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2808051758) - EOL - (Dev)

This work in progress version supports Minecraft:BE `1.19.10 (Protocol Version 534)`.

### Added

- [#17] Implemented 384 height-limited worlds within PowerNukkitX (currently main world only).
- [#45] Added items added in version 1.18.10.
- [#106] Initial implementation of maps within PowerNukkitX.
- [#146] Added Bootstrap launcher to PowerNukkitX to use PowerNukkitX without installing Java17.
- [#161] Implemented and built in vanilla commands and command block within PowerNukkitX.
- [#177] Added Terra generator inside PowerNukkitX ([FAQ](https://doc.powernukkitx.cn/en-us/faq/Terra_faq.html)).
- [#236] Initial implementation and completion of JavaScript plugin support in
  PowerNukkitX [(click here for development documentation)](https://doc.powernukkitx.cn/en-us/plugin-dev/js/%E6%A6%82%E8%BF%B0_en-us.html)
  .
- [#288] Implement the /summon command. ` (Tips: will conflict with MobPlugin's /summon command)`
- [#307] Implement the /function command.
- [#326] Implement RAWTEXT (/tellraw /titleraw).
- [#352] Implementation of NPC-API and implementation of NPC functions.
- [#354] Implementing custom blocks / items / entities (refinement in
  progress, [documentation](https://doc.powernukkitx.cn) to be added, custom entities implemented in [#458]).
- [#363] Implemented NPC SKIN switch.
- [#365] Add toSnbt.
- [#370] Add JS cross-plugin interoperability.
- [#384] Implement swift_sneak enchantment effect.
- [#385] Implemented darkness potion effect.
- [#387] Support new achievement interface.
- [#389] Implement sculk block entities.
- [#414] Implement fine snow squares.
- [#416] Add `PlayerFreezeEvent` event.
- [#425] Preliminary completion of `JS Feature` architecture.
- [#431] New BioAI (Basic Architecture).
- [#433] Implement frequently loaded blocks and the corresponding commands.
- [#426] Add reflection escape door for events where bytecode calls fail.
- [#446] Implement DeathInfo.
- [#468] Add `ServerStartedEvent` event.
- [#470] Add OtherSide record.
- [#481] Implement creature AI for pigs, cows and chickens.
- [#483] Implement zombie creature AI.
- [#492] Add SNBT deserialization.
- [#494] Added Chest boat.
- [#500] Implement creeper creature AI.

### Changed

- [#45] Implemented compatibility with 1.18.10 (protocol version 486).
- [#78] Add newly added items to the creation item bar.
- [#132] Change whitelist to allowlist.
- [#243] Implemented to be compatible with 1.18.30 (protocol version 503).
- [#275] Implement basic entity motion handling.
- [#330] Refine the target selector.
- [#333] Preliminary Implementation of Equipment for Mob.
- [#337] Improve BioInventory.
- [#346] Update hunger value calculation.
- [#359] Modify recipes.
- [#366] Update terra version.
- [#367] Improve NPC interface.
- [#368] Improve NPC hint box.
- [#373] Update terra version to 6.0.0-Release.
- [#375] Implement the Scrolling Subtitles API.
- [#380] Implemented to be compatible with 1.19.0 (protocol version 527).
- [#390] Support player name resolution with _.
- [#402] Merge NukkitX modifications.
- [#411] Optimize /version command.
- [#418] Optimize event call performance.
- [#428] NPC Dialog protocol logic synchronization 1.19.0.
- [#443] Improve Mapping.
- [#445] Implemented to be compatible with 1.19.10 (protocol version 534).
- [#455] Update resource files.
- [#461] Update startup command detection + deprecate submodule.
- [#466] `Change custom block api.`
- [#467] Tweak Version command to update checks.
- [#473] Improve status command + update dependency library.
- [#477] Enhance customizability and compatibility of JS engine.
- [#489] Optimize pathfinding logic.
- [#490] Change boss entity position to fit 384 height.
- [#491] Improve entity AI.
- [#499] Minor changes to SNBT format.

### Fixes

- [#4] Fix an exploit where players may spawn on dangerous locations with a small probability (PN Legacy exploit).
- [#22] Fix an issue with main world cubes spontaneously combusting.
- [#33] Fix an exploit where snow and other cubes can be ignited by flint (PN Legacy exploit).
- [#34] Fix an exploit where Mordor is inaccessible.
- [#44] Fix an exploit where white dye can be used as bone powder (PN Legacy exploit).
- [#49] Fix an issue where cubes near lava in Inferno will spontaneously combust.
- [#55] Fix glowing ink sacs not working on notice boards.
- [#93] Fix an exploit where crossbows don't work (PN legacy exploit).
- [#112] Fix Void damage.
- [#114] Fix an exploit where the glowing display box is put in the display box, but the creature egg is displayed.
- [#116] Fix an exploit where the glowing display frame is a normal display frame after copying.
- [#124] Fix the bug that standing on a cactus does no damage.
- [#136] Fix an exploit where dripping stone cone does not fill the pot of alchemy.
- [#141] Fix the exploit that blacksmith table does not work.
- [#147] Fix Piston exploit (PN legacy exploit).
- [#152] Fix the loophole that enchantment book effect can be used directly.
- [#153] Fix the vulnerability that sponge does not absorb water.
- [#155] Fix the vulnerability of detecting rail.
- [#171] Fix the bug that trap boxes don't work properly.
- [#178] Fix magma cube damage calculation bias.
- [#188] Fix incorrect drop damage calculation.
- [#202] Fix EntityArmorChangeEvene not triggering properly.
- [#251] Fix an issue where plowing does not change back to dirt when a square is placed on a plowed field.
- [#265] Fix an exploit where Hellgate cannot teleport.
- [#273] Fix an exploit where synthesizing an empty barrel or empty barrel has a probability of turning into the wrong
  barrel with ID 0 when storing lava.
- [#283] Fix an exploit where logs decompose all to oak boards.
- [#318] Fix an exploit that prevents the item bar from being moved/dropped/used after death.
- [#323] Fix the bug that giant mushrooms don't drop after destruction.
- [#325] Fix id null pointer bug.
- [#327] Fix the clone issue of some cubes.
- [#336] Fix auto-cube entity clear leak issue.
- [#338] Fix hell birth point acquisition issue.
- [#347] Fix language files.
- [#364] Fix NPC exploit.
- [#375] Fix NPC-API exploits and memory leaks.
- [#376] Preliminary attempt to fix birth point issue.
- [#377] Fix camera shake.
- [#382] Fix low-level bug in ListTag#toSnbt().
- [#386] Fix spectator collision.
- [#388] Fix target selector Type parameter issue.
- [#394] Fix update block error caused by BlockEntityCauldron.
- [#401] Fix exploit where eating purple ode fruit underwater teleports (fixed in [#406]).
- [#402] Merge NukkitX changes.
- [#415] Fix some minor bugs in /setblock /fill /spawnpoint.
- [#422] Fix exploit where some items were missing from the create item bar.
- [#425] Fix an activation issue with pistons.
- [#429] Fix PlayerFreezeEvent & mark nullability.
- [#437] Fix issue with watchdog forcing server stop due to long map repair times.
- [#442] Fix missing glowing ink sacs and copper ingots in RuntimeMapping::namespacedIdItem.
- [#448] Fix input/xp crash issue.
- [#462] Fix create Item Entry.
- [#463] Fix /particle command.
- [#464] Fix world spawn.
- [#465] Fix the calculation problem of spawn points.
- [#474] Fix wrong player birth point.
- [#476] Fix non-existent capitalization command prompt.
- [#478] Fix the probable null pointer issue of scoreboard.
- [#479] Fix `player.getCraftingGrid().clearAll()` not working (fixed in [#480]).
- [#487] Fix registerCustomBlock (fix another bug in [#488]).
- [#493] Fix entity damage calculation.
- [#498] Fix Speed II potion duration bug.

### CRITICAL SECURITY FIX

- [#16] Changed Log4J Library from `2.17.0` to `2.17.1`, fix CVE-2021-44832.
- [#255] Preliminary re-login attack issue.
- [#292] Updated [Bedrock-Network](https://github.com/PowerNukkit/Bedrock-Network) dependency to 1.6.28, fixes
  CVE-2020-7238.

### Documentation

- [#235] Added all missing `@PowerNukkitXOnly` annotations
- [#412] Added and fixed missing `@PowerNukkitXOnly`
- [#417] Fix documentation for `PlayerFreezeEvent` event
- [#424] Publish PowerNukkitX to [Maven Central] and add [Javadoc]
- [#454] Add package comment information.

## [Unreleased 1.6.0.0-PN] - Future ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/29?closed=1))

Click the link above to see the future.

This work in progress version supports Minecraft `1.18.0`.

### Breaking changes

- [#PN-1267] Changed Nimbus Jose JWT library from `7.9` to `9.13`
- [#PN-1267] Removed some deprecated APIs, check the JDiff for details.
- [#PN-1267] Changed the method signature to customize the boss bar color
- [#PN-1267] `ItemArmor.TIER_OTHER` is not a constant anymore.

### Depreciation
- [#PN-1266] Some APIs become deprecated, check the JDiff for details.
- [#PN-1266] `ItemTrident.setCreative` and `getCreative` are now deprecated.

### Added
- [#PN-1266] API to get the potion names, level in roman string and tipped arrow potion.
- [#PN-1266] API for the banner pattern snout (Piglin)

### Changed
- [#PN-1258] Changed supported version to Minecraft Bedrock Edition `1.18.0`.

### Fixes
- [#PN-267] Regression of: Fishing hooks without players, loaded from the level save.
- [#PN-1267] Network decoding of the `MoveEntityDeltaPacket`
- [#PN-1267] `isOp` param of the `CapturingCommandSender` constructors were not being used
- [#PN-1267] Boats placed by dispenser could have the wrong wood type
- [#PN-1267] Falling anvil was not dealing damage to the entities correctly
- [#PN-1267] Some randomizers could pick the same number over and over again.
- [#PN-1267] Bowl and Crossbow fuel time
- [#PN-1267] The durability of some items

### Documentation
- [#PN-1267] Added all missing `@PowerNukkitOnly` annotations
- [#PN-1267] Added all missing `@Override` annotations
- [#PN-1267] Removed all incorrect `@PowerNukkitOnly` annotations

## [1.5.2.1-PN] - 2021-12-21 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/30?closed=1))

### CRITICAL SECURITY FIX
- [#PN-1266], [#PN-1270] Changed Log4J library from `2.13.3` to `2.17.0`

## [1.5.2.0-PN] - 2021-12-01 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/28?closed=1))
This new version adds protocol support for Minecraft `1.17.40` as if it was `1.16.221` with some new features and fixes.

We are still working on `1.17` and `1.18` new features, but we plain to release them in December 2021.

`1.18` support will be added on `1.6.0.0-PN` and it will be released as soon as possible.

Thank you for the translations!
Help us to translate PowerNukkit at https://translate.powernukkit.org

Want to talk?
Talk to us at https://discuss.powernukkit.org and/or https://powernukkit.org/discord

### Added
- [#PN-1233] New API classes and methods were added, check the [JDiff](https://devs.powernukkit.org/jdiff/1.5.2.0-PN_x_1.5.1.0-PN/changes.html) for details.
- [#PN-1193] Add more damage causes to the API and improve magma block death message
- [#PN-1233] French translations (thank you for the translations!)

### Changed
- [#PN-1244] Changed the `recipes.json` and `creativeitems.json` format for easier changes, updates, and maintenance (backward compatible)
- [#PN-1233] Updated Deutsche, Indonesian, Korean, Poland, Russian, Spanish, Turkish, Vietnamese, Brazilian Portuguese, and Simplified Chinese translations. (thank you!)

### Fixes
- [#PN-1187] Fixes powered rails do not update in a row
- [#PN-1191] `SimpleChunkManager.setBlockAtLayer` ignoring the layer
- [#PN-1174] Fixes Infinite loop with double chest and comparator
- [#PN-1202] Improves unknown item handling, shows unknown block instead of disconnections
- [#PN-982] Populator error due to corruption on compressed light data
- [#PN-1214] Fixed the names for BlockConcrete and BlockConcretePowder
- [#PN-1172] Fix and improve resource pack related packets

## [1.5.1.0-PN] - 2021-07-05 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/22?closed=1))
Our goal on this version was to fix bugs, and we did it, we fixed a lot of them!

Thank you for the translations!
Help us to translate PowerNukkit at https://translate.powernukkit.org

Want to talk? 
Talk to us at https://discuss.powernukkit.org and/or https://powernukkit.org/discord

### Changed
- [#PN-1107] Guava version from `29.0` to `30.1.1`
- [#PN-1107] SnakeYAML version from `1.26` to `1.28`
- [#PN-1134] Update the Chinese, Russian, and Turkish translations. Thank you for your contributions!
- [#PN-1149] Update the Spanish, and Russian translations. Also improved the message when a plugin is not found. Thank you for your contributions!
- [#PN-1177] Update the Portuguese, Chinese, and Polish translations. Also added the key `language.locale` to allow plugin devs to build a `Locale` object
- [#PN-1150] The `show_death_message` gamerule was renamed to `show_death_messages`. A backward compatibility code will keep the old one working, but it's now deprecated.
- [#PN-1151] Improved `/setworldspaw` auto completion
- [#PN-1153] Deprecate BlockNetherBrick in favor of BlockBricksNether
- [#PN-783] Campfire now drop 2 charcoal always
- [#PN-783] Soul campfire now drops 1 soul sand
- [#PN-783] Soul campfire now deal double the damage that normal campfires deals
- [#PN-783] Campfire and Soul campfire now deal damage even the entity is sneaking
- [#PN-783] Campfire and Soul campfire now breaks when pushed by piston
- [#PN-669] Improved the output of the `/kill @e` command

### Added
- [#PN-1146] Added implementation for `AnimateEntityPacket`
- [#PN-1150] The `freeze_damage` gamerule 
- [#PN-1150] Mappings for Goat, Glow Squid, and Axolotl entities and spawn eggs
- [#PN-783] Campfire and Soul Campfire can now be lit by burning entities stepping on it
- [#PN-783] Campfire and Soul Campfire can now be unlit by throwing a splash water bottle on it
- [#PN-783] Campfire and Soul Campfire can now lit by using an item enchanted with fire aspect
- [#PN-669] New API methods to get the name of the entity for display

### Fixes
- [#PN-1119] `TickSyncPacket` was not registered 
- [#PN-1120] Entities sometimes gets invisible for some players
- [#PN-1122] Backward compatibility with plugins setting full bark logs with 17:13
- [#PN-1132] You don't dismount the vehicle when you teleport, causing you to glitch
- [#PN-1103] The output message of the `/enchant` command
- [#PN-1100] Abrupt Time Change
- [#PN-1130] Soul Campfire and End Crystal were rendering as other items in the inventory
- [#PN-1139] Backward compatibility with some custom world generators
- [#PN-1147] Sharpness damage calculation
- [#PN-1153] Some code quality issues reported by sonar
- [#PN-1170] Cobwebs are now breakable by using shears
- [#PN-702] Burning arrow and rain will make a lot of particles
- [#PN-625] If you instant kill a mob with fire aspect enchant tool, it will not give fire aspect drops
- [#PN-979] Fixes an issue where the players could not hear each other walking
- [#PN-576] Swimming in a 1x1 tunnel of water was causing suffocation damage by the block above the player

## [1.5.0.0-PN] - 2021-06-11 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/26?closed=1))
This was quick! This new version add protocol support for Minecraft `1.17.0` as if it was `1.16.221`.

The new changes will be implemented in `1.5.1.0-PN` and onwards.

This version works with Minecraft `1.16.221`!

### Breaking change!
***This version supports a new major Minecraft version, some plugin sources might need to be updated or recompiled!***

- `BlockWall.WallType.END_STONE_BRICK` was renamed to `END_BRICK` to match the property
- Custom blocks now have to implement `Block.getProperties()` if they need to have custom meta.
- `BlockCauldron.getFillLevel()` and it's setter now range from 0 to 6. Glass bottle remove/add 2 levels instead of one now.
- The creative inventory file format has changed
- The recipes file format has changed
- `BellAttachmentType` was renamed to `AttachmentType`
- `BlockBell.getBellAttachmentType` and `BlockBell.setBellAttachmentType` were renamed to `get/setAttachment`.
- `DoublePlantType` enum had the entries changed to match the property values.
- `BlockMeta`, `BlockSolidMeta`, and `BlockFallableMeta` now have `getProperties` abstract.
- `CommonBlockProperties.LEGACY_PROPERTY_NAME`, `LEGACY_PROPERTIES`, and `LEGACY_BIG_PROPERTIES` were removed.
- `MinecraftItemID.DEBUG_STICK` was removed.
- All deprecated stuff marked to be removed at this version was removed. Except `AnvilDamageEvent.getDamage()`.

### Deprecated
- This is a reminder that numeric block meta are deprecated. Use the specifc block API to make modifications. Come to Discord if you have questions.
- A lot of duplicated BlockIDs are being deprecated, follow the `replaceBy` instructions to use the right ones.

### Changed
- All blocks are now using the new block state system.
- We are no longer using `runtime_block_states.dat` and `runtime+block_states_overrides.dat`, we are now using `canonical_block_states.nbt` from [pmmp/BedrockData](https://github.com/pmmp/BedrockData)
- `BlockProperties.requireRegisteredProperty` now throws `BlockPropertyNotFoundException` instead of `NoSuchElementException` when the prop is not found.
- Some `Entity` magic values have changed
- Game rules now have a flag to determine if it can be changed.

#### Added
- Event to handle player fishing by plugins. `PlayerFishEvent`.
- 3 new packets: `AddVolumeEntityPacket`, `RemoveVolumeEntityPacket`, and `SyncEntityPropertyPacket`

### Fixes
- Issues with crafting recipes involving charcoal and dyes and ink_sac related items

## [1.4.0.0-PN] - 2021-05-31 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/15?closed=1))
It's finally here! A stable version of the Nether update! Supporting almost all blocks and items!

It works with Minecraft `1.16.221`!

### Breaking change!
***This version supports a new major Minecraft version, some plugin sources might need to be updated or recompiled!***

- Many `final` constants are no longer constants, they are now marked with `dynamic` due to constant changes on updates
- The size of the block data bits changed back from `6` to `4` to fix backward compatibility with Nukkit plugins
- New chunk content versioning! Don't keep changing versions back and forth, or you will end up with having some odd block states!

### Deprecated
- All usage of the numeric block damage system is now deprecated, new code should use the new block state system
- Direct usage of static mutable arrays in the Block class are now deprecated, use the getters and API methods instead
- Avoid using `Item.get` to get ItemBlocks! Use `Item.getBlock` or use `MinecraftItemID.<the-id>.get` instead!

### Fixes
- [#PN-857] Items in wrong tabs of the creative inventory and at the side of crafting grid screens
- [#PN-959] Give command not working correctly when using a namespace, like in `/give minecraft:dirt`
- [#PN-902] NetherPortal block can't be destroyed by liquid flow
- [#PN-902] Lava doesn't turn concrete powder into concrete
- [#PN-770] Bamboo not dropping when broken, were also affecting blocks with id > 255
- [#PN-765] Unsafe level.dat writes could lead to world corruption
- [#PN-766] Error saving region files with content over 2 GB
- [#PN-777] Falling block falling though scaffolds
- [#PN-778] Unable to get `minecraft:mob_spawner` with `/give Nick mob_spawner`
- Snowballs not damaging blazes
- Issues with the geometry of player and human entities
- Hay bale not reducing fall damage
- Lapis ore drops with enchanted pickaxes
- Break time calculations
- A lot of block placement rules
- A lot of item drop rules
- Mixing potions, water, lava, and dyes in cauldrons
- Many boat issues
- Many dispenser issues
- Some duplication issues
- Enchantment level of the enchantments
- Many other issues not listed here

### Added
- Block state system and API with backward compatibility to the legacy numeric block damage system
- [#PN-917] Adds automatic bug reports using Sentry, can be opted out in `server.properties`
- API to get how long the player has been awake
- New APIs to detect the type of bucket, dye, spawn egg, coal, and a few others
- A `MinecraftItemID` API for simpler version independent vanilla item creation
- Shield mechanics
- Trident mechanics
- Many new API classes and methods not listed here
- Emerald ore generation

#### Blocks
- Allow
- Deny
- Structure Void
- Nether Reactor Core
- Structure Block
- Lodestone
- Crimson Roots
- Warped Woots
- Warped Wart Block
- Crimson Fungus
- Warped Fungus
- Shroomlight
- Weeping Vines
- Crimson Nylium
- Warped Nylium
- Basalt
- Polished Basalt
- Soul Soil
- Soul Fire
- Nether Sprouts Block
- Target
- Stripped Crimson/Warped Stem
- Crimson/Warped Planks
- Crimson/Warped Door
- Crimson/Warped Trapdoor
- Crimson/Warped Sign
- Crimson/Warped Stairs
- Crimson/Warped Fences
- Crimson/Warped Fence Gate
- Crimson/Warped Button
- Crimson/Warped Pressure Plate
- Crimson/Warped Slab
- Soul Torch
- Soul Lantern
- Netherite Block
- Ancient Derbirs
- Respawn Anchor
- Blackstone
- Polished Blackstone Bricks
- Polished Blackstone Bricks Stairs
- Blackstone Stairs
- Blackstone Wall
- Polished Blackstone Bricks Wall
- Chiseled Polished Blackstone
- Cracked Polished Blackstone Bricks
- Gilded Blackstone
- Blackstone Slab
- Polished Blackstone Brick Slab
- Chain Block
- Twisting Vines
- Nether Gold Ore
- Crying Obsidian
- Soul Campfire
- Polished Blackstone
- Polished Blackstone Stairs
- Polished Blackstone Slab
- Polished Blackstone Pressure Plate
- Polished Blackstone Button
- Polished Blackstone Wall
- Warped/Crimson Hyphae
- Stripped Warped/Crimson Hyphae
- Chiseled Nether Bricks
- Cracked Nether Bricks
- Quartz Bricks

#### Items
- Rabbit Hide
- Lead
- Popped Chorus Fruit
- Dragon Breath
- Iron Nugget
- Crossbow (shooting is not implemented)
- Lodestone
- Netherite Ingot
- Netherite Sword
- Netherite Shovel
- Netherite Pickaxe
- Netherite Axe
- Netherite Hoe
- Netherite Helmet
- Netherite Chestplate
- Netherite Leggings
- Netherite Boots
- Netherite Scrap
- Warped Fungus On A Stick
- Record Pigstep
- Nether Sprouts

#### Entities
- Armor Stand
- Iron Golem
- Snow Golem
- Piglin Brute
- Fox
- NPC (Edu)

#### Enchantments
- Multishot
- Piercing
- Quick Charge
- Soul Speed

#### Effects
- Bad Omen
- Village Hero

#### Potions
- Slowness II Extended
- Slowness IV

### Changed
- Translations updated. Help us to translate PowerNukkit at https://translate.powernukkit.org
- The block system was revamped
- Optimized the RAM memory usage
- Many hard-coded block, item, and entity instantiation were replaced to dynamic calls, allowing plugins to use custom classes
- [#PN-765] The `ServerBrand` tag in the `level.dat` file will be set to `PowerNukkit` now
- [#PN-776] Grindstone won't reset the repair cost anymore
- Packet batching is now handled near the RakNet layer
- Removed extra data from chunk encoding
- The sound enum has been updated
- Bucket with fish can no longer interact with cauldrons
- The /give command now support all current vanilla namespaced ids
- Updated the raknet dependency from 1.6.15-PN2 to 1.6.25-PN
- Improved the `/debugpaste` command, it saves the paste locally now, to upload the paste use `/debugpaste upload` or `/debugpaste upload last`
- Many commands were improved
- Improved javadocs
- Improved the bed behaviour

## [1.3.1.5-PN] - 2020-09-01
Fixes a critical duplication exploit.

### Fixes
- [#PN-544] Duplication exploit by packet manipulation

### Changed
- Translations updated

## [1.3.1.4-PN] - 2020-08-14  ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/20?closed=1))
Fixes beehives, client crashes in Nether and improves some error handling

### Fixes
- [#PN-467] Players crash when reconnecting in the Nether
- [#PN-469] Players who don't crash when reconnecting in the Nether, see overworld sky
- [#PN-462] Beehives and bee nest getting rendered as an "UPDATE!" block
- [#PN-475] If middle packet inside a batch packet fails processing, the other packets in the batch gets ignored

### Changed
- [#PN-475] Improved error log whilst loading a config file
- [#PN-475] Improved error log when a batch packet decoding or processing fails
- [#PN-462] The beehive and bee_nest block data have been changed from `[3-bits BlockFace index, 3-bits honey level]` to `[2-bits BlockFace horizontal index, 3-bits honey level]`
- [#PN-462] The chunk's content version got increased to 5
- [#PN-464] The German and the Simplified Chinese translations have been updated

## [1.3.1.3-PN] - 2020-08-11 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/19?closed=1))
A quick update that adds support to 1.16.20 and updates the translations

### Fixes
- [#PN-298] Having the gamemode changed by another player shows a `%s` in the chat

### Changed
- Changed the protocol version to support Minecraft Bedrock Edition 1.16.20
- The translations have been updated

## [1.3.1.2-PN] - 2020-08-10 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/18?closed=1))
Very important fixes that you must have. Make a backup before upgrading.

### Fixes
- [#PN-404] Issues with inventories, click events, and cursor
- [#PN-365] Client crashing when FakeInventories
- [#PN-339] Client crashes when closing some inventories
- [#PN-287] Campfire does not extinguish when it touches flowing water
- [#PN-287] Grindstone placement rule on vines, bubble, liquid, and replaceable blocks
- [#PN-287] `BlockGrowEvent` being fired when using bone meal on dead sea pickle
- [#PN-287] Mesa biome generating a wrong block instead of red sandstone
- [#PN-366] Block disappears when making bridges
- [#PN-261] Bamboo and bamboo sampling bone meal, placement, and breaking behaviours
- [#PN-359] Piston causing tile entities to invalidate
- [#PN-340] Brewing stand placement rules, recipes, and processing
- [#PN-397] Firework effects getting overridden by a black creeper face
- [#PN-400] OP players in spectator gamemode could break blocks in client-side
- [#PN-403] Right-clicking some blocks while sneaking were not opening the block
- [#PN-407] Server could be stuck and not shutdown even after Watchdog detects the an issue
- [#PN-412] Daylight detector's tile entity wasn't being removed
- [#PN-440] Predefined long world seeds wasn't loading correctly
- [#PN-414] Minecart names could return null can cause unexpected NullPointerExceptions
- [#PN-436] Chorus plant and flowers could be placed anywhere and could keep floating
- [#PN-436] Chorus plant and flowers had wrong blast resistance values
- [#PN-437] Nether dimension having overworld sky
- [#PN-427] Campfire was moving with pistons
- [#PN-422] Can't ignite leaves with flint and steel directly
- [#PN-450] Can't ignite leaves with fireball directly
- [#PN-450] Flowerpot placement and support rules
- [#PN-430] Redstone repearter not causing redstone update to the block right in front of it
- [#PN-445] Some languages had different default nukkit.yml settings values
- [#PN-443] Boats and minecarts were not checking if they were already 'killed' and could drop itself more than once
- [#PN-404] Minecarts trying to make death animations when it don't have
- [#PN-404] Comparator not causing redstone updates correcty
- [#PN-404] Fixed three duplication glitches
- [#PN-430] Fire not fading sometimes
- [#PN-430] Iron door not dropping when you break the block under it
- [#PN-449] Honey block couldn't be used to make a note on noteblock

### Added
- [#PN-287] You can now set yaw and pitch when using the teleport command: `/tp <x> <y> <z> <yaw> <pitch>`
- [#PN-445] New translation site. Help us to translate PowerNukkit at https://translate.powernukkit.org

### Changes
- [#PN-390] If a compression issue happens, an IOException will be thrown now
- [#PN-287] Removed the teleport limitation in y-axis with the `/tp` command
- [#PN-287] Campfire does not allow flowing allow passing through it anymore
- [#PN-287] Improved the lantern placement rules code
- [#PN-287] Improved the liquid flow logic
- [#PN-287] Prevents placing blocks in water if the block would break itself in the next tick
- [#PN-287] Narrow down the logic to prevent the right-click spam bug
- [#PN-404] Grindstone will not be forced to face up when replacing vines anymore
- [#PN-445] The translation system have been improved
- [#PN-433] Improved snowball particle performance
- [#PN-404] Chunk content version bumped to 4

## [1.3.1.1-PN] - 2020-07-19
Fixes an important stability issue and improves resource pack compatibility

### Fixes
- [#PN-390] Server stop responding due to a compression issue
- [#PN-368] Improves resource pack compatibility

## [1.3.1.0-PN] - 2020-07-09 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/13?closed=1))
Security, stability and enchanting table fixes alongside with few additions.

PowerNukkit now has its own [discord guild], click the link below to join and have fun!  
💬 https://powernukkit.org/discord 💬  
[![Discord](https://img.shields.io/discord/728280425255927879)](https://powernukkit.org/discord)

### Fixes
- [#PN-326] Enchantment table not working
- [#PN-297] Using the hoe or shovel doesn't emit any sound
- [#PN-328] ClassCastException and some logic errors while processing the chunk backward compatibility method
- [#PN-344] Sticky pistons not pulling other sticky piston
- [#PN-344] The technical block names weren't being saved in memory when `GlobalBlockPalette` was loaded
- [#PN-338] The Dried Kelp Block was not burnable as fuel
- [#PN-232] The enchanting table level cost is now managed by the server

### Added
- [#PN-330] The [discord guild] link to the readme
- [#PN-352] The library jsr305 library at version `3.0.2` to add `@Nullable`, `@Nonnull` and related annotations
- [#PN-326] A couple of new classes, methods and fields to interact with the enchanting table transactions
- [#PN-326] The entities without AI: Hoglin, Piglin, Zoglin, Strider
- [#PN-352] Adds default runtime id to the new blocks with meta `0`

### Changed
- [#PN-348] Updated the guava library from `21.0` to `24.1.1`
- [#PN-347] Updated the JWT library from `4.39.2` to `7.9`
- [#PN-346] Updated the Log4J library from `2.11.1` to `2.13.3`
- [#PN-326] Changed the Nukkit API version from `1.0.10` to `1.0.11`
- [#PN-335] The chunk content version from `1` to `2`, all cobblestone walls will be reprocessed on the chunk first load after the update
- [#PN-352] The `runtime_block_states_overrides.dat` file has been updated

## [1.3.0.1-PN] - 2020-07-01 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/14?closed=1))
Improves plugin compatibility and downgrade the RakNet lib to solve a memory leak

### Fixes
- [#PN-320] Multiple output crafting, cake for example
- [#PN-323] Compatibility issue with the regular version of GAC

### Added
- [#PN-315] Hoglin, Piglin, Zoglin and Strider entities without AI

### Changed
- [#PN-319] The RakNet library were downgraded to 1.6.15 due to a potential memory leak issue

## [1.3.0.0-PN] - 2020-07-01 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/11?closed=1))
Added support for Bedrock Edition 1.16.0 and 1.16.1

### Breaking change!
***This version supports a new major Minecraft version, some plugin sources might need to be updated or recompiled!*** 

The following breaking change will be pulled in `1.3.0.0-PN`
- [8a09f93](https://github.com/PowerNukkit/PowerNukkit/commit/8a09f933f83c9a52531ff8a184a58c6d733c9174) Quick craft implementation. ([NukkitX#1473](https://github.com/NukkitX/Nukkit/pull/1473)) Jedrzej* 05/06/2020

### Binary incompatibility!
- [#PN-293] A few `Entity` data constant values were changed, plugins which uses them might need to be recompiled, no code change required

### Save format changed!
The save format has been changed to accommodate very high block data values. **Make a world backup before updating!**

### Incomplete changelog warning!
Due to the high amount of changes, and the urgency of this update, this changelog file will be released with outdated information,
please check the current changelog file in the [updated changelog] online for further details about this update.

### Disabled features warning!
* Enchanting table GUI has been temporarily disabled due to an incompatible change to the Bedrock protocol,
it's planned to be fixed on 1.3.1.0-PN
* End portal formation has been disabled due to reported crashes, it's planned to be reviewed on 1.3.1.0-PN

### Experimental warning!
This is the first release of a huge set of changes to accommodate the new Bedrock Edition 1.16.0/1.16.1 release,
please take extra cautions with this version, make constant backups and report any issues you find. 

### Deprecation warnings!
- [#PN-293] Many `Entity` constants are deprecated and might be removed on `1.4.0.0-PN`
- [#PN-293] `Entity.DATA_FLAG_TRANSITION_SITTING` and `DATA_FLAG_TRANSITION_SETTING` only one of them is correct, the incorrect will be removed
- [#PN-293] `Network.inflate_raw` and `deflate_raw` does not follow the correct naming convention and will be removed. Use `inflateRaw` and `deflateRaw` instead. 
- [#PN-293] `HurtArmorPacket.health` was renamed to `damage` and will be removed on `1.4.0.0-PN`. A backward compatibility code has been added.
- [#PN-293] `SetSpawnPositionPacket.spawnForce` is now unused and will be removed on `1.4.0.0-PN`
- [#PN-293] `TextPacket.TYPE_JSON` was renamed to `TYPE_OBJECT` and will be removed on `1.4.0.0-PN`
- [#PN-293] `riderInitiated` argument was added to the `EntityLink` constructor. The old constructor will be removed on `1.4.0.0-PN`

### Fixes
- [#PN-293] Spectator colliding with vehicles
- [#PN-293] Ice melting into water in the Nether
- [#PN-293] `Player.removeWindow` was able to remove permanent windows

### Added
- [#PN-293] End portals can now be formed using Eye of Ender
- [#PN-293] Setting to make the server ignore specific packets
- [#PN-293] New compression/decompression methods
- [#PN-293] Trace logging to outbound packets when trace is enabled
- [#PN-293] The server now logs a warning when a packet violation warning is correctly received
- [#PN-293] 12 new packets, please see the pull request file changes for details
- [#PN-293] Many new entity data constants, please see the `Entity.java` file in the PR for details

### Changed
- [#PN-293] Thorns can now be applied to any armor while enchanting
- [#PN-293] The server now requires the clients to playing on Bedrock Edition 1.16.0
- [#PN-293] Updated RakNet to `1.6.18`
- [#PN-293] RakNet protocol version changed from `9` to `10`
- [#PN-293] 10 packets, please see the pull request file changes for details
- [#PN-293] The server have more control over the player UI now
- [#PN-293] New entity data constants
- [#PN-293] `FakeBlockUIComponent` now fires `InventoryCloseEvent` when the inventory is closed
- [#PN-293] The `runtime_block_states.dat`, `recipes.json`, `entity_identifiers.dat` and `biome_definitions.dat` files have been updated
- [#PN-293] Grindstone now clears only the enchantments and sets the repair cost to `0`, it used to clear all NBT tags

## [1.2.1.0-PN] - 2020-06-07 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/9?closed=1))
Adds new methods to be used by plugins and fixes many issues. 

### Fixes
- [#PN-224] Enchantment compatibility rules when merging enchanted items in an anvil
- [#PN-113] Beehives not dropping in creative when it has bees
- [#PN-270] Replacing sugarcane's water don't break the sugarcane immediately
- [#PN-272] `EntityPortalEnterEvent` not being fired when entering end portals
- [#PN-279] `BlockEndPortal` missing collision bounding box
- [#PN-279] `Entity.checkBlockCollision()`'s over scaffolding logic outdated
- [#PN-281] Levers and buttons don't replace the snow layers
- [#PN-285] Chicken, cow, pig, rabbit and sheep not dropping cooked food when on fire
- [#PN-285] Chorus plant and flower not dropping
- [#PN-285] Item string placing tripwire hooks instead of tripwires
- [#PN-285] Wrong block name and color for dark prismarine block and prismarine bricks
- [#PN-285] Nether bricks fence were burnable and flammable
- [#PN-285] Item on hands disappear (looses one from the stack) when interacting with chest minecarts and hopper minecarts

### Added
- [#PN-227] PlayerJumpEvent called when jump packets are received.
- [#PN-242] `Item.equalsIgnoringEnchantmentOrder` method for public usage.
- [#PN-244] `Enchantment.getPowerNukkit().isItemAcceptable(Item)` to check if an enchantment can exist 
         in a given item stack by any non-hack means.
- [#PN-256] `CapturingCommandSender` intended to capture output of commands which don't require players.
- [#PN-259] `Hash.hashBlock(Vector3)` method for public usage.
- [#PN-261] `Player.isCheckingMovement()` method for public usage.
- [#PN-261] Protected field `EntityEndCrystal.detonated` to disable the `EndCrystal.explode()` method.
- [#PN-275] New annotations to document when elements get added and when deprecated elements will be removed
- [#PN-123] Adds and register the banner pattern items
- [#PN-276] `Block.afterRemoval()` called automatically when the block is replaced using any `Level.setBlock()`
- [#PN-277] `Block.mustSilkTouch()` and `Block.mustDrop()` to allow blocks to force the dropping behaviour when being broken
- [#PN-279] `Entity.isInEndPortal()` for public usage
- [#PN-285] `LoginChainData.getRawData()` for public usage

### Changed
- [#PN-227] Sugar canes now fires BlockGrowEvent when growing naturally.
- [#PN-261] Kicked players can now view the kick reason on kick.
- [#PN-285] Limit the maximum size of `BookEditPacket`'s text to 256, ignoring the packet if it exceeds the limit
- [#PN-285] Ender pearls will now be unable to teleport players across different dimensions
- [#PN-285] `ShortTag.load(NBTInputStream)` now reads a signed short. Used to read an unsigned short.

## [1.2.0.2-PN] - 2020-05-18 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/10?closed=1))
Contains several fixes, including issues which cause item losses and performance issues

### Fixes
- [#PN-239] Anvil fails to merge some enchantments because the ordering mismatches
- [#PN-240] Anvils were charging fewer levels to merge thorn books
- [#PN-243] Anvils were charging more levels to merge punch books
- [#PN-246] Anvil checking the enchantment table property instead of the enchantment id
- [#PN-246] Compatibility rules for unbreaking, fortune, mending, riptide, loyalty and channeling enchantments
- [#PN-248] Air blocks with metadata were being rendered as "UPDATE!" block (backward compatibility fix)
- [#PN-212] The `/tp player 0 1 2` command doesn't work
- [#PN-220] Stripping old full bark log results in a wrong block
- [#PN-157] Wrong Packed and Blue Ice break time with the hands
- [#PN-193] Wrong explosion behaviour with waterlogged block
- [#PN-103] Fixes BlockLeaves's random update logic spamming packets and consuming CPU unnecessarily
- [#PN-253] Fixes `LeavesDecayEvent` also being called when leaves wouldn't decay
- [#PN-254] Fixes BlockLeaves not checking for log connectivity, was checking only if it had a log block nearby
- [#PN-255] Fix /status information in /debugpaste not being collected
- [#PN-260] Fix a stack overflow when setting off end crystals near to each other
- [#PN-260] Fix drops of block entity inventory contents on explosion
- [#PN-260] Check SUPPORTED_PROTOCOLS instead of CURRENT_PROTOCOL in `LoginPacket.decode()`
- [#PN-79] Sugarcane can grow without water
- [#PN-262] Removing the water don't break the sugarcane (using empty bucket or breaking water flow)
- [#PN-263] Fixes disconnect messages not reaching the player sometimes
- [#PN-116] Fishing hooks don't attach to entities and damages multiples entities
- [#PN-95] The Level Up sound is not centered
- [#PN-267] Fishing hooks without players, loaded from the level save. They are now removed on load
- [#PN-266] Loosing connection with items in an open anvil makes you loose the items
- [#PN-273] Loosing connection with items in an open grindstone, enchanting table, stone cutter  makes you loose the items
- [#PN-273] Loosing connection with items in an open crafting table, 2x2 crafting grid makes you loose the items

### Changed
- [#PN-247] Invalid BlockId:Meta combinations now log an error when found. It logs only once
- [#PN-255] The report issues link has been changed to point to the PowerNukkit repository
- [#PN-268] The `/xp` command now makes level up sound every 5 levels
- [#PN-273] If an anvil, grindstone, enchanting, stonecutter, crafting GUI closes, the items will try to go to the player's inventory
- [#PN-273] `FakeBlockUIComponent.close(Player)` now calls `onClose(Player)`
- [#PN-274] `Player.checkInteractNearby()` is now called once every 10 ticks, it was called every tick

## [1.2.0.1-PN] - 2020-05-08 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/8?closed=1))
Fixes several anvil issues.

### Added
- [#PN-224] Added option to disable watchdog with `-DdisableWatchdog=true`. 
  This should be used **only by developers** to debug the server without interruptions by the crash detection system.

### Fixes
- [#PN-224] Anvil not merging enchanted items correctly and destroying the items.
- [#PN-228] Invalid enchantment order on anvil's results causing the crafting transaction to fail.
- [#PN-226] Anvil cost calculation not applying bedrock edition reductions
- [#PN-222] Anvil changes the level twice and fails the transaction if the player doesn't have enough.
- [#PN-235] Wrong flags in MoveEntityAbsolutePacket
- [#PN-234] Failed anvil transactions caused all involved items to be destroyed
- [#PN-234] Visual desync in the player's experience level when an anvil transaction fails or is cancelled. 

### Changed
- [#PN-234] Anvil's result is no longer stored in the PlayerUIInventory at slot 50 as 
         it was vulnerable to heavy duplication exploits.
- [#PN-234] `setResult` methods in `AnvilInventory` are now deprecated and marked for removal at 1.3.0.0-PN
         because it's not supported by the client and changing it will fail the transaction.

## [1.2.0.0-PN] - 2020-05-03 ([Check the milestone](https://github.com/PowerNukkit/PowerNukkit/milestone/6?closed=1))
**Note:** Effort has been made to keep this list accurate but some bufixes and new features might be missing here, specially those made by the NukkitX team and contributors.

### Added
- This change log file
- [#PN-108] EntityMoveByPistonEvent
- [#PN-140] `isUndead()` method to the entities

### Fixes
- [#PN-129] A typo in the BlockBambooSapling class name **(breaking change)**
- [#PN-102] Leaves decay calculation
- [#PN-87] Arrows in offhand are black in the first person view
- [#PN-46] checked if ProjectileHitEvent is cancelled before the action execution
- [#PN-108] Lever sounds
- [#PN-108] Incorrect sponge particles
- [#PN-12] Wrong redstone signal from levers
- [#PN-129] You can now shift to climb down while you are in the edges of a scaffold
- [#PN-129] Fixes a turtle_egg placement validation
- [#PN-129] Campfire can no longer be placed over an other campfire directly
- [#PN-129] The sound that campfire does when it extinguishes
- [#PN-140] Instant damage and instant health are now inverted when applied to undead entities
- [#PN-140] A collision detection issue on Area Effect Cloud which could make it wears off way quicker than it should
- [#PN-152] Changes the blue_ice blast resistance from 2.5 to 14
- [#PN-170] Trapdoors behaving incorrectly when they receive redstone signal
- [#PN-219] Button and door sounds
- [#PN-44] Different daytime from Android and Windows 10 Edition
- [#PN-93] Nukkit sends a rain time that doesn't matches the server
- [#PN-210] Issues with old blocks from old NukkitX worlds, specially fully barked logs (log:15 for example)

### Changed
- Make BlockLectern implements Faceable
- The versioning convention now follows this pattern:<br/>`upstream.major.minor.patch-PN`<br/>[Click here for details.](https://github.com/PowerNukkit/PowerNukkit/blob/7912aa4be68e94a52762361c2d5189b7bbc58d2a/pom.xml#L8-L14)

## [1.1.1.0-PN] - 2020-01-21

### Fixes
- Piston heads not rendering
- Cauldron implementation, should be closer to vanilla now
- Implements hashCode in the NBT Tags, fixes usage with Set and HashMap
- Fixes BaseInventory ignoring it's own max stack size
- Fix cauldron's lightFilter value
- Fix the project throwing sound effect
- No particles when snow hits something
- Fixes projectile sounds
- Fixes egg particles and exp sounds
- The anvil block implementation
- Plants now requires light to grow
- Fix player does not get update for own skin
- Fix ~ operator in teleport command
- Fix ~ operator in /particle command
- Fall damage with slow falling effect
- Fishing Hook drag and gravity values
- [a8247360] Crops, grass, leaves, tallgrass growth and population
- Fixes fuzzy spawn radius calculation
- [#PN-49] noDamageTicks should make the entity completely invulnerable while active
- [#PN-54] Fixes movement issues on heavy server load
- [#PN-57] Fixes block placement of Bone Block, End Portal Frame, Jukebox and Observer

### Changed
- Unregistered block states will be shown as 248:0 (minecraft:info_update) now
- Improves the UI inventories
- The codename to PowerNukkit to distinct from [NukkitX]'s implementation
- [#PN-50] The kick message is now more descriptive
- [#PN-80] Merged the "New RakNet Implementation" pull request which greatly improves the server performance and connections

### Added

- Waterlogging support
- Support with blocks ID higher then 255 to the Anvil save format
- Support for blocks with 6 bits data value (used to support only 4 bits)
- [#PN-51] Support for the offhand slot
- [#PN-52] Merge the "More redstone components" pull request which fixes and implements many redstone related blocks
- [#PN-53] Merge the "Vehicle event fix" pull request which add new events and fixes damage issues related to vehicles
- [#PN-55] Minecart (chest and hopper) inventories
- [#PN-56] ServerStopEvent
- Shield block animation (without damage calculation)
- New gamerules
- The /setblock command
- Dyeing leather support to cauldrons
- Color mixing support to cauldron
- Implementation for the entities (without AI):
    - Bees
    - Lingering Potions
    - Area Effect Clouds
- Implementation for the items:
    - Honey
    - Honey Bottle
    - Honeycomb
    - Suspicious Stew
    - Totem of Undying (without functionality)
    - Name Tags
    - Shulker Shell
- Implementation for the blocks:
    - [#PN-58] Daylight Sensor
    - Lectern
    - Smoker
    - Blast Furnace
    - Light Block
    - Honeycomb Block
    - Wither Roses
    - Honey Block
    - Acacia, Birch, Dark Oak, Jungle, Spruce signs
    - Composter
    - Andesite, Polished Andesite, Diorite, Polished Diorite, End Brick, Granited, Polished Granite, Mossy Cobblestone stairs
    - Mossy Stone Brick, Prismarine Brick, Red Nether Brick stairs, Smooth Quartz, Red Sandstone, Smooth Sandstone stairs
    - Beehive and Bee Nests
    - Sticky Piston Head
    - Lava Cauldron
    - Wood (barks)
    - Jigsaw
    - Stripped Acacia, Birch, Dark Oak, Jungle, Oak and Spruce logs and barks
    - Blue Ice
    - Seagrass
    - Coral
    - Coral Fans
    - Coral Blocks
    - Dried Kelp Block
    - Kelp
    - Carved Pumpkin
    - Smooth Stone
    - Acacia, Birch, Dark Oak, Jungle, Spruce Button
    - Acacia, Birch, Dark Oak, Jungle, Spruce Pressure Plate
    - Acacia, Birch, Dark Oak, Jungle, Spruce Trapdoor
    - Bubble Column
    - Scaffolding
    - Sweet Berry Bush
    - Conduit
    - All stone type slabs
    - Lantern
    - Barrel
    - Campfire
    - Cartography Table
    - Fletching Table
    - Smithing Table
    - Bell
    - Turtle Eggs
    - Grindstone
    - Stonecutter
    - Loom
    - Bamboo

## <a id="CataLogs-Join-the-community"></a>💬 Join the Community / 加入我们

* [Discord]
* [QQ]

## <a id="CataLogs-Version-history"></a>🔖 Version history / 历史版本

<details>
  <summary>1.6.0.0-PNX</summary>

1. [#V1-dev] PNX-1.6.0.0-dev (Protocol Version 486)
2. [#V2-dev] PNX-1.6.0.0-dev (Protocol Version 503)
3. [#V3-dev] PNX-1.6.0.0-dev (Protocol Version 527)
4. [#V4-dev] PNX-1.6.0.0-dev (Protocol Version 534)

</details>

<details>
  <summary>1.19.10-PNX</summary>

1. [#1.19.10-r1] PNX-1.19.10-r1 (Protocol Verison 534)

</details>

<details>
  <summary>1.19.20-PNX</summary>

1. [#1.19.20-r1] PNX-1.19.20-r1 (Protocol Verison 544)
2. [#1.19.20-r2] PNX-1.19.20-r2 (Protocol Verison 544)
3. [#1.19.20-r3] PNX-1.19.20-r3 (Protocol Verison 544)
4. [#1.19.20-r4] PNX-1.19.20-r4 (Protocol Verison 544)
5. [#1.19.20-r5] PNX-1.19.20-r5 (Protocol Verison 544)

</details>

<details>
  <summary>1.19.21-PNX</summary>

1. [#1.19.21-r1] PNX-1.19.21-r1 (Protocol Verison 545)
2. [#1.19.21-r2] PNX-1.19.21-r2 (Protocol Verison 545)
3. [#1.19.21-r3] PNX-1.19.21-r3 (Protocol Verison 545)

</details>

## <a id="CataLogs-Swlang"></a>🌐 多语言文档

---
Need to switch languages?

[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/CHANGELOG.md)
[![繁體中文](https://img.shields.io/badge/繁體中文-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/zh-hant/CHANGELOG.md)
[![English](https://img.shields.io/badge/English-100%25-green?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/en-us/CHANGELOG.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/LICENSE)
[![ChangeLog](https://img.shields.io/badge/README-blue?style=flat-square)](https://github.com/PowerNukkitX/PowerNukkitX/blob/master/blob/en-us/README.md)
[![PNX-DOC](https://img.shields.io/badge/PNX-DOC-blue?style=flat-square)](https://doc.powernukkitx.cn)

[updated changelog]:https://github.com/PowerNukkit/PowerNukkit/blob/bleeding/CHANGELOG.md
[discord guild]: https://powernukkit.org/discord

[Unreleased 1.6.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.5.2.1-PN...bleeding
[1.5.2.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.5.2.0-PN...v1.5.2.1-PN
[1.5.2.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.5.1.0-PN...v1.5.2.0-PN
[1.5.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.5.0.0-PN...v1.5.1.0-PN
[1.5.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.4.0.0-PN...v1.5.0.0-PN
[1.4.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.5-PN...v1.4.0.0-PN
[1.3.1.5-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.4-PN...v1.3.1.5-PN
[1.3.1.4-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.3-PN...v1.3.1.4-PN
[1.3.1.3-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.2-PN...v1.3.1.3-PN
[1.3.1.2-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.1-PN...v1.3.1.2-PN
[1.3.1.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.1.0-PN...v1.3.1.1-PN
[1.3.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.0.1-PN...v1.3.1.0-PN
[1.3.0.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.3.0.0-PN...v1.3.0.1-PN
[1.3.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.1.0-PN...v1.3.0.1-PN
[1.3.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.1.0-PN...v1.3.0.0-PN
[1.2.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.0.2-PN...v1.2.1.0-PN
[1.2.0.2-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.0.1-PN...v1.2.0.2-PN
[1.2.0.1-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.2.0.0-PN...v1.2.0.1-PN
[1.2.0.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/v1.1.1.0-PN...v1.2.0.0-PN
[1.1.1.0-PN]: https://github.com/PowerNukkit/PowerNukkit/compare/1ac6d50d36f07b6f1a02df299d9591d78c379db9...v1.1.1.0-PN#files_bucket

[a8247360]: https://github.com/PowerNukkit/PowerNukkit/commit/a8247360

[NukkitX]: https://github.com/CloudburstMC/Nukkit

[#PN-12]: https://github.com/PowerNukkit/PowerNukkit/issues/12
[#PN-44]: https://github.com/PowerNukkit/PowerNukkit/issues/44
[#PN-46]: https://github.com/PowerNukkit/PowerNukkit/issues/46
[#PN-49]: https://github.com/PowerNukkit/PowerNukkit/pull/49
[#PN-50]: https://github.com/PowerNukkit/PowerNukkit/pull/50
[#PN-51]: https://github.com/PowerNukkit/PowerNukkit/pull/51
[#PN-52]: https://github.com/PowerNukkit/PowerNukkit/pull/52
[#PN-53]: https://github.com/PowerNukkit/PowerNukkit/pull/53
[#PN-54]: https://github.com/PowerNukkit/PowerNukkit/pull/54
[#PN-55]: https://github.com/PowerNukkit/PowerNukkit/pull/55
[#PN-56]: https://github.com/PowerNukkit/PowerNukkit/pull/56
[#PN-57]: https://github.com/PowerNukkit/PowerNukkit/pull/57
[#PN-58]: https://github.com/PowerNukkit/PowerNukkit/pull/58
[#PN-79]: https://github.com/PowerNukkit/PowerNukkit/issues/79
[#PN-80]: https://github.com/PowerNukkit/PowerNukkit/pull/80
[#PN-87]: https://github.com/PowerNukkit/PowerNukkit/issues/87
[#PN-93]: https://github.com/PowerNukkit/PowerNukkit/issues/93
[#PN-95]: https://github.com/PowerNukkit/PowerNukkit/issues/95
[#PN-102]: https://github.com/PowerNukkit/PowerNukkit/pull/102
[#PN-103]: https://github.com/PowerNukkit/PowerNukkit/issues/103
[#PN-108]: https://github.com/PowerNukkit/PowerNukkit/pull/108
[#PN-113]: https://github.com/PowerNukkit/PowerNukkit/issues/113
[#PN-116]: https://github.com/PowerNukkit/PowerNukkit/issues/116
[#PN-123]: https://github.com/PowerNukkit/PowerNukkit/issues/123
[#PN-129]: https://github.com/PowerNukkit/PowerNukkit/pull/129
[#PN-140]: https://github.com/PowerNukkit/PowerNukkit/pull/140
[#PN-152]: https://github.com/PowerNukkit/PowerNukkit/pull/152
[#PN-157]: https://github.com/PowerNukkit/PowerNukkit/issues/157
[#PN-170]: https://github.com/PowerNukkit/PowerNukkit/pull/170
[#PN-193]: https://github.com/PowerNukkit/PowerNukkit/issues/193
[#PN-210]: https://github.com/PowerNukkit/PowerNukkit/issues/210
[#PN-212]: https://github.com/PowerNukkit/PowerNukkit/issues/212
[#PN-220]: https://github.com/PowerNukkit/PowerNukkit/issues/220
[#PN-219]: https://github.com/PowerNukkit/PowerNukkit/pull/219
[#PN-222]: https://github.com/PowerNukkit/PowerNukkit/issues/223
[#PN-224]: https://github.com/PowerNukkit/PowerNukkit/pull/224
[#PN-226]: https://github.com/PowerNukkit/PowerNukkit/issues/226
[#PN-227]: https://github.com/PowerNukkit/PowerNukkit/pull/227
[#PN-228]: https://github.com/PowerNukkit/PowerNukkit/issues/228
[#PN-232]: https://github.com/PowerNukkit/PowerNukkit/issues/232
[#PN-234]: https://github.com/PowerNukkit/PowerNukkit/issues/234
[#PN-235]: https://github.com/PowerNukkit/PowerNukkit/issues/235
[#PN-239]: https://github.com/PowerNukkit/PowerNukkit/issues/239
[#PN-240]: https://github.com/PowerNukkit/PowerNukkit/issues/240
[#PN-242]: https://github.com/PowerNukkit/PowerNukkit/pull/242
[#PN-243]: https://github.com/PowerNukkit/PowerNukkit/issues/243
[#PN-244]: https://github.com/PowerNukkit/PowerNukkit/pull/244
[#PN-246]: https://github.com/PowerNukkit/PowerNukkit/issues/246
[#PN-247]: https://github.com/PowerNukkit/PowerNukkit/pull/247
[#PN-248]: https://github.com/PowerNukkit/PowerNukkit/pull/248
[#PN-253]: https://github.com/PowerNukkit/PowerNukkit/pull/253
[#PN-254]: https://github.com/PowerNukkit/PowerNukkit/issues/254
[#PN-255]: https://github.com/PowerNukkit/PowerNukkit/pull/255
[#PN-256]: https://github.com/PowerNukkit/PowerNukkit/pull/256
[#PN-259]: https://github.com/PowerNukkit/PowerNukkit/pull/259
[#PN-260]: https://github.com/PowerNukkit/PowerNukkit/pull/260
[#PN-261]: https://github.com/PowerNukkit/PowerNukkit/pull/261
[#PN-262]: https://github.com/PowerNukkit/PowerNukkit/pull/262
[#PN-263]: https://github.com/PowerNukkit/PowerNukkit/pull/263
[#PN-266]: https://github.com/PowerNukkit/PowerNukkit/issues/266
[#PN-267]: https://github.com/PowerNukkit/PowerNukkit/issues/267
[#PN-268]: https://github.com/PowerNukkit/PowerNukkit/pull/268
[#PN-270]: https://github.com/PowerNukkit/PowerNukkit/issues/270
[#PN-272]: https://github.com/PowerNukkit/PowerNukkit/issues/272
[#PN-273]: https://github.com/PowerNukkit/PowerNukkit/pull/273
[#PN-274]: https://github.com/PowerNukkit/PowerNukkit/pull/274
[#PN-275]: https://github.com/PowerNukkit/PowerNukkit/pull/275
[#PN-276]: https://github.com/PowerNukkit/PowerNukkit/pull/276
[#PN-277]: https://github.com/PowerNukkit/PowerNukkit/pull/277
[#PN-279]: https://github.com/PowerNukkit/PowerNukkit/pull/279
[#PN-281]: https://github.com/PowerNukkit/PowerNukkit/pull/281
[#PN-285]: https://github.com/PowerNukkit/PowerNukkit/pull/285
[#PN-287]: https://github.com/PowerNukkit/PowerNukkit/issues/287
[#PN-293]: https://github.com/PowerNukkit/PowerNukkit/pull/293
[#PN-297]: https://github.com/PowerNukkit/PowerNukkit/pull/297
[#PN-298]: https://github.com/PowerNukkit/PowerNukkit/issues/298
[#PN-315]: https://github.com/PowerNukkit/PowerNukkit/pull/315
[#PN-319]: https://github.com/PowerNukkit/PowerNukkit/pull/319
[#PN-320]: https://github.com/PowerNukkit/PowerNukkit/pull/320
[#PN-323]: https://github.com/PowerNukkit/PowerNukkit/issues/323
[#PN-326]: https://github.com/PowerNukkit/PowerNukkit/pull/326
[#PN-328]: https://github.com/PowerNukkit/PowerNukkit/issues/326
[#PN-330]: https://github.com/PowerNukkit/PowerNukkit/issues/330
[#PN-335]: https://github.com/PowerNukkit/PowerNukkit/issues/335
[#PN-338]: https://github.com/PowerNukkit/PowerNukkit/issues/338
[#PN-339]: https://github.com/PowerNukkit/PowerNukkit/issues/339
[#PN-340]: https://github.com/PowerNukkit/PowerNukkit/issues/340
[#PN-344]: https://github.com/PowerNukkit/PowerNukkit/issues/344
[#PN-346]: https://github.com/PowerNukkit/PowerNukkit/issues/346
[#PN-347]: https://github.com/PowerNukkit/PowerNukkit/issues/347
[#PN-348]: https://github.com/PowerNukkit/PowerNukkit/issues/348
[#PN-352]: https://github.com/PowerNukkit/PowerNukkit/issues/352
[#PN-359]: https://github.com/PowerNukkit/PowerNukkit/issues/359
[#PN-365]: https://github.com/PowerNukkit/PowerNukkit/issues/365
[#PN-366]: https://github.com/PowerNukkit/PowerNukkit/issues/366
[#PN-368]: https://github.com/PowerNukkit/PowerNukkit/issues/368
[#PN-390]: https://github.com/PowerNukkit/PowerNukkit/issues/390
[#PN-397]: https://github.com/PowerNukkit/PowerNukkit/issues/397
[#PN-400]: https://github.com/PowerNukkit/PowerNukkit/issues/400
[#PN-403]: https://github.com/PowerNukkit/PowerNukkit/issues/403
[#PN-404]: https://github.com/PowerNukkit/PowerNukkit/issues/404
[#PN-407]: https://github.com/PowerNukkit/PowerNukkit/issues/407
[#PN-412]: https://github.com/PowerNukkit/PowerNukkit/issues/412
[#PN-414]: https://github.com/PowerNukkit/PowerNukkit/issues/414
[#PN-422]: https://github.com/PowerNukkit/PowerNukkit/issues/422
[#PN-427]: https://github.com/PowerNukkit/PowerNukkit/issues/427
[#PN-430]: https://github.com/PowerNukkit/PowerNukkit/issues/430
[#PN-433]: https://github.com/PowerNukkit/PowerNukkit/issues/433
[#PN-436]: https://github.com/PowerNukkit/PowerNukkit/issues/436
[#PN-437]: https://github.com/PowerNukkit/PowerNukkit/issues/437
[#PN-440]: https://github.com/PowerNukkit/PowerNukkit/issues/440
[#PN-443]: https://github.com/PowerNukkit/PowerNukkit/issues/443
[#PN-445]: https://github.com/PowerNukkit/PowerNukkit/issues/445
[#PN-449]: https://github.com/PowerNukkit/PowerNukkit/issues/449
[#PN-450]: https://github.com/PowerNukkit/PowerNukkit/issues/450
[#PN-462]: https://github.com/PowerNukkit/PowerNukkit/issues/462
[#PN-464]: https://github.com/PowerNukkit/PowerNukkit/issues/464
[#PN-467]: https://github.com/PowerNukkit/PowerNukkit/issues/467
[#PN-469]: https://github.com/PowerNukkit/PowerNukkit/issues/469
[#PN-475]: https://github.com/PowerNukkit/PowerNukkit/issues/475
[#PN-544]: https://github.com/PowerNukkit/PowerNukkit/issues/544
[#PN-576]: https://github.com/PowerNukkit/PowerNukkit/issues/576
[#PN-625]: https://github.com/PowerNukkit/PowerNukkit/issues/625
[#PN-669]: https://github.com/PowerNukkit/PowerNukkit/issues/669
[#PN-702]: https://github.com/PowerNukkit/PowerNukkit/issues/702
[#PN-765]: https://github.com/PowerNukkit/PowerNukkit/issues/765
[#PN-766]: https://github.com/PowerNukkit/PowerNukkit/issues/766
[#PN-770]: https://github.com/PowerNukkit/PowerNukkit/issues/770
[#PN-776]: https://github.com/PowerNukkit/PowerNukkit/issues/776
[#PN-777]: https://github.com/PowerNukkit/PowerNukkit/issues/777
[#PN-778]: https://github.com/PowerNukkit/PowerNukkit/issues/778
[#PN-783]: https://github.com/PowerNukkit/PowerNukkit/issues/783
[#PN-857]: https://github.com/PowerNukkit/PowerNukkit/issues/857
[#PN-882]: https://github.com/PowerNukkit/PowerNukkit/issues/882
[#PN-902]: https://github.com/PowerNukkit/PowerNukkit/issues/902
[#PN-917]: https://github.com/PowerNukkit/PowerNukkit/issues/917
[#PN-959]: https://github.com/PowerNukkit/PowerNukkit/issues/959
[#PN-960]: https://github.com/PowerNukkit/PowerNukkit/issues/960
[#PN-979]: https://github.com/PowerNukkit/PowerNukkit/issues/979
[#PN-982]: https://github.com/PowerNukkit/PowerNukkit/issues/982
[#PN-990]: https://github.com/PowerNukkit/PowerNukkit/issues/990
[#PN-1100]: https://github.com/PowerNukkit/PowerNukkit/issues/1100
[#PN-1103]: https://github.com/PowerNukkit/PowerNukkit/issues/1103
[#PN-1107]: https://github.com/PowerNukkit/PowerNukkit/issues/1107
[#PN-1119]: https://github.com/PowerNukkit/PowerNukkit/issues/1119
[#PN-1120]: https://github.com/PowerNukkit/PowerNukkit/issues/1120
[#PN-1122]: https://github.com/PowerNukkit/PowerNukkit/issues/1122
[#PN-1130]: https://github.com/PowerNukkit/PowerNukkit/issues/1130
[#PN-1132]: https://github.com/PowerNukkit/PowerNukkit/issues/1132
[#PN-1134]: https://github.com/PowerNukkit/PowerNukkit/issues/1134
[#PN-1139]: https://github.com/PowerNukkit/PowerNukkit/issues/1139
[#PN-1146]: https://github.com/PowerNukkit/PowerNukkit/issues/1146
[#PN-1147]: https://github.com/PowerNukkit/PowerNukkit/issues/1147
[#PN-1149]: https://github.com/PowerNukkit/PowerNukkit/issues/1149
[#PN-1150]: https://github.com/PowerNukkit/PowerNukkit/issues/1150
[#PN-1151]: https://github.com/PowerNukkit/PowerNukkit/issues/1151
[#PN-1120]: https://github.com/PowerNukkit/PowerNukkit/issues/1120
[#PN-1153]: https://github.com/PowerNukkit/PowerNukkit/issues/1153
[#PN-1170]: https://github.com/PowerNukkit/PowerNukkit/issues/1170
[#PN-1172]: https://github.com/PowerNukkit/PowerNukkit/issues/1172
[#PN-1174]: https://github.com/PowerNukkit/PowerNukkit/issues/1174
[#PN-1177]: https://github.com/PowerNukkit/PowerNukkit/issues/1177
[#PN-1187]: https://github.com/PowerNukkit/PowerNukkit/issues/1187
[#PN-1191]: https://github.com/PowerNukkit/PowerNukkit/issues/1191
[#PN-1193]: https://github.com/PowerNukkit/PowerNukkit/issues/1193
[#PN-1202]: https://github.com/PowerNukkit/PowerNukkit/issues/1202
[#PN-1214]: https://github.com/PowerNukkit/PowerNukkit/issues/1214
[#PN-1233]: https://github.com/PowerNukkit/PowerNukkit/issues/1233
[#PN-1244]: https://github.com/PowerNukkit/PowerNukkit/issues/1244
[#PN-1216]: https://github.com/PowerNukkit/PowerNukkit/issues/1216
[#PN-1258]: https://github.com/PowerNukkit/PowerNukkit/issues/1258
[#PN-1266]: https://github.com/PowerNukkit/PowerNukkit/issues/1266
[#PN-1267]: https://github.com/PowerNukkit/PowerNukkit/issues/1267
[#PN-1270]: https://github.com/PowerNukkit/PowerNukkit/issues/1270


[#4]: https://github.com/PowerNukkitX/PowerNukkitX/pull/4
[#16]: https://github.com/PowerNukkitX/PowerNukkitX/pull/16
[#17]: https://github.com/PowerNukkitX/PowerNukkitX/issues/17
[#22]: https://github.com/PowerNukkitX/PowerNukkitX/issues/22
[#33]: https://github.com/PowerNukkitX/PowerNukkitX/pull/33
[#34]: https://github.com/PowerNukkitX/PowerNukkitX/pull/34
[#44]: https://github.com/PowerNukkitX/PowerNukkitX/pull/44
[#45]: https://github.com/PowerNukkitX/PowerNukkitX/pull/45
[#49]: https://github.com/PowerNukkitX/PowerNukkitX/issues/49
[#55]: https://github.com/PowerNukkitX/PowerNukkitX/issues/55
[#78]: https://github.com/PowerNukkitX/PowerNukkitX/pull/78
[#93]: https://github.com/PowerNukkitX/PowerNukkitX/pull/93
[#106]: https://github.com/PowerNukkitX/PowerNukkitX/issues/106
[#112]: https://github.com/PowerNukkitX/PowerNukkitX/pull/112
[#114]: https://github.com/PowerNukkitX/PowerNukkitX/issues/114
[#116]: https://github.com/PowerNukkitX/PowerNukkitX/issues/116
[#124]: https://github.com/PowerNukkitX/PowerNukkitX/issues/124
[#132]: https://github.com/PowerNukkitX/PowerNukkitX/pull/132
[#136]: https://github.com/PowerNukkitX/PowerNukkitX/issues/136
[#141]: https://github.com/PowerNukkitX/PowerNukkitX/pull/141
[#146]: https://github.com/PowerNukkitX/PowerNukkitX/pull/146
[#147]: https://github.com/PowerNukkitX/PowerNukkitX/pull/147
[#152]: https://github.com/PowerNukkitX/PowerNukkitX/issues/152
[#153]: https://github.com/PowerNukkitX/PowerNukkitX/issues/153
[#155]: https://github.com/PowerNukkitX/PowerNukkitX/pull/155
[#161]: https://github.com/PowerNukkitX/PowerNukkitX/pull/161
[#171]: https://github.com/PowerNukkitX/PowerNukkitX/issues/171
[#177]: https://github.com/PowerNukkitX/PowerNukkitX/pull/177
[#178]: https://github.com/PowerNukkitX/PowerNukkitX/pull/178
[#188]: https://github.com/PowerNukkitX/PowerNukkitX/issues/188
[#202]: https://github.com/PowerNukkitX/PowerNukkitX/issues/202
[#235]: https://github.com/PowerNukkitX/PowerNukkitX/pull/235
[#236]: https://github.com/PowerNukkitX/PowerNukkitX/pull/236
[#243]: https://github.com/PowerNukkitX/PowerNukkitX/pull/243
[#251]: https://github.com/PowerNukkitX/PowerNukkitX/pull/251
[#255]: https://github.com/PowerNukkitX/PowerNukkitX/pull/255
[#265]: https://github.com/PowerNukkitX/PowerNukkitX/pull/265
[#273]: https://github.com/PowerNukkitX/PowerNukkitX/pull/273
[#275]: https://github.com/PowerNukkitX/PowerNukkitX/pull/275
[#283]: https://github.com/PowerNukkitX/PowerNukkitX/pull/283
[#288]: https://github.com/PowerNukkitX/PowerNukkitX/pull/288
[#292]: https://github.com/PowerNukkitX/PowerNukkitX/pull/292
[#307]: https://github.com/PowerNukkitX/PowerNukkitX/pull/307
[#318]: https://github.com/PowerNukkitX/PowerNukkitX/issues/318
[#323]: https://github.com/PowerNukkitX/PowerNukkitX/issues/323
[#325]: https://github.com/PowerNukkitX/PowerNukkitX/pull/325
[#326]: https://github.com/PowerNukkitX/PowerNukkitX/pull/326
[#327]: https://github.com/PowerNukkitX/PowerNukkitX/pull/327
[#330]: https://github.com/PowerNukkitX/PowerNukkitX/pull/330
[#333]: https://github.com/PowerNukkitX/PowerNukkitX/pull/333
[#336]: https://github.com/PowerNukkitX/PowerNukkitX/pull/336
[#337]: https://github.com/PowerNukkitX/PowerNukkitX/pull/337
[#338]: https://github.com/PowerNukkitX/PowerNukkitX/pull/338
[#346]: https://github.com/PowerNukkitX/PowerNukkitX/pull/346
[#347]: https://github.com/PowerNukkitX/PowerNukkitX/pull/347
[#352]: https://github.com/PowerNukkitX/PowerNukkitX/pull/352
[#354]: https://github.com/PowerNukkitX/PowerNukkitX/pull/354
[#359]: https://github.com/PowerNukkitX/PowerNukkitX/pull/359
[#363]: https://github.com/PowerNukkitX/PowerNukkitX/pull/363
[#364]: https://github.com/PowerNukkitX/PowerNukkitX/pull/364
[#365]: https://github.com/PowerNukkitX/PowerNukkitX/pull/365
[#366]: https://github.com/PowerNukkitX/PowerNukkitX/pull/366
[#367]: https://github.com/PowerNukkitX/PowerNukkitX/pull/367
[#368]: https://github.com/PowerNukkitX/PowerNukkitX/pull/368
[#370]: https://github.com/PowerNukkitX/PowerNukkitX/pull/370
[#373]: https://github.com/PowerNukkitX/PowerNukkitX/pull/373
[#374]: https://github.com/PowerNukkitX/PowerNukkitX/pull/374
[#375]: https://github.com/PowerNukkitX/PowerNukkitX/pull/375
[#376]: https://github.com/PowerNukkitX/PowerNukkitX/pull/376
[#377]: https://github.com/PowerNukkitX/PowerNukkitX/pull/377
[#380]: https://github.com/PowerNukkitX/PowerNukkitX/pull/380
[#382]: https://github.com/PowerNukkitX/PowerNukkitX/pull/382
[#384]: https://github.com/PowerNukkitX/PowerNukkitX/pull/384
[#385]: https://github.com/PowerNukkitX/PowerNukkitX/pull/385
[#386]: https://github.com/PowerNukkitX/PowerNukkitX/pull/386
[#387]: https://github.com/PowerNukkitX/PowerNukkitX/pull/387
[#388]: https://github.com/PowerNukkitX/PowerNukkitX/pull/388
[#389]: https://github.com/PowerNukkitX/PowerNukkitX/pull/389
[#390]: https://github.com/PowerNukkitX/PowerNukkitX/pull/390
[#394]: https://github.com/PowerNukkitX/PowerNukkitX/pull/394
[#401]: https://github.com/PowerNukkitX/PowerNukkitX/issues/401
[#402]: https://github.com/PowerNukkitX/PowerNukkitX/pull/402
[#405]: https://github.com/PowerNukkitX/PowerNukkitX/pull/405
[#406]: https://github.com/PowerNukkitX/PowerNukkitX/pull/406
[#411]: https://github.com/PowerNukkitX/PowerNukkitX/pull/411
[#412]: https://github.com/PowerNukkitX/PowerNukkitX/pull/412
[#414]: https://github.com/PowerNukkitX/PowerNukkitX/pull/414
[#415]: https://github.com/PowerNukkitX/PowerNukkitX/pull/415
[#416]: https://github.com/PowerNukkitX/PowerNukkitX/pull/416
[#417]: https://github.com/PowerNukkitX/PowerNukkitX/pull/417
[#418]: https://github.com/PowerNukkitX/PowerNukkitX/pull/418
[#422]: https://github.com/PowerNukkitX/PowerNukkitX/pull/422
[#424]: https://github.com/PowerNukkitX/PowerNukkitX/pull/424
[#425]: https://github.com/PowerNukkitX/PowerNukkitX/pull/425
[#426]: https://github.com/PowerNukkitX/PowerNukkitX/pull/426
[#427]: https://github.com/PowerNukkitX/PowerNukkitX/issues/427
[#428]: https://github.com/PowerNukkitX/PowerNukkitX/pull/428
[#429]:https://github.com/PowerNukkitX/PowerNukkitX/pull/429
[#431]: https://github.com/PowerNukkitX/PowerNukkitX/pull/431
[#433]: https://github.com/PowerNukkitX/PowerNukkitX/pull/433
[#436]: https://github.com/PowerNukkitX/PowerNukkitX/pull/436
[#437]: https://github.com/PowerNukkitX/PowerNukkitX/pull/437
[#442]: https://github.com/PowerNukkitX/PowerNukkitX/pull/442
[#443]: https://github.com/PowerNukkitX/PowerNukkitX/pull/443
[#445]: https://github.com/PowerNukkitX/PowerNukkitX/pull/445
[#446]: https://github.com/PowerNukkitX/PowerNukkitX/pull/446
[#448]: https://github.com/PowerNukkitX/PowerNukkitX/pull/448
[#454]: https://github.com/PowerNukkitX/PowerNukkitX/pull/454
[#455]: https://github.com/PowerNukkitX/PowerNukkitX/pull/455
[#458]: https://github.com/PowerNukkitX/PowerNukkitX/pull/458
[#461]: https://github.com/PowerNukkitX/PowerNukkitX/pull/461
[#462]: https://github.com/PowerNukkitX/PowerNukkitX/pull/462
[#463]: https://github.com/PowerNukkitX/PowerNukkitX/pull/463
[#464]: https://github.com/PowerNukkitX/PowerNukkitX/pull/464
[#465]: https://github.com/PowerNukkitX/PowerNukkitX/pull/465
[#466]: https://github.com/PowerNukkitX/PowerNukkitX/pull/466
[#467]: https://github.com/PowerNukkitX/PowerNukkitX/pull/467
[#468]: https://github.com/PowerNukkitX/PowerNukkitX/pull/468
[#470]: https://github.com/PowerNukkitX/PowerNukkitX/pull/470
[#473]: https://github.com/PowerNukkitX/PowerNukkitX/pull/473
[#474]: https://github.com/PowerNukkitX/PowerNukkitX/pull/474
[#476]: https://github.com/PowerNukkitX/PowerNukkitX/pull/476
[#477]: https://github.com/PowerNukkitX/PowerNukkitX/pull/477
[#478]: https://github.com/PowerNukkitX/PowerNukkitX/pull/478
[#479]: https://github.com/PowerNukkitX/PowerNukkitX/issues/479
[#480]: https://github.com/PowerNukkitX/PowerNukkitX/pull/480
[#481]: https://github.com/PowerNukkitX/PowerNukkitX/pull/481
[#483]: https://github.com/PowerNukkitX/PowerNukkitX/pull/483
[#487]: https://github.com/PowerNukkitX/PowerNukkitX/pull/487
[#488]: https://github.com/PowerNukkitX/PowerNukkitX/pull/488
[#489]: https://github.com/PowerNukkitX/PowerNukkitX/pull/489
[#490]: https://github.com/PowerNukkitX/PowerNukkitX/pull/490
[#491]: https://github.com/PowerNukkitX/PowerNukkitX/pull/491
[#492]: https://github.com/PowerNukkitX/PowerNukkitX/pull/492
[#493]: https://github.com/PowerNukkitX/PowerNukkitX/pull/493
[#494]: https://github.com/PowerNukkitX/PowerNukkitX/pull/494
[#498]: https://github.com/PowerNukkitX/PowerNukkitX/pull/498
[#499]: https://github.com/PowerNukkitX/PowerNukkitX/pull/499
[#500]: https://github.com/PowerNukkitX/PowerNukkitX/pull/500
[#506]: https://github.com/PowerNukkitX/PowerNukkitX/pull/506
[#510]: https://github.com/PowerNukkitX/PowerNukkitX/pull/510
[#511]: https://github.com/PowerNukkitX/PowerNukkitX/pull/511
[#512]: https://github.com/PowerNukkitX/PowerNukkitX/pull/512
[#514]: https://github.com/PowerNukkitX/PowerNukkitX/pull/514
[#515]: https://github.com/PowerNukkitX/PowerNukkitX/pull/515
[#519]: https://github.com/PowerNukkitX/PowerNukkitX/pull/519
[#520]: https://github.com/PowerNukkitX/PowerNukkitX/issues/520
[#523]: https://github.com/PowerNukkitX/PowerNukkitX/pull/523
[#524]: https://github.com/PowerNukkitX/PowerNukkitX/pull/524
[#525]: https://github.com/PowerNukkitX/PowerNukkitX/issues/525
[#526]: https://github.com/PowerNukkitX/PowerNukkitX/issues/526
[#527]: https://github.com/PowerNukkitX/PowerNukkitX/pull/527
[#528]:https://github.com/PowerNukkitX/PowerNukkitX/pull/528
[#532]: https://github.com/PowerNukkitX/PowerNukkitX/pull/532
[#536]: https://github.com/PowerNukkitX/PowerNukkitX/pull/536
[#537]: https://github.com/PowerNukkitX/PowerNukkitX/pull/537
[#542]: https://github.com/PowerNukkitX/PowerNukkitX/pull/542
[#550]: https://github.com/PowerNukkitX/PowerNukkitX/pull/550
[#552]: https://github.com/PowerNukkitX/PowerNukkitX/pull/552
[#554]: https://github.com/PowerNukkitX/PowerNukkitX/pull/554
[#556]: https://github.com/PowerNukkitX/PowerNukkitX/pull/556
[#557]: https://github.com/PowerNukkitX/PowerNukkitX/pull/557
[#562]: https://github.com/PowerNukkitX/PowerNukkitX/pull/562
[#563]: https://github.com/PowerNukkitX/PowerNukkitX/pull/563
[#564]: https://github.com/PowerNukkitX/PowerNukkitX/pull/564
[#565]: https://github.com/PowerNukkitX/PowerNukkitX/pull/565
[#568]: https://github.com/PowerNukkitX/PowerNukkitX/pull/568
[#569]: https://github.com/PowerNukkitX/PowerNukkitX/pull/569
[#570]: https://github.com/PowerNukkitX/PowerNukkitX/pull/570
[#571]: https://github.com/PowerNukkitX/PowerNukkitX/pull/571
[#572]: https://github.com/PowerNukkitX/PowerNukkitX/pull/572
[#573]: https://github.com/PowerNukkitX/PowerNukkitX/pull/573
[#574]: https://github.com/PowerNukkitX/PowerNukkitX/pull/574
[#575]: https://github.com/PowerNukkitX/PowerNukkitX/issues/575
[#576]: https://github.com/PowerNukkitX/PowerNukkitX/pull/576
[#584]: https://github.com/PowerNukkitX/PowerNukkitX/pull/584
[#585]: https://github.com/PowerNukkitX/PowerNukkitX/pull/585
[#586]: https://github.com/PowerNukkitX/PowerNukkitX/pull/586
[#587]: https://github.com/PowerNukkitX/PowerNukkitX/pull/587
[#591]: https://github.com/PowerNukkitX/PowerNukkitX/issues/591
[#592]: https://github.com/PowerNukkitX/PowerNukkitX/issues/592
[#593]: https://github.com/PowerNukkitX/PowerNukkitX/pull/593
[#594]: https://github.com/PowerNukkitX/PowerNukkitX/pull/594
[#596]: https://github.com/PowerNukkitX/PowerNukkitX/pull/596
[#599]: https://github.com/PowerNukkitX/PowerNukkitX/pull/599
[#601]: https://github.com/PowerNukkitX/PowerNukkitX/pull/601
[#602]: https://github.com/PowerNukkitX/PowerNukkitX/pull/602
[#603]: https://github.com/PowerNukkitX/PowerNukkitX/pull/603
[#605]: https://github.com/PowerNukkitX/PowerNukkitX/pull/605
[#607]: https://github.com/PowerNukkitX/PowerNukkitX/pull/607
[#610]: https://github.com/PowerNukkitX/PowerNukkitX/pull/610
[#611]: https://github.com/PowerNukkitX/PowerNukkitX/pull/611
[#612]: https://github.com/PowerNukkitX/PowerNukkitX/pull/612
[#613]: https://github.com/PowerNukkitX/PowerNukkitX/pull/613
[#615]: https://github.com/PowerNukkitX/PowerNukkitX/pull/615
[#617]: https://github.com/PowerNukkitX/PowerNukkitX/pull/617
[#620]: https://github.com/PowerNukkitX/PowerNukkitX/pull/620
[#621]: https://github.com/PowerNukkitX/PowerNukkitX/pull/621
[#623]: https://github.com/PowerNukkitX/PowerNukkitX/pull/623
<!--PowerNukkitX Version history-->

<!--1.6.0.0-PNX Version summary Start-->
<!--Protocol Version 486-->
[#V1-dev]: https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2179919470 

<!--Protocol Version 503-->
[#V2-dev]: https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2479714447

<!--Protocol Version 527-->
[#V3-dev]:https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2662176331

<!--Protocol Version 534-->
[#V4-dev]:https://github.com/PowerNukkitX/PowerNukkitX/actions/runs/2808051758
<!--1.6.0.0-PNX Version summary End-->

<!--1.19.xx-PNX Version summary Start-->
<!--1.19.10-r1-PNX Protocol Version 534-->
[#1.19.10-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.10-r1

<!--1.19.20-r1-PNX Protocol Verison 544-->
[#1.19.20-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r1
<!--1.19.20-r2-PNX Protocol Version 544-->
[#1.19.20-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r2
<!--1.19.20-r3-PNX Protocol Version 544-->
[#1.19.20-r3]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r3
<!--1.19.20-r4-PNX Protocol Version 544-->
[#1.19.20-r4]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r4
<!--1.19.20-r5-PNX Protocol Version 544-->
[#1.19.20-r5]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.20-r5

<!--1.19.21-r1-PNX Protocol Version 545-->
[#1.19.21-r1]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r1
<!--1.19.21-r2-PNX Protocol Version 545-->
[#1.19.21-r2]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r2
<!--1.19.21-r3-PNX Protocol Version 545-->
[#1.19.21-r3]: https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/1.19.21-r3
<!--1.19.xx-PNX Version summary End-->

<!--PowerNukkitX Urls-->

<!--Website Links-->
[PowerNukkitX]: https://www.powernukkitx.cn
[Maven Central]: https://search.maven.org/search?q=g:cn.powernukkitx
[Javadoc]: https://javadoc.io/doc/cn.powernukkitx/powernukkitx

<!--Social Links-->
[QQ]: https://jq.qq.com/?_wv=1027&k=6rm3gbUI
[Discord]:https://discord.gg/BcPhZCVJHJ