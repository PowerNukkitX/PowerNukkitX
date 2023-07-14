package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@SuppressWarnings("unused")
public interface BlockID {
    int AIR = 0;
    int STONE = 1;
    int GRASS = 2;
    int DIRT = 3;
    int COBBLESTONE = 4;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "COBBLESTONE", reason = "Wrong Minecraft block name")
    int COBBLE = COBBLESTONE;
    int PLANKS = 5;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "PLANKS", reason = "Wrong Minecraft block name")
    int PLANK = PLANKS;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "PLANKS", reason = "Wrong Minecraft block name")
    int WOODEN_PLANK = PLANKS;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "PLANKS", reason = "Wrong Minecraft block name")
    int WOODEN_PLANKS = PLANKS;
    int SAPLING = 6;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "SAPLING", reason = "Wrong Minecraft block name")
    int SAPLINGS = SAPLING;
    int BEDROCK = 7;
    @Since("FUTURE")
    @PowerNukkitOnly
    int FLOWING_WATER = 8;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "FLOWING_WATER for minecraft:flowing_water or STILL_WATER for minecraft:water",
            reason = "Wrong Minecraft block name")
    int WATER = FLOWING_WATER;
    int STILL_WATER = 9;

    @PowerNukkitOnly
    int FLOWING_LAVA = 10;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "FLOWING_LAVA for minecraft:flowing_lava or STILL_LAVA for minecraft:lava",
            reason = "Wrong Minecraft block name")
    int LAVA = FLOWING_LAVA;
    int STILL_LAVA = 11;
    int SAND = 12;
    int GRAVEL = 13;
    int GOLD_ORE = 14;
    int IRON_ORE = 15;
    int COAL_ORE = 16;
    int LOG = 17;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "LOG for minecraft:log or WOOD_BARK for minecraft:wood",
            reason = "Wrong Minecraft block name")
    int WOOD = LOG;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "LOG", reason = "Wrong Minecraft block name")
    int TRUNK = LOG;
    int LEAVES = 18;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "LEAVES", reason = "Wrong Minecraft block name and English word")
    int LEAVE = LEAVES;
    int SPONGE = 19;
    int GLASS = 20;
    int LAPIS_ORE = 21;
    int LAPIS_BLOCK = 22;
    int DISPENSER = 23;
    int SANDSTONE = 24;
    int NOTEBLOCK = 25;
    int BED_BLOCK = 26;
    int POWERED_RAIL = 27;
    int DETECTOR_RAIL = 28;
    int STICKY_PISTON = 29;
    int COBWEB = 30;
    int TALL_GRASS = 31;
    int BUSH = 32;
    int DEAD_BUSH = 32;
    int PISTON = 33;
    int PISTON_ARM_COLLISION = 34;
    int WOOL = 35;
    int DANDELION = 37;
    int RED_FLOWER = 38;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "RED_FLOWER", reason = "Wrong Minecraft block name")
    int POPPY = RED_FLOWER;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "RED_FLOWER", reason = "Wrong Minecraft block name")
    int ROSE = RED_FLOWER;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "RED_FLOWER", reason = "Wrong Minecraft block name")
    int FLOWER = RED_FLOWER;
    int BROWN_MUSHROOM = 39;
    int RED_MUSHROOM = 40;
    int GOLD_BLOCK = 41;
    int IRON_BLOCK = 42;
    int DOUBLE_STONE_SLAB = 43;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "DOUBLE_STONE_SLAB", reason = "Wrong Minecraft block name")
    int DOUBLE_SLAB = DOUBLE_STONE_SLAB;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "DOUBLE_STONE_SLAB", reason = "Wrong Minecraft block name")
    int DOUBLE_SLABS = DOUBLE_STONE_SLAB;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkitX", since = "FUTURE",
            replaceWith = "DOUBLE_STONE_SLAB", reason = "Wrong Minecraft block name")
    int DOUBLE_STONE_BLOCK_SLAB = DOUBLE_STONE_SLAB;
    int STONE_SLAB = 44;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "STONE_SLAB", reason = "Wrong Minecraft block name")
    int SLAB = STONE_SLAB;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "STONE_SLAB", reason = "Wrong Minecraft block name")
    int SLABS = STONE_SLAB;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkitX", since = "FUTURE",
            replaceWith = "STONE_SLAB", reason = "Wrong Minecraft block name")
    int STONE_BLOCK_SLAB = STONE_SLAB;
    int BRICKS_BLOCK = 45;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "BRICKS_BLOCK", reason = "Wrong Minecraft block name")
    int BRICKS = BRICKS_BLOCK;
    int TNT = 46;
    int BOOKSHELF = 47;
    @Since("FUTURE")
    @PowerNukkitOnly
    int MOSSY_COBBLESTONE = 48;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "MOSSY_COBBLESTONE", reason = "Wrong Minecraft block name")
    int MOSS_STONE = MOSSY_COBBLESTONE;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "MOSSY_COBBLESTONE", reason = "Wrong Minecraft block name")
    int MOSSY_STONE = MOSSY_COBBLESTONE;
    int OBSIDIAN = 49;
    int TORCH = 50;
    int FIRE = 51;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int MOB_SPAWNER = 52;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "MOB_SPAWNER", reason = "Wrong Minecraft block name")
    int MONSTER_SPAWNER = MOB_SPAWNER;
    @Since("FUTURE")
    @PowerNukkitOnly
    int OAK_STAIRS = 53;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "OAK_STAIRS", reason = "Wrong Minecraft block name")
    int WOOD_STAIRS = OAK_STAIRS;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "OAK_STAIRS", reason = "Wrong Minecraft block name")
    int WOODEN_STAIRS = OAK_STAIRS;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "OAK_STAIRS", reason = "Wrong Minecraft block name")
    int OAK_WOOD_STAIRS = OAK_STAIRS;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "OAK_STAIRS", reason = "Wrong Minecraft block name")
    int OAK_WOODEN_STAIRS = OAK_STAIRS;
    int CHEST = 54;
    int REDSTONE_WIRE = 55;
    int DIAMOND_ORE = 56;
    int DIAMOND_BLOCK = 57;
    int CRAFTING_TABLE = 58;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "CRAFTING_TABLE", reason = "Wrong Minecraft block name")
    int WORKBENCH = CRAFTING_TABLE;
    int WHEAT_BLOCK = 59;
    int FARMLAND = 60;
    int FURNACE = 61;
    int LIT_FURNACE = 62;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "LIT_FURNACE", reason = "Wrong Minecraft block name")
    int BURNING_FURNACE = LIT_FURNACE;
    int SIGN_POST = 63;
    @Since("FUTURE")
    @PowerNukkitOnly
    int OAK_DOOR_BLOCK = 64;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "OAK_DOOR_BLOCK", reason = "Wrong Minecraft block name")
    int DOOR_BLOCK = OAK_DOOR_BLOCK;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "OAK_DOOR_BLOCK", reason = "Wrong Minecraft block name")
    int WOODEN_DOOR_BLOCK = OAK_DOOR_BLOCK;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "OAK_DOOR_BLOCK", reason = "Wrong Minecraft block name")
    int WOOD_DOOR_BLOCK = OAK_DOOR_BLOCK;
    int LADDER = 65;
    int RAIL = 66;
    @Since("FUTURE")
    @PowerNukkitOnly
    int STONE_STAIRS = 67;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "STONE_STAIRS", reason = "Wrong Minecraft block name")
    int COBBLESTONE_STAIRS = STONE_STAIRS;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "STONE_STAIRS", reason = "Wrong Minecraft block name")
    int COBBLE_STAIRS = STONE_STAIRS;
    int WALL_SIGN = 68;
    int LEVER = 69;
    int STONE_PRESSURE_PLATE = 70;
    int IRON_DOOR_BLOCK = 71;
    int WOODEN_PRESSURE_PLATE = 72;
    int REDSTONE_ORE = 73;
    int LIT_REDSTONE_ORE = 74;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "LIT_REDSTONE_ORE", reason = "Wrong Minecraft block name")
    int GLOWING_REDSTONE_ORE = LIT_REDSTONE_ORE;
    int UNLIT_REDSTONE_TORCH = 75;
    int REDSTONE_TORCH = 76;
    int STONE_BUTTON = 77;
    int SNOW_LAYER = 78;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "SNOW_LAYER for minecraft:snow_layer and SNOW_BLOCK for minecraft:snow",
            reason = "Wrong Minecraft block name")
    int SNOW = SNOW_LAYER;
    int ICE = 79;
    int SNOW_BLOCK = 80;
    int CACTUS = 81;
    int CLAY_BLOCK = 82;
    int REEDS = 83;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "REEDS", reason = "Wrong Minecraft block name")
    int SUGARCANE_BLOCK = REEDS;
    int JUKEBOX = 84;
    int FENCE = 85;
    int PUMPKIN = 86;
    int NETHERRACK = 87;
    int SOUL_SAND = 88;
    int GLOWSTONE = 89;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "GLOWSTONE", reason = "Wrong Minecraft block name")
    int GLOWSTONE_BLOCK = GLOWSTONE;
    int NETHER_PORTAL = 90;
    int LIT_PUMPKIN = 91;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "LIT_PUMPKIN", reason = "Wrong Minecraft block name")
    int JACK_O_LANTERN = LIT_PUMPKIN;
    int CAKE_BLOCK = 92;
    int UNPOWERED_REPEATER = 93;
    int POWERED_REPEATER = 94;
    int INVISIBLE_BEDROCK = 95;
    int TRAPDOOR = 96;
    int MONSTER_EGG = 97;
    @Since("FUTURE")
    @PowerNukkitOnly
    int STONEBRICK = 98;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "STONEBRICK", reason = "Wrong Minecraft block name")
    int STONE_BRICKS = STONEBRICK;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "STONEBRICK", reason = "Wrong Minecraft block name")
    int STONE_BRICK = STONEBRICK;
    int BROWN_MUSHROOM_BLOCK = 99;
    int RED_MUSHROOM_BLOCK = 100;
    int IRON_BARS = 101;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "IRON_BARS", reason = "Wrong Minecraft block name")
    int IRON_BAR = IRON_BARS;
    int GLASS_PANE = 102;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "GLASS_PANE", reason = "Wrong Minecraft block name")
    int GLASS_PANEL = GLASS_PANE;
    int MELON_BLOCK = 103;
    int PUMPKIN_STEM = 104;
    int MELON_STEM = 105;
    int VINE = 106;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "VINE", reason = "Wrong Minecraft block name")
    int VINES = VINE;
    int FENCE_GATE = 107;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "FENCE_GATE", reason = "Wrong Minecraft block name")
    int FENCE_GATE_OAK = FENCE_GATE;
    int BRICK_STAIRS = 108;
    int STONE_BRICK_STAIRS = 109;
    int MYCELIUM = 110;
    @Since("FUTURE")
    @PowerNukkitOnly
    int WATERLILY = 111;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "WATERLILY", reason = "Wrong Minecraft block name")
    int WATER_LILY = WATERLILY;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "WATERLILY", reason = "Wrong Minecraft block name")
    int LILY_PAD = WATERLILY;
    int NETHER_BRICK_BLOCK = 112;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "NETHER_BRICK_BLOCK", reason = "Confusing name with block and item")
    int NETHER_BRICKS = NETHER_BRICK_BLOCK;
    int NETHER_BRICK_FENCE = 113;
    int NETHER_BRICKS_STAIRS = 114;
    int NETHER_WART_BLOCK = 115;
    int ENCHANTING_TABLE = 116;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "ENCHANTING_TABLE", reason = "Wrong Minecraft block name")
    int ENCHANT_TABLE = ENCHANTING_TABLE;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "ENCHANTING_TABLE", reason = "Wrong Minecraft block name")
    int ENCHANTMENT_TABLE = ENCHANTING_TABLE;
    int BREWING_STAND_BLOCK = 117;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "ENCHANTING_TABLE", reason = "Wrong Minecraft block name")
    int BREWING_BLOCK = BREWING_STAND_BLOCK;
    int CAULDRON_BLOCK = 118;
    int END_PORTAL = 119;
    int END_PORTAL_FRAME = 120;
    int END_STONE = 121;
    int DRAGON_EGG = 122;
    int REDSTONE_LAMP = 123;
    int LIT_REDSTONE_LAMP = 124;
    int DROPPER = 125;
    int ACTIVATOR_RAIL = 126;
    int COCOA = 127;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "COCOA", reason = "Wrong Minecraft block name")
    int COCOA_BLOCK = COCOA;
    int SANDSTONE_STAIRS = 128;
    int EMERALD_ORE = 129;
    int ENDER_CHEST = 130;
    int TRIPWIRE_HOOK = 131;
    int TRIP_WIRE = 132;
    int EMERALD_BLOCK = 133;
    @Since("FUTURE")
    @PowerNukkitOnly
    int SPRUCE_STAIRS = 134;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "SPRUCE_STAIRS", reason = "Wrong Minecraft block name")
    int SPRUCE_WOOD_STAIRS = SPRUCE_STAIRS;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "SPRUCE_STAIRS", reason = "Wrong Minecraft block name")
    int SPRUCE_WOODEN_STAIRS = SPRUCE_STAIRS;
    @Since("FUTURE")
    @PowerNukkitOnly
    int BIRCH_STAIRS = 135;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "BIRCH_STAIRS", reason = "Wrong Minecraft block name")
    int BIRCH_WOOD_STAIRS = BIRCH_STAIRS;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "BIRCH_STAIRS", reason = "Wrong Minecraft block name")
    int BIRCH_WOODEN_STAIRS = BIRCH_STAIRS;
    @Since("FUTURE")
    @PowerNukkitOnly
    int JUNGLE_STAIRS = 136;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "JUNGLE_STAIRS", reason = "Wrong Minecraft block name")
    int JUNGLE_WOOD_STAIRS = JUNGLE_STAIRS;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE",
            replaceWith = "JUNGLE_STAIRS", reason = "Wrong Minecraft block name")
    int JUNGLE_WOODEN_STAIRS = JUNGLE_STAIRS;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    int COMMAND_BLOCK = 137;
    int BEACON = 138;
    int COBBLE_WALL = 139;
    int STONE_WALL = 139;
    int COBBLESTONE_WALL = COBBLE_WALL;
    int FLOWER_POT_BLOCK = 140;
    int CARROT_BLOCK = 141;
    int POTATO_BLOCK = 142;
    int WOODEN_BUTTON = 143;
    int SKULL_BLOCK = 144;
    int ANVIL = 145;
    int TRAPPED_CHEST = 146;
    int LIGHT_WEIGHTED_PRESSURE_PLATE = 147;
    int HEAVY_WEIGHTED_PRESSURE_PLATE = 148;
    int UNPOWERED_COMPARATOR = 149;
    int POWERED_COMPARATOR = 150;
    int DAYLIGHT_DETECTOR = 151;
    int REDSTONE_BLOCK = 152;
    int QUARTZ_ORE = 153;
    int HOPPER_BLOCK = 154;
    int QUARTZ_BLOCK = 155;
    int QUARTZ_STAIRS = 156;
    int DOUBLE_WOOD_SLAB = 157;
    int DOUBLE_WOODEN_SLAB = 157;
    int DOUBLE_WOOD_SLABS = 157;
    int DOUBLE_WOODEN_SLABS = 157;
    int WOOD_SLAB = 158;
    int WOODEN_SLAB = 158;
    int WOOD_SLABS = 158;
    int WOODEN_SLABS = 158;
    int STAINED_TERRACOTTA = 159;
    int STAINED_HARDENED_CLAY = STAINED_TERRACOTTA;
    int STAINED_GLASS_PANE = 160;
    int LEAVES2 = 161;
    int LEAVE2 = 161;
    int WOOD2 = 162;
    int TRUNK2 = 162;
    int LOG2 = 162;
    int ACACIA_WOOD_STAIRS = 163;
    int ACACIA_WOODEN_STAIRS = 163;
    int DARK_OAK_WOOD_STAIRS = 164;
    int DARK_OAK_WOODEN_STAIRS = 164;
    int SLIME_BLOCK = 165;

    int IRON_TRAPDOOR = 167;
    int PRISMARINE = 168;
    int SEA_LANTERN = 169;
    int HAY_BALE = 170;
    int CARPET = 171;
    int TERRACOTTA = 172;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int HARDENED_CLAY = TERRACOTTA;
    int COAL_BLOCK = 173;
    int PACKED_ICE = 174;
    int DOUBLE_PLANT = 175;
    int STANDING_BANNER = 176;
    int WALL_BANNER = 177;
    int DAYLIGHT_DETECTOR_INVERTED = 178;
    int RED_SANDSTONE = 179;
    int RED_SANDSTONE_STAIRS = 180;
    int DOUBLE_RED_SANDSTONE_SLAB = 181;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkitX", since = "FUTURE",
            replaceWith = "DOUBLE_RED_SANDSTONE_SLAB", reason = "Wrong Minecraft block name")
    int DOUBLE_STONE_BLOCK_SLAB2 = DOUBLE_RED_SANDSTONE_SLAB;
    int RED_SANDSTONE_SLAB = 182;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkitX", since = "FUTURE",
            replaceWith = "RED_SANDSTONE_SLAB", reason = "Wrong Minecraft block name")
    int STONE_BLOCK_SLAB2 = RED_SANDSTONE_SLAB;
    int FENCE_GATE_SPRUCE = 183;
    int FENCE_GATE_BIRCH = 184;
    int FENCE_GATE_JUNGLE = 185;
    int FENCE_GATE_DARK_OAK = 186;
    int FENCE_GATE_ACACIA = 187;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    int REPEATING_COMMAND_BLOCK = 188;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    int CHAIN_COMMAND_BLOCK = 189;

    int SPRUCE_DOOR_BLOCK = 193;
    int BIRCH_DOOR_BLOCK = 194;
    int JUNGLE_DOOR_BLOCK = 195;
    int ACACIA_DOOR_BLOCK = 196;
    int DARK_OAK_DOOR_BLOCK = 197;
    int GRASS_PATH = 198;
    int ITEM_FRAME_BLOCK = 199;
    int CHORUS_FLOWER = 200;
    int PURPUR_BLOCK = 201;
    //int COLORED_TORCH_RG = 202;
    int PURPUR_STAIRS = 203;
    //int COLORED_TORCH_BP = 204;
    int UNDYED_SHULKER_BOX = 205;
    int END_BRICKS = 206;
    //Note: frosted ice CAN NOT BE HARVESTED WITH HAND -- canHarvestWithHand method should be overridden FALSE.
    int ICE_FROSTED = 207;
    int END_ROD = 208;
    int END_GATEWAY = 209;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int ALLOW = 210;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int DENY = 211;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int BORDER_BLOCK = 212;
    int MAGMA = 213;
    int BLOCK_NETHER_WART_BLOCK = 214;
    int RED_NETHER_BRICK = 215;
    int BONE_BLOCK = 216;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int STRUCTURE_VOID = 217;
    int SHULKER_BOX = 218;
    int PURPLE_GLAZED_TERRACOTTA = 219;
    int WHITE_GLAZED_TERRACOTTA = 220;
    int ORANGE_GLAZED_TERRACOTTA = 221;
    int MAGENTA_GLAZED_TERRACOTTA = 222;
    int LIGHT_BLUE_GLAZED_TERRACOTTA = 223;
    int YELLOW_GLAZED_TERRACOTTA = 224;
    int LIME_GLAZED_TERRACOTTA = 225;
    int PINK_GLAZED_TERRACOTTA = 226;
    int GRAY_GLAZED_TERRACOTTA = 227;
    int SILVER_GLAZED_TERRACOTTA = 228;
    int CYAN_GLAZED_TERRACOTTA = 229;

    int BLUE_GLAZED_TERRACOTTA = 231;
    int BROWN_GLAZED_TERRACOTTA = 232;
    int GREEN_GLAZED_TERRACOTTA = 233;
    int RED_GLAZED_TERRACOTTA = 234;
    int BLACK_GLAZED_TERRACOTTA = 235;
    int CONCRETE = 236;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CONCRETEPOWDER = 237;
    int CONCRETE_POWDER = CONCRETEPOWDER;

    int CHORUS_PLANT = 240;
    int STAINED_GLASS = 241;

    int PODZOL = 243;
    int BEETROOT_BLOCK = 244;
    int STONECUTTER = 245;
    int GLOWING_OBSIDIAN = 246;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int NETHERREACTOR = 247;
    int NETHER_REACTOR = NETHERREACTOR;

    @Since("1.5.0.0-PN")
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit",
            reason = "This was added by Cloudburst Nukkit, but it is a tecnical block, avoid usinig it.",
            since = "1.5.0.0-PN")
    int INFO_UPDATE = 248;

    @Since("1.5.0.0-PN")
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit",
            reason = "This was added by Cloudburst Nukkit, but it is a tecnical block, avoid usinig it.",
            since = "1.5.0.0-PN")
    int INFO_UPDATE2 = 249;

    int PISTON_EXTENSION = 250;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int MOVING_BLOCK = PISTON_EXTENSION;

    int OBSERVER = 251;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int STRUCTURE_BLOCK = 252;

    @PowerNukkitOnly
    int PRISMARINE_STAIRS = 257;
    @PowerNukkitOnly
    int DARK_PRISMARINE_STAIRS = 258;
    @PowerNukkitOnly
    int PRISMARINE_BRICKS_STAIRS = 259;
    @PowerNukkitOnly
    int STRIPPED_SPRUCE_LOG = 260;
    @PowerNukkitOnly
    int STRIPPED_BIRCH_LOG = 261;
    @PowerNukkitOnly
    int STRIPPED_JUNGLE_LOG = 262;
    @PowerNukkitOnly
    int STRIPPED_ACACIA_LOG = 263;
    @PowerNukkitOnly
    int STRIPPED_DARK_OAK_LOG = 264;
    @PowerNukkitOnly
    int STRIPPED_OAK_LOG = 265;
    @PowerNukkitOnly
    int BLUE_ICE = 266;

    @PowerNukkitOnly
    int SEAGRASS = 385;
    @PowerNukkitOnly
    int CORAL = 386;
    @PowerNukkitOnly
    int CORAL_BLOCK = 387;
    @PowerNukkitOnly
    int CORAL_FAN = 388;
    @PowerNukkitOnly
    int CORAL_FAN_DEAD = 389;
    @PowerNukkitOnly
    int CORAL_FAN_HANG = 390;
    @PowerNukkitOnly
    int CORAL_FAN_HANG2 = 391;
    @PowerNukkitOnly
    int CORAL_FAN_HANG3 = 392;
    @PowerNukkitOnly
    int BLOCK_KELP = 393;
    @PowerNukkitOnly
    int DRIED_KELP_BLOCK = 394;
    @PowerNukkitOnly
    int ACACIA_BUTTON = 395;
    @PowerNukkitOnly
    int BIRCH_BUTTON = 396;
    @PowerNukkitOnly
    int DARK_OAK_BUTTON = 397;
    @PowerNukkitOnly
    int JUNGLE_BUTTON = 398;
    @PowerNukkitOnly
    int SPRUCE_BUTTON = 399;
    @PowerNukkitOnly
    int ACACIA_TRAPDOOR = 400;
    @PowerNukkitOnly
    int BIRCH_TRAPDOOR = 401;
    @PowerNukkitOnly
    int DARK_OAK_TRAPDOOR = 402;
    @PowerNukkitOnly
    int JUNGLE_TRAPDOOR = 403;
    @PowerNukkitOnly
    int SPRUCE_TRAPDOOR = 404;
    @PowerNukkitOnly
    int ACACIA_PRESSURE_PLATE = 405;
    @PowerNukkitOnly
    int BIRCH_PRESSURE_PLATE = 406;
    @PowerNukkitOnly
    int DARK_OAK_PRESSURE_PLATE = 407;
    @PowerNukkitOnly
    int JUNGLE_PRESSURE_PLATE = 408;
    @PowerNukkitOnly
    int SPRUCE_PRESSURE_PLATE = 409;
    @PowerNukkitOnly
    int CARVED_PUMPKIN = 410;
    @PowerNukkitOnly
    int SEA_PICKLE = 411;
    @PowerNukkitOnly
    int CONDUIT = 412;

    @PowerNukkitOnly
    int TURTLE_EGG = 414;
    @PowerNukkitOnly
    int BUBBLE_COLUMN = 415;
    @PowerNukkitOnly
    int BARRIER = 416;
    @PowerNukkitOnly
    int STONE_SLAB3 = 417;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkitX", since = "FUTURE",
            replaceWith = "STONE_SLAB3", reason = "Wrong Minecraft block name")
    int STONE_BLOCK_SLAB3 = STONE_SLAB3;
    @PowerNukkitOnly
    int BAMBOO = 418;
    @PowerNukkitOnly
    int BAMBOO_SAPLING = 419;
    @PowerNukkitOnly
    int SCAFFOLDING = 420;
    @PowerNukkitOnly
    int STONE_SLAB4 = 421;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkitX", since = "FUTURE",
            replaceWith = "STONE_SLAB4", reason = "Wrong Minecraft block name")
    int STONE_BLOCK_SLAB4 = STONE_SLAB4;
    @PowerNukkitOnly
    int DOUBLE_STONE_SLAB3 = 422;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkitX", since = "FUTURE",
            replaceWith = "STONE_SLAB4", reason = "Wrong Minecraft block name")
    int DOUBLE_STONE_BLOCK_SLAB3 = DOUBLE_STONE_SLAB3;
    @PowerNukkitOnly
    int DOUBLE_STONE_SLAB4 = 423;
    @Deprecated
    @DeprecationDetails(by = "PowerNukkitX", since = "FUTURE",
            replaceWith = "STONE_SLAB4", reason = "Wrong Minecraft block name")
    int DOUBLE_STONE_BLOCK_SLAB4 = DOUBLE_STONE_SLAB4;
    @PowerNukkitOnly
    int GRANITE_STAIRS = 424;
    @PowerNukkitOnly
    int DIORITE_STAIRS = 425;
    @PowerNukkitOnly
    int ANDESITE_STAIRS = 426;
    @PowerNukkitOnly
    int POLISHED_GRANITE_STAIRS = 427;
    @PowerNukkitOnly
    int POLISHED_DIORITE_STAIRS = 428;
    @PowerNukkitOnly
    int POLISHED_ANDESITE_STAIRS = 429;
    @PowerNukkitOnly
    int MOSSY_STONE_BRICK_STAIRS = 430;
    @PowerNukkitOnly
    int SMOOTH_RED_SANDSTONE_STAIRS = 431;
    @PowerNukkitOnly
    int SMOOTH_SANDSTONE_STAIRS = 432;
    @PowerNukkitOnly
    int END_BRICK_STAIRS = 433;
    @PowerNukkitOnly
    int MOSSY_COBBLESTONE_STAIRS = 434;
    @PowerNukkitOnly
    int NORMAL_STONE_STAIRS = 435;
    @PowerNukkitOnly
    int SPRUCE_STANDING_SIGN = 436;
    @PowerNukkitOnly
    int SPRUCE_WALL_SIGN = 437;
    @PowerNukkitOnly
    int SMOOTH_STONE = 438;
    @PowerNukkitOnly
    int RED_NETHER_BRICK_STAIRS = 439;
    @PowerNukkitOnly
    int SMOOTH_QUARTZ_STAIRS = 440;
    @PowerNukkitOnly
    int BIRCH_STANDING_SIGN = 441;
    @PowerNukkitOnly
    int BIRCH_WALL_SIGN = 442;
    @PowerNukkitOnly
    int JUNGLE_STANDING_SIGN = 443;
    @PowerNukkitOnly
    int JUNGLE_WALL_SIGN = 444;
    @PowerNukkitOnly
    int ACACIA_STANDING_SIGN = 445;
    @PowerNukkitOnly
    int ACACIA_WALL_SIGN = 446;
    @PowerNukkitOnly
    int DARKOAK_STANDING_SIGN = 447;
    @PowerNukkitOnly
    int DARK_OAK_STANDING_SIGN = 447;
    @PowerNukkitOnly
    int DARKOAK_WALL_SIGN = 448;
    @PowerNukkitOnly
    int DARK_OAK_WALL_SIGN = 448;
    @PowerNukkitOnly
    int LECTERN = 449;
    @PowerNukkitOnly
    int GRINDSTONE = 450;
    @PowerNukkitOnly
    int BLAST_FURNACE = 451;
    @PowerNukkitOnly
    int STONECUTTER_BLOCK = 452;
    @PowerNukkitOnly
    int SMOKER = 453;
    @PowerNukkitOnly
    int LIT_SMOKER = 454;
    @PowerNukkitOnly
    int CARTOGRAPHY_TABLE = 455;
    @PowerNukkitOnly
    int FLETCHING_TABLE = 456;
    @PowerNukkitOnly
    int SMITHING_TABLE = 457;
    @PowerNukkitOnly
    int BARREL = 458;
    @PowerNukkitOnly
    int LOOM = 459;

    @PowerNukkitOnly
    int BELL = 461;
    @PowerNukkitOnly
    int SWEET_BERRY_BUSH = 462;
    @PowerNukkitOnly
    int LANTERN = 463;
    @PowerNukkitOnly
    int CAMPFIRE_BLOCK = 464;
    @PowerNukkitOnly
    int LAVA_CAULDRON = 465;
    @PowerNukkitOnly
    int JIGSAW = 466;
    @PowerNukkitOnly
    int WOOD_BARK = 467;
    @PowerNukkitOnly
    int COMPOSTER = 468;
    @PowerNukkitOnly
    int LIT_BLAST_FURNACE = 469;
    @PowerNukkitOnly
    int LIGHT_BLOCK = 470;
    @PowerNukkitOnly
    int WITHER_ROSE = 471;
    @PowerNukkitOnly
    int STICKY_PISTON_ARM_COLLISION = 472;
    @PowerNukkitOnly
    int PISTON_HEAD_STICKY = 472;
    @PowerNukkitOnly
    int BEE_NEST = 473;
    @PowerNukkitOnly
    int BEEHIVE = 474;
    @PowerNukkitOnly
    int HONEY_BLOCK = 475;
    @PowerNukkitOnly
    int HONEYCOMB_BLOCK = 476;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int LODESTONE = 477;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_ROOTS = 478;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_ROOTS = 479;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_STEM = 480;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_STEM = 481;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_WART_BLOCK = 482;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_FUNGUS = 483;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_FUNGUS = 484;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int SHROOMLIGHT = 485;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WEEPING_VINES = 486;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_NYLIUM = 487;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_NYLIUM = 488;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int BASALT = 489;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BASALT = 490;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int SOUL_SOIL = 491;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int SOUL_FIRE = 492;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int NETHER_SPROUTS_BLOCK = 493;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int TARGET = 494;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int STRIPPED_CRIMSON_STEM = 495;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int STRIPPED_WARPED_STEM = 496;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_PLANKS = 497;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_PLANKS = 498;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_DOOR_BLOCK = 499;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_DOOR_BLOCK = 500;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_TRAPDOOR = 501;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_TRAPDOOR = 502;
    // 503
    // 504
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_STANDING_SIGN = 505;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_STANDING_SIGN = 506;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_WALL_SIGN = 507;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_WALL_SIGN = 508;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_STAIRS = 509;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_STAIRS = 510;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_FENCE = 511;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_FENCE = 512;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_FENCE_GATE = 513;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_FENCE_GATE = 514;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_BUTTON = 515;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_BUTTON = 516;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_PRESSURE_PLATE = 517;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_PRESSURE_PLATE = 518;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_SLAB = 519;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_SLAB = 520;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_DOUBLE_SLAB = 521;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_DOUBLE_SLAB = 522;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int SOUL_TORCH = 523;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int SOUL_LANTERN = 524;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int NETHERITE_BLOCK = 525;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int ANCIENT_DERBRIS = 526;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int RESPAWN_ANCHOR = 527;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int BLACKSTONE = 528;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_BRICKS = 529;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_BRICK_STAIRS = 530;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int BLACKSTONE_STAIRS = 531;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int BLACKSTONE_WALL = 532;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_BRICK_WALL = 533;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CHISELED_POLISHED_BLACKSTONE = 534;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRACKED_POLISHED_BLACKSTONE_BRICKS = 535;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int GILDED_BLACKSTONE = 536;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int BLACKSTONE_SLAB = 537;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int BLACKSTONE_DOUBLE_SLAB = 538;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_BRICK_SLAB = 539;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB = 540;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CHAIN_BLOCK = 541;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int TWISTING_VINES = 542;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int NETHER_GOLD_ORE = 543;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRYING_OBSIDIAN = 544;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int SOUL_CAMPFIRE_BLOCK = 545;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE = 546;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_STAIRS = 547;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_SLAB = 548;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_DOUBLE_SLAB = 549;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_PRESSURE_PLATE = 550;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_BUTTON = 551;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int POLISHED_BLACKSTONE_WALL = 552;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int WARPED_HYPHAE = 553;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRIMSON_HYPHAE = 554;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int STRIPPED_CRIMSON_HYPHAE = 555;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int STRIPPED_WARPED_HYPHAE = 556;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CHISELED_NETHER_BRICKS = 557;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int CRACKED_NETHER_BRICKS = 558;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    int QUARTZ_BRICKS = 559;
    // 560 Special block: minecraft:unknown
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int POWDER_SNOW = 561;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int SCULK_SENSOR = 562;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int POINTED_DRIPSTONE = 563;
    // 564 (unused)
    // 565 (unused)
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int COPPER_ORE = 566;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int LIGHTNING_ROD = 567;
    // 568 (unused)
    // 569 (unused)
    // 570 (unused)
    // 571 (unused)
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DRIPSTONE_BLOCK = 572;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DIRT_WITH_ROOTS = 573;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int HANGING_ROOTS = 574;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MOSS_BLOCK = 575;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int SPORE_BLOSSOM = 576;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CAVE_VINES = 577;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int BIG_DRIPLEAF = 578;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int AZALEA_LEAVES = 579;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int AZALEA_LEAVES_FLOWERED = 580;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CALCITE = 581;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int AMETHYST_BLOCK = 582;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int BUDDING_AMETHYST = 583;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int AMETHYST_CLUSTER = 584;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int LARGE_AMETHYST_BUD = 585;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MEDIUM_AMETHYST_BUD = 586;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int SMALL_AMETHYST_BUD = 587;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int TUFF = 588;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int TINTED_GLASS = 589;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MOSS_CARPET = 590;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int SMALL_DRIPLEAF_BLOCK = 591;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int AZALEA = 592;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int FLOWERING_AZALEA = 593;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int GLOW_FRAME = 594;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int COPPER_BLOCK = 595;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int EXPOSED_COPPER = 596;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WEATHERED_COPPER = 597;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int OXIDIZED_COPPER = 598;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_COPPER = 599;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_EXPOSED_COPPER = 600;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_WEATHERED_COPPER = 601;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CUT_COPPER = 602;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int EXPOSED_CUT_COPPER = 603;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WEATHERED_CUT_COPPER = 604;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int OXIDIZED_CUT_COPPER = 605;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_CUT_COPPER = 606;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_EXPOSED_CUT_COPPER = 607;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_WEATHERED_CUT_COPPER = 608;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CUT_COPPER_STAIRS = 609;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int EXPOSED_CUT_COPPER_STAIRS = 610;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WEATHERED_CUT_COPPER_STAIRS = 611;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int OXIDIZED_CUT_COPPER_STAIRS = 612;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_CUT_COPPER_STAIRS = 613;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_EXPOSED_CUT_COPPER_STAIRS = 614;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_WEATHERED_CUT_COPPER_STAIRS = 615;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CUT_COPPER_SLAB = 616;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int EXPOSED_CUT_COPPER_SLAB = 617;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WEATHERED_CUT_COPPER_SLAB = 618;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int OXIDIZED_CUT_COPPER_SLAB = 619;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_CUT_COPPER_SLAB = 620;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_EXPOSED_CUT_COPPER_SLAB = 621;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_WEATHERED_CUT_COPPER_SLAB = 622;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DOUBLE_CUT_COPPER_SLAB = 623;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int EXPOSED_DOUBLE_CUT_COPPER_SLAB = 624;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WEATHERED_DOUBLE_CUT_COPPER_SLAB = 625;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int OXIDIZED_DOUBLE_CUT_COPPER_SLAB = 626;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_DOUBLE_CUT_COPPER_SLAB = 627;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB = 628;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB = 629;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CAVE_VINES_BODY_WITH_BERRIES = 630;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CAVE_VINES_HEAD_WITH_BERRIES = 631;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int SMOOTH_BASALT = 632;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE = 633;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int COBBLED_DEEPSLATE = 634;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int COBBLED_DEEPSLATE_SLAB = 635;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int COBBLED_DEEPSLATE_STAIRS = 636;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int COBBLED_DEEPSLATE_WALL = 637;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int POLISHED_DEEPSLATE = 638;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int POLISHED_DEEPSLATE_SLAB = 639;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int POLISHED_DEEPSLATE_STAIRS = 640;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int POLISHED_DEEPSLATE_WALL = 641;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_TILES = 642;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_TILE_SLAB = 643;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_TILE_STAIRS = 644;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_TILE_WALL = 645;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_BRICKS = 646;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_BRICK_SLAB = 647;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_BRICK_STAIRS = 648;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_BRICK_WALL = 649;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CHISELED_DEEPSLATE = 650;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int COBBLED_DEEPSLATE_DOUBLE_SLAB = 651;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int POLISHED_DEEPSLATE_DOUBLE_SLAB = 652;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_TILE_DOUBLE_SLAB = 653;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_BRICK_DOUBLE_SLAB = 654;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_LAPIS_ORE = 655;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_IRON_ORE = 656;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_GOLD_ORE = 657;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_REDSTONE_ORE = 658;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int LIT_DEEPSLATE_REDSTONE_ORE = 659;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_DIAMOND_ORE = 660;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_COAL_ORE = 661;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_EMERALD_ORE = 662;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DEEPSLATE_COPPER_ORE = 663;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CRACKED_DEEPSLATE_TILES = 664;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CRACKED_DEEPSLATE_BRICKS = 665;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int GLOW_LICHEN = 666;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CANDLE = 667;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WHITE_CANDLE = 668;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int ORANGE_CANDLE = 669;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MAGENTA_CANDLE = 670;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int LIGHT_BLUE_CANDLE = 671;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int YELLOW_CANDLE = 672;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int LIME_CANDLE = 673;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int PINK_CANDLE = 674;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int GRAY_CANDLE = 675;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int LIGHT_GRAY_CANDLE = 676;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CYAN_CANDLE = 677;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int PURPLE_CANDLE = 678;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int BLUE_CANDLE = 679;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int BROWN_CANDLE = 680;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int GREEN_CANDLE = 681;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int RED_CANDLE = 682;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int BLACK_CANDLE = 683;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CANDLE_CAKE = 684;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WHITE_CANDLE_CAKE = 685;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int ORANGE_CANDLE_CAKE = 686;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MAGENTA_CANDLE_CAKE = 687;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int LIGHT_BLUE_CANDLE_CAKE = 688;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int YELLOW_CANDLE_CAKE = 689;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int LIME_CANDLE_CAKE = 690;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int PINK_CANDLE_CAKE = 691;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int GRAY_CANDLE_CAKE = 692;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int LIGHT_GRAY_CANDLE_CAKE = 693;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int CYAN_CANDLE_CAKE = 694;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int PURPLE_CANDLE_CAKE = 695;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int BLUE_CANDLE_CAKE = 696;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int BROWN_CANDLE_CAKE = 697;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int GREEN_CANDLE_CAKE = 698;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int RED_CANDLE_CAKE = 699;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int BLACK_CANDLE_CAKE = 700;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_OXIDIZED_COPPER = 701;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_OXIDIZED_CUT_COPPER = 702;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_OXIDIZED_CUT_COPPER_STAIRS = 703;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_OXIDIZED_CUT_COPPER_SLAB = 704;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB = 705;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int RAW_IRON_BLOCK = 706;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int RAW_COPPER_BLOCK = 707;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int RAW_GOLD_BLOCK = 708;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int INFESTED_DEEPSLATE = 709;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int SCULK = 713;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int SCULK_VEIN = 714;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int SCULK_CATALYST = 715;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int SCULK_SHRIEKER = 716;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int REINFORCED_DEEPSLATE = 721;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int FROG_SPAWN = 723;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int PEARLESCENT_FROGLIGHT = 724;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int VERDANT_FROGLIGHT = 725;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int OCHRE_FROGLIGHT = 726;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_LEAVES = 727;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MUD = 728;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_PROPAGULE = 729;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MUD_BRICKS = 730;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_PROPAGULE_HANGING = 731;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int PACKED_MUD = 732;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MUD_BRICK_SLAB = 733;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MUD_BRICK_DOUBLE_SLAB = 734;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MUD_BRICK_STAIRS = 735;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MUD_BRICK_WALL = 736;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_ROOTS = 737;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MUDDY_MANGROVE_ROOTS = 738;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_LOG = 739;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int STRIPPED_MANGROVE_LOG = 740;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_PLANKS = 741;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_BUTTON = 742;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_STAIRS = 743;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_SLAB = 744;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_PRESSURE_PLATE = 745;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_FENCE = 746;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_FENCE_GATE = 747;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_DOOR = 748;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_STANDING_SIGN = 749;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_WALL_SIGN = 750;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_TRAPDOOR = 751;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int MANGROVE_WOOD = 752;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int STRIPPED_MANGROVE_WOOD = 753;
    @Since("1.6.0.0-PNX")
    @PowerNukkitXOnly
    int DOUBLE_MANGROVE_SLAB = 754;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int OAK_HANGING_SIGN = 755;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int SPRUCE_HANGING_SIGN = 756;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BIRCH_HANGING_SIGN = 757;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int JUNGLE_HANGING_SIGN = 758;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int ACACIA_HANGING_SIGN = 759;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int DARK_OAK_HANGING_SIGN = 760;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CRIMSON_HANGING_SIGN = 761;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int WARPED_HANGING_SIGN = 762;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int MANGROVE_HANGING_SIGN = 763;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_MOSAIC = 764;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_PLANKS = 765;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_BUTTON = 766;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_STAIRS = 767;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_SLAB = 768;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_PRESSURE_PLATE = 769;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_FENCE = 770;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_FENCE_GATE = 771;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_DOOR = 772;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_STANDING_SIGN = 773;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_WALL_SIGN = 774;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_TRAPDOOR = 775;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_DOUBLE_SLAB = 776;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_HANGING_SIGN = 777;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_MOSAIC_STAIRS = 778;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_MOSAIC_SLAB = 779;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_MOSAIC_DOUBLE_SLAB = 780;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHISELED_BOOKSHELF = 781;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int BAMBOO_BLOCK = 782;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int STRIPPED_BAMBOO_BLOCK = 783;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int SUSPICIOUS_SAND = 784;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_BUTTON = 785;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_DOOR = 786;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_FENCE = 787;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_FENCE_GATE = 788;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int STRIPPED_CHERRY_LOG = 790;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_LOG = 791;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_PLANKS = 792;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_PRESSURE_PLATE = 793;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_SLAB = 794;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int DOUBLE_CHERRY_SLAB = 795;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_STAIRS = 796;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_STANDING_SIGN = 797;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_TRAPDOOR = 798;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_WALL_SIGN = 799;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int STRIPPED_CHERRY_WOOD = 800;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_WOOD = 801;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_SAPLING = 802;
    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    int CHERRY_LEAVES = 803;
    @PowerNukkitXOnly
    @Since("1.20.10-r1")
    int PINK_PETALS = 804;
}
