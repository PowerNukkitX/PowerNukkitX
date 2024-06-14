package cn.nukkit.registry;

import cn.nukkit.block.*;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.Plugin;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import me.sunlan.fastreflection.FastConstructor;
import me.sunlan.fastreflection.FastMemberLoader;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Cool_Loong | Mcayear | KoshakMineDEV | WWMB | Draglis
 */
@Slf4j
public final class BlockRegistry implements BlockID, IRegistry<String, Block, Class<? extends Block>> {
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);
    private static final Set<String> KEYSET = new HashSet<>();
    private static final Object2ObjectOpenHashMap<String, FastConstructor<? extends Block>> CACHE_CONSTRUCTORS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, BlockProperties> PROPERTIES = new Object2ObjectOpenHashMap<>();
    private static final Map<Plugin, List<CustomBlockDefinition>> CUSTOM_BLOCK_DEFINITIONS = new LinkedHashMap<>();

    public static final Set<String> skipBlockSet = Set.of(
            "minecraft:camera",
            "minecraft:chemical_heat",
            "minecraft:chemistry_table",
            "minecraft:colored_torch_bp",
            "minecraft:colored_torch_rg",
            "minecraft:element_0",
            "minecraft:element_1",
            "minecraft:element_10",
            "minecraft:element_100",
            "minecraft:element_101",
            "minecraft:element_102",
            "minecraft:element_103",
            "minecraft:element_104",
            "minecraft:element_105",
            "minecraft:element_106",
            "minecraft:element_107",
            "minecraft:element_108",
            "minecraft:element_109",
            "minecraft:element_11",
            "minecraft:element_110",
            "minecraft:element_111",
            "minecraft:element_112",
            "minecraft:element_113",
            "minecraft:element_114",
            "minecraft:element_115",
            "minecraft:element_116",
            "minecraft:element_117",
            "minecraft:element_118",
            "minecraft:element_12",
            "minecraft:element_13",
            "minecraft:element_14",
            "minecraft:element_15",
            "minecraft:element_16",
            "minecraft:element_17",
            "minecraft:element_18",
            "minecraft:element_19",
            "minecraft:element_2",
            "minecraft:element_20",
            "minecraft:element_21",
            "minecraft:element_22",
            "minecraft:element_23",
            "minecraft:element_24",
            "minecraft:element_25",
            "minecraft:element_26",
            "minecraft:element_27",
            "minecraft:element_28",
            "minecraft:element_29",
            "minecraft:element_3",
            "minecraft:element_30",
            "minecraft:element_31",
            "minecraft:element_32",
            "minecraft:element_33",
            "minecraft:element_34",
            "minecraft:element_35",
            "minecraft:element_36",
            "minecraft:element_37",
            "minecraft:element_38",
            "minecraft:element_39",
            "minecraft:element_4",
            "minecraft:element_40",
            "minecraft:element_41",
            "minecraft:element_42",
            "minecraft:element_43",
            "minecraft:element_44",
            "minecraft:element_45",
            "minecraft:element_46",
            "minecraft:element_47",
            "minecraft:element_48",
            "minecraft:element_49",
            "minecraft:element_5",
            "minecraft:element_50",
            "minecraft:element_51",
            "minecraft:element_52",
            "minecraft:element_53",
            "minecraft:element_54",
            "minecraft:element_55",
            "minecraft:element_56",
            "minecraft:element_57",
            "minecraft:element_58",
            "minecraft:element_59",
            "minecraft:element_6",
            "minecraft:element_60",
            "minecraft:element_61",
            "minecraft:element_62",
            "minecraft:element_63",
            "minecraft:element_64",
            "minecraft:element_65",
            "minecraft:element_66",
            "minecraft:element_67",
            "minecraft:element_68",
            "minecraft:element_69",
            "minecraft:element_7",
            "minecraft:element_70",
            "minecraft:element_71",
            "minecraft:element_72",
            "minecraft:element_73",
            "minecraft:element_74",
            "minecraft:element_75",
            "minecraft:element_76",
            "minecraft:element_77",
            "minecraft:element_78",
            "minecraft:element_79",
            "minecraft:element_8",
            "minecraft:element_80",
            "minecraft:element_81",
            "minecraft:element_82",
            "minecraft:element_83",
            "minecraft:element_84",
            "minecraft:element_85",
            "minecraft:element_86",
            "minecraft:element_87",
            "minecraft:element_88",
            "minecraft:element_89",
            "minecraft:element_9",
            "minecraft:element_90",
            "minecraft:element_91",
            "minecraft:element_92",
            "minecraft:element_93",
            "minecraft:element_94",
            "minecraft:element_95",
            "minecraft:element_96",
            "minecraft:element_97",
            "minecraft:element_98",
            "minecraft:element_99",
            "minecraft:hard_black_stained_glass",
            "minecraft:hard_black_stained_glass_pane",
            "minecraft:hard_blue_stained_glass",
            "minecraft:hard_blue_stained_glass_pane",
            "minecraft:hard_brown_stained_glass",
            "minecraft:hard_brown_stained_glass_pane",
            "minecraft:hard_cyan_stained_glass",
            "minecraft:hard_cyan_stained_glass_pane",
            "minecraft:hard_glass",
            "minecraft:hard_glass_pane",
            "minecraft:hard_gray_stained_glass",
            "minecraft:hard_gray_stained_glass_pane",
            "minecraft:hard_green_stained_glass",
            "minecraft:hard_green_stained_glass_pane",
            "minecraft:hard_light_blue_stained_glass",
            "minecraft:hard_light_blue_stained_glass_pane",
            "minecraft:hard_light_gray_stained_glass",
            "minecraft:hard_light_gray_stained_glass_pane",
            "minecraft:hard_lime_stained_glass",
            "minecraft:hard_lime_stained_glass_pane",
            "minecraft:hard_magenta_stained_glass",
            "minecraft:hard_magenta_stained_glass_pane",
            "minecraft:hard_orange_stained_glass",
            "minecraft:hard_orange_stained_glass_pane",
            "minecraft:hard_pink_stained_glass",
            "minecraft:hard_pink_stained_glass_pane",
            "minecraft:hard_purple_stained_glass",
            "minecraft:hard_purple_stained_glass_pane",
            "minecraft:hard_red_stained_glass",
            "minecraft:hard_red_stained_glass_pane",
            "minecraft:hard_white_stained_glass",
            "minecraft:hard_white_stained_glass_pane",
            "minecraft:hard_yellow_stained_glass",
            "minecraft:hard_yellow_stained_glass_pane",
            "minecraft:underwater_torch"
    );

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try {
            register(ACACIA_BUTTON, BlockAcaciaButton.class);
            register(ACACIA_DOOR, BlockAcaciaDoor.class);
            register(ACACIA_DOUBLE_SLAB, BlockAcaciaDoubleSlab.class);
            register(ACACIA_FENCE, BlockAcaciaFence.class);
            register(ACACIA_FENCE_GATE, BlockAcaciaFenceGate.class);
            register(ACACIA_HANGING_SIGN, BlockAcaciaHangingSign.class);
            register(ACACIA_LEAVES, BlockAcaciaLeaves.class);
            register(ACACIA_LOG, BlockAcaciaLog.class);
            register(ACACIA_PLANKS, BlockAcaciaPlanks.class);
            register(ACACIA_PRESSURE_PLATE, BlockAcaciaPressurePlate.class);
            register(ACACIA_SAPLING, BlockAcaciaSapling.class);
            register(ACACIA_SLAB, BlockAcaciaSlab.class);
            register(ACACIA_STAIRS, BlockAcaciaStairs.class);
            register(ACACIA_STANDING_SIGN, BlockAcaciaStandingSign.class);
            register(ACACIA_TRAPDOOR, BlockAcaciaTrapdoor.class);
            register(ACACIA_WALL_SIGN, BlockAcaciaWallSign.class);
            register(ACACIA_WOOD, BlockAcaciaWood.class);
            register(ACTIVATOR_RAIL, BlockActivatorRail.class);
            register(AIR, BlockAir.class);
            register(ALLIUM, BlockAllium.class);
            register(ALLOW, BlockAllow.class);
            register(AMETHYST_BLOCK, BlockAmethystBlock.class);
            register(AMETHYST_CLUSTER, BlockAmethystCluster.class);
            register(ANCIENT_DEBRIS, BlockAncientDebris.class);
            register(ANDESITE, BlockAndesite.class);
            register(ANDESITE_STAIRS, BlockAndesiteStairs.class);
            register(ANVIL, BlockAnvil.class);
            register(AZALEA, BlockAzalea.class);
            register(AZALEA_LEAVES, BlockAzaleaLeaves.class);
            register(AZALEA_LEAVES_FLOWERED, BlockAzaleaLeavesFlowered.class);
            register(AZURE_BLUET, BlockAzureBluet.class);
            register(BAMBOO, BlockBamboo.class);
            register(BAMBOO_BLOCK, BlockBambooBlock.class);
            register(BAMBOO_BUTTON, BlockBambooButton.class);
            register(BAMBOO_DOOR, BlockBambooDoor.class);
            register(BAMBOO_DOUBLE_SLAB, BlockBambooDoubleSlab.class);
            register(BAMBOO_FENCE, BlockBambooFence.class);
            register(BAMBOO_FENCE_GATE, BlockBambooFenceGate.class);
            register(BAMBOO_HANGING_SIGN, BlockBambooHangingSign.class);
            register(BAMBOO_MOSAIC, BlockBambooMosaic.class);
            register(BAMBOO_MOSAIC_DOUBLE_SLAB, BlockBambooMosaicDoubleSlab.class);
            register(BAMBOO_MOSAIC_SLAB, BlockBambooMosaicSlab.class);
            register(BAMBOO_MOSAIC_STAIRS, BlockBambooMosaicStairs.class);
            register(BAMBOO_PLANKS, BlockBambooPlanks.class);
            register(BAMBOO_PRESSURE_PLATE, BlockBambooPressurePlate.class);
            register(BAMBOO_SAPLING, BlockBambooSapling.class);
            register(BAMBOO_SLAB, BlockBambooSlab.class);
            register(BAMBOO_STAIRS, BlockBambooStairs.class);
            register(BAMBOO_STANDING_SIGN, BlockBambooStandingSign.class);
            register(BAMBOO_TRAPDOOR, BlockBambooTrapdoor.class);
            register(BAMBOO_WALL_SIGN, BlockBambooWallSign.class);
            register(BARREL, BlockBarrel.class);
            register(BARRIER, BlockBarrier.class);
            register(BASALT, BlockBasalt.class);
            register(BEACON, BlockBeacon.class);
            register(BED, BlockBed.class);
            register(BEDROCK, BlockBedrock.class);
            register(BEE_NEST, BlockBeeNest.class);
            register(BEEHIVE, BlockBeehive.class);
            register(BEETROOT, BlockBeetroot.class);
            register(BELL, BlockBell.class);
            register(BIG_DRIPLEAF, BlockBigDripleaf.class);
            register(BIRCH_BUTTON, BlockBirchButton.class);
            register(BIRCH_DOOR, BlockBirchDoor.class);
            register(BIRCH_DOUBLE_SLAB, BlockBirchDoubleSlab.class);
            register(BIRCH_FENCE, BlockBirchFence.class);
            register(BIRCH_FENCE_GATE, BlockBirchFenceGate.class);
            register(BIRCH_HANGING_SIGN, BlockBirchHangingSign.class);
            register(BIRCH_LEAVES, BlockBirchLeaves.class);
            register(BIRCH_LOG, BlockBirchLog.class);
            register(BIRCH_PLANKS, BlockBirchPlanks.class);
            register(BIRCH_PRESSURE_PLATE, BlockBirchPressurePlate.class);
            register(BIRCH_SAPLING, BlockBirchSapling.class);
            register(BIRCH_SLAB, BlockBirchSlab.class);
            register(BIRCH_STAIRS, BlockBirchStairs.class);
            register(BIRCH_STANDING_SIGN, BlockBirchStandingSign.class);
            register(BIRCH_TRAPDOOR, BlockBirchTrapdoor.class);
            register(BIRCH_WALL_SIGN, BlockBirchWallSign.class);
            register(BIRCH_WOOD, BlockBirchWood.class);
            register(BLACK_CANDLE, BlockBlackCandle.class);
            register(BLACK_CANDLE_CAKE, BlockBlackCandleCake.class);
            register(BLACK_CARPET, BlockBlackCarpet.class);
            register(BLACK_CONCRETE, BlockBlackConcrete.class);
            register(BLACK_CONCRETE_POWDER, BlockBlackConcretePowder.class);
            register(BLACK_GLAZED_TERRACOTTA, BlockBlackGlazedTerracotta.class);
            register(BLACK_SHULKER_BOX, BlockBlackShulkerBox.class);
            register(BLACK_STAINED_GLASS, BlockBlackStainedGlass.class);
            register(BLACK_STAINED_GLASS_PANE, BlockBlackStainedGlassPane.class);
            register(BLACK_TERRACOTTA, BlockBlackTerracotta.class);
            register(BLACK_WOOL, BlockBlackWool.class);
            register(BLACKSTONE, BlockBlackstone.class);
            register(BLACKSTONE_DOUBLE_SLAB, BlockBlackstoneDoubleSlab.class);
            register(BLACKSTONE_SLAB, BlockBlackstoneSlab.class);
            register(BLACKSTONE_STAIRS, BlockBlackstoneStairs.class);
            register(BLACKSTONE_WALL, BlockBlackstoneWall.class);
            register(BLAST_FURNACE, BlockBlastFurnace.class);
            register(BLUE_CANDLE, BlockBlueCandle.class);
            register(BLUE_CANDLE_CAKE, BlockBlueCandleCake.class);
            register(BLUE_CARPET, BlockBlueCarpet.class);
            register(BLUE_CONCRETE, BlockBlueConcrete.class);
            register(BLUE_CONCRETE_POWDER, BlockBlueConcretePowder.class);
            register(BLUE_GLAZED_TERRACOTTA, BlockBlueGlazedTerracotta.class);
            register(BLUE_ICE, BlockBlueIce.class);
            register(BLUE_ORCHID, BlockBlueOrchid.class);
            register(BLUE_SHULKER_BOX, BlockBlueShulkerBox.class);
            register(BLUE_STAINED_GLASS, BlockBlueStainedGlass.class);
            register(BLUE_STAINED_GLASS_PANE, BlockBlueStainedGlassPane.class);
            register(BLUE_TERRACOTTA, BlockBlueTerracotta.class);
            register(BLUE_WOOL, BlockBlueWool.class);
            register(BONE_BLOCK, BlockBoneBlock.class);
            register(BOOKSHELF, BlockBookshelf.class);
            register(BORDER_BLOCK, BlockBorderBlock.class);
            register(BRAIN_CORAL, BlockBrainCoral.class);
            register(BRAIN_CORAL_BLOCK, BlockBrainCoralBlock.class);
            register(BRAIN_CORAL_FAN, BlockBrainCoralFan.class);
            register(BREWING_STAND, BlockBrewingStand.class);
            register(BRICK_BLOCK, BlockBrickBlock.class);
            register(BRICK_SLAB, BlockBrickSlab.class);
            register(BRICK_STAIRS, BlockBrickStairs.class);
            register(BROWN_CANDLE, BlockBrownCandle.class);
            register(BROWN_CANDLE_CAKE, BlockBrownCandleCake.class);
            register(BROWN_CARPET, BlockBrownCarpet.class);
            register(BROWN_CONCRETE, BlockBrownConcrete.class);
            register(BROWN_CONCRETE_POWDER, BlockBrownConcretePowder.class);
            register(BROWN_GLAZED_TERRACOTTA, BlockBrownGlazedTerracotta.class);
            register(BROWN_MUSHROOM, BlockBrownMushroom.class);
            register(BROWN_MUSHROOM_BLOCK, BlockBrownMushroomBlock.class);
            register(BROWN_SHULKER_BOX, BlockBrownShulkerBox.class);
            register(BROWN_STAINED_GLASS, BlockBrownStainedGlass.class);
            register(BROWN_STAINED_GLASS_PANE, BlockBrownStainedGlassPane.class);
            register(BROWN_TERRACOTTA, BlockBrownTerracotta.class);
            register(BROWN_WOOL, BlockBrownWool.class);
            register(BUBBLE_COLUMN, BlockBubbleColumn.class);
            register(BUBBLE_CORAL, BlockBubbleCoral.class);
            register(BUBBLE_CORAL_BLOCK, BlockBubbleCoralBlock.class);
            register(BUBBLE_CORAL_FAN, BlockBubbleCoralFan.class);
            register(BUDDING_AMETHYST, BlockBuddingAmethyst.class);
            register(CACTUS, BlockCactus.class);
            register(CAKE, BlockCake.class);
            register(CALCITE, BlockCalcite.class);
            register(CALIBRATED_SCULK_SENSOR, BlockCalibratedSculkSensor.class);
            register(CAMPFIRE, BlockCampfire.class);
            register(CANDLE, BlockCandle.class);
            register(CANDLE_CAKE, BlockCandleCake.class);
            register(CARROTS, BlockCarrots.class);
            register(CARTOGRAPHY_TABLE, BlockCartographyTable.class);
            register(CARVED_PUMPKIN, BlockCarvedPumpkin.class);
            register(CAULDRON, BlockCauldron.class);
            register(CAVE_VINES, BlockCaveVines.class);
            register(CAVE_VINES_BODY_WITH_BERRIES, BlockCaveVinesBodyWithBerries.class);
            register(CAVE_VINES_HEAD_WITH_BERRIES, BlockCaveVinesHeadWithBerries.class);
            register(CHAIN, BlockChain.class);
            register(CHAIN_COMMAND_BLOCK, BlockChainCommandBlock.class);
            register(CHERRY_BUTTON, BlockCherryButton.class);
            register(CHERRY_DOOR, BlockCherryDoor.class);
            register(CHERRY_DOUBLE_SLAB, BlockCherryDoubleSlab.class);
            register(CHERRY_FENCE, BlockCherryFence.class);
            register(CHERRY_FENCE_GATE, BlockCherryFenceGate.class);
            register(CHERRY_HANGING_SIGN, BlockCherryHangingSign.class);
            register(CHERRY_LEAVES, BlockCherryLeaves.class);
            register(CHERRY_LOG, BlockCherryLog.class);
            register(CHERRY_PLANKS, BlockCherryPlanks.class);
            register(CHERRY_PRESSURE_PLATE, BlockCherryPressurePlate.class);
            register(CHERRY_SAPLING, BlockCherrySapling.class);
            register(CHERRY_SLAB, BlockCherrySlab.class);
            register(CHERRY_STAIRS, BlockCherryStairs.class);
            register(CHERRY_STANDING_SIGN, BlockCherryStandingSign.class);
            register(CHERRY_TRAPDOOR, BlockCherryTrapdoor.class);
            register(CHERRY_WALL_SIGN, BlockCherryWallSign.class);
            register(CHERRY_WOOD, BlockCherryWood.class);
            register(CHEST, BlockChest.class);
            register(CHISELED_BOOKSHELF, BlockChiseledBookshelf.class);
            register(CHISELED_COPPER, BlockChiseledCopper.class);
            register(CHISELED_DEEPSLATE, BlockChiseledDeepslate.class);
            register(CHISELED_NETHER_BRICKS, BlockChiseledNetherBricks.class);
            register(CHISELED_POLISHED_BLACKSTONE, BlockChiseledPolishedBlackstone.class);
            register(CHISELED_TUFF, BlockChiseledTuff.class);
            register(CHISELED_TUFF_BRICKS, BlockChiseledTuffBricks.class);
            register(CHORUS_FLOWER, BlockChorusFlower.class);
            register(CHORUS_PLANT, BlockChorusPlant.class);
            register(CLAY, BlockClay.class);
            register(CLIENT_REQUEST_PLACEHOLDER_BLOCK, BlockClientRequestPlaceholderBlock.class);
            register(COAL_BLOCK, BlockCoalBlock.class);
            register(COAL_ORE, BlockCoalOre.class);
            register(COBBLED_DEEPSLATE, BlockCobbledDeepslate.class);
            register(COBBLED_DEEPSLATE_DOUBLE_SLAB, BlockCobbledDeepslateDoubleSlab.class);
            register(COBBLED_DEEPSLATE_SLAB, BlockCobbledDeepslateSlab.class);
            register(COBBLED_DEEPSLATE_STAIRS, BlockCobbledDeepslateStairs.class);
            register(COBBLED_DEEPSLATE_WALL, BlockCobbledDeepslateWall.class);
            register(COBBLESTONE, BlockCobblestone.class);
            register(COBBLESTONE_SLAB, BlockCobblestoneSlab.class);
            register(COBBLESTONE_WALL, BlockCobblestoneWall.class);
            register(COCOA, BlockCocoa.class);
            register(COMMAND_BLOCK, BlockCommandBlock.class);
            register(COMPOSTER, BlockComposter.class);
            register(CONDUIT, BlockConduit.class);
            register(COPPER_BLOCK, BlockCopperBlock.class);
            register(COPPER_BULB, BlockCopperBulb.class);
            register(COPPER_DOOR, BlockCopperDoor.class);
            register(COPPER_GRATE, BlockCopperGrate.class);
            register(COPPER_ORE, BlockCopperOre.class);
            register(COPPER_TRAPDOOR, BlockCopperTrapdoor.class);
            register(CORAL_FAN_HANG, BlockCoralFanHang.class);
            register(CORAL_FAN_HANG2, BlockCoralFanHang2.class);
            register(CORAL_FAN_HANG3, BlockCoralFanHang3.class);
            register(CORNFLOWER, BlockCornflower.class);
            register(CRACKED_DEEPSLATE_BRICKS, BlockCrackedDeepslateBricks.class);
            register(CRACKED_DEEPSLATE_TILES, BlockCrackedDeepslateTiles.class);
            register(CRACKED_NETHER_BRICKS, BlockCrackedNetherBricks.class);
            register(CRACKED_POLISHED_BLACKSTONE_BRICKS, BlockCrackedPolishedBlackstoneBricks.class);
            register(CRAFTER, BlockCrafter.class);
            register(CRAFTING_TABLE, BlockCraftingTable.class);
            register(CRIMSON_BUTTON, BlockCrimsonButton.class);
            register(CRIMSON_DOOR, BlockCrimsonDoor.class);
            register(CRIMSON_DOUBLE_SLAB, BlockCrimsonDoubleSlab.class);
            register(CRIMSON_FENCE, BlockCrimsonFence.class);
            register(CRIMSON_FENCE_GATE, BlockCrimsonFenceGate.class);
            register(CRIMSON_FUNGUS, BlockCrimsonFungus.class);
            register(CRIMSON_HANGING_SIGN, BlockCrimsonHangingSign.class);
            register(CRIMSON_HYPHAE, BlockCrimsonHyphae.class);
            register(CRIMSON_NYLIUM, BlockCrimsonNylium.class);
            register(CRIMSON_PLANKS, BlockCrimsonPlanks.class);
            register(CRIMSON_PRESSURE_PLATE, BlockCrimsonPressurePlate.class);
            register(CRIMSON_ROOTS, BlockCrimsonRoots.class);
            register(CRIMSON_SLAB, BlockCrimsonSlab.class);
            register(CRIMSON_STAIRS, BlockCrimsonStairs.class);
            register(CRIMSON_STANDING_SIGN, BlockCrimsonStandingSign.class);
            register(CRIMSON_STEM, BlockCrimsonStem.class);
            register(CRIMSON_TRAPDOOR, BlockCrimsonTrapdoor.class);
            register(CRIMSON_WALL_SIGN, BlockCrimsonWallSign.class);
            register(CRYING_OBSIDIAN, BlockCryingObsidian.class);
            register(CUT_COPPER, BlockCutCopper.class);
            register(CUT_COPPER_SLAB, BlockCutCopperSlab.class);
            register(CUT_COPPER_STAIRS, BlockCutCopperStairs.class);
            register(CYAN_CANDLE, BlockCyanCandle.class);
            register(CYAN_CANDLE_CAKE, BlockCyanCandleCake.class);
            register(CYAN_CARPET, BlockCyanCarpet.class);
            register(CYAN_CONCRETE, BlockCyanConcrete.class);
            register(CYAN_CONCRETE_POWDER, BlockCyanConcretePowder.class);
            register(CYAN_GLAZED_TERRACOTTA, BlockCyanGlazedTerracotta.class);
            register(CYAN_SHULKER_BOX, BlockCyanShulkerBox.class);
            register(CYAN_STAINED_GLASS, BlockCyanStainedGlass.class);
            register(CYAN_STAINED_GLASS_PANE, BlockCyanStainedGlassPane.class);
            register(CYAN_TERRACOTTA, BlockCyanTerracotta.class);
            register(CYAN_WOOL, BlockCyanWool.class);
            register(DARK_OAK_BUTTON, BlockDarkOakButton.class);
            register(DARK_OAK_DOOR, BlockDarkOakDoor.class);
            register(DARK_OAK_DOUBLE_SLAB, BlockDarkOakDoubleSlab.class);
            register(DARK_OAK_FENCE, BlockDarkOakFence.class);
            register(DARK_OAK_FENCE_GATE, BlockDarkOakFenceGate.class);
            register(DARK_OAK_HANGING_SIGN, BlockDarkOakHangingSign.class);
            register(DARK_OAK_LEAVES, BlockDarkOakLeaves.class);
            register(DARK_OAK_LOG, BlockDarkOakLog.class);
            register(DARK_OAK_PLANKS, BlockDarkOakPlanks.class);
            register(DARK_OAK_PRESSURE_PLATE, BlockDarkOakPressurePlate.class);
            register(DARK_OAK_SAPLING, BlockDarkOakSapling.class);
            register(DARK_OAK_SLAB, BlockDarkOakSlab.class);
            register(DARK_OAK_STAIRS, BlockDarkOakStairs.class);
            register(DARK_OAK_TRAPDOOR, BlockDarkOakTrapdoor.class);
            register(DARK_OAK_WOOD, BlockDarkOakWood.class);
            register(DARK_PRISMARINE_STAIRS, BlockDarkPrismarineStairs.class);
            register(DARKOAK_STANDING_SIGN, BlockDarkoakStandingSign.class);
            register(DARKOAK_WALL_SIGN, BlockDarkoakWallSign.class);
            register(DAYLIGHT_DETECTOR, BlockDaylightDetector.class);
            register(DAYLIGHT_DETECTOR_INVERTED, BlockDaylightDetectorInverted.class);
            register(DEAD_BRAIN_CORAL, BlockDeadBrainCoral.class);
            register(DEAD_BRAIN_CORAL_BLOCK, BlockDeadBrainCoralBlock.class);
            register(DEAD_BRAIN_CORAL_FAN, BlockDeadBrainCoralFan.class);
            register(DEAD_BUBBLE_CORAL, BlockDeadBubbleCoral.class);
            register(DEAD_BUBBLE_CORAL_BLOCK, BlockDeadBubbleCoralBlock.class);
            register(DEAD_BUBBLE_CORAL_FAN, BlockDeadBubbleCoralFan.class);
            register(DEAD_FIRE_CORAL, BlockDeadFireCoral.class);
            register(DEAD_FIRE_CORAL_BLOCK, BlockDeadFireCoralBlock.class);
            register(DEAD_FIRE_CORAL_FAN, BlockDeadFireCoralFan.class);
            register(DEAD_HORN_CORAL, BlockDeadHornCoral.class);
            register(DEAD_HORN_CORAL_BLOCK, BlockDeadHornCoralBlock.class);
            register(DEAD_HORN_CORAL_FAN, BlockDeadHornCoralFan.class);
            register(DEAD_TUBE_CORAL, BlockDeadTubeCoral.class);
            register(DEAD_TUBE_CORAL_BLOCK, BlockDeadTubeCoralBlock.class);
            register(DEAD_TUBE_CORAL_FAN, BlockDeadTubeCoralFan.class);
            register(DEADBUSH, BlockDeadbush.class);
            register(DECORATED_POT, BlockDecoratedPot.class);
            register(DEEPSLATE, BlockDeepslate.class);
            register(DEEPSLATE_BRICK_DOUBLE_SLAB, BlockDeepslateBrickDoubleSlab.class);
            register(DEEPSLATE_BRICK_SLAB, BlockDeepslateBrickSlab.class);
            register(DEEPSLATE_BRICK_STAIRS, BlockDeepslateBrickStairs.class);
            register(DEEPSLATE_BRICK_WALL, BlockDeepslateBrickWall.class);
            register(DEEPSLATE_BRICKS, BlockDeepslateBricks.class);
            register(DEEPSLATE_COAL_ORE, BlockDeepslateCoalOre.class);
            register(DEEPSLATE_COPPER_ORE, BlockDeepslateCopperOre.class);
            register(DEEPSLATE_DIAMOND_ORE, BlockDeepslateDiamondOre.class);
            register(DEEPSLATE_EMERALD_ORE, BlockDeepslateEmeraldOre.class);
            register(DEEPSLATE_GOLD_ORE, BlockDeepslateGoldOre.class);
            register(DEEPSLATE_IRON_ORE, BlockDeepslateIronOre.class);
            register(DEEPSLATE_LAPIS_ORE, BlockDeepslateLapisOre.class);
            register(DEEPSLATE_REDSTONE_ORE, BlockDeepslateRedstoneOre.class);
            register(DEEPSLATE_TILE_DOUBLE_SLAB, BlockDeepslateTileDoubleSlab.class);
            register(DEEPSLATE_TILE_SLAB, BlockDeepslateTileSlab.class);
            register(DEEPSLATE_TILE_STAIRS, BlockDeepslateTileStairs.class);
            register(DEEPSLATE_TILE_WALL, BlockDeepslateTileWall.class);
            register(DEEPSLATE_TILES, BlockDeepslateTiles.class);
            register(DENY, BlockDeny.class);
            register(DETECTOR_RAIL, BlockDetectorRail.class);
            register(DIAMOND_BLOCK, BlockDiamondBlock.class);
            register(DIAMOND_ORE, BlockDiamondOre.class);
            register(DIORITE, BlockDiorite.class);
            register(DIORITE_STAIRS, BlockDioriteStairs.class);
            register(DIRT, BlockDirt.class);
            register(DIRT_WITH_ROOTS, BlockDirtWithRoots.class);
            register(DISPENSER, BlockDispenser.class);
            register(DOUBLE_CUT_COPPER_SLAB, BlockDoubleCutCopperSlab.class);
            register(DOUBLE_STONE_BLOCK_SLAB, BlockDoubleStoneBlockSlab.class);
            register(DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleStoneBlockSlab2.class);
            register(DOUBLE_STONE_BLOCK_SLAB3, BlockDoubleStoneBlockSlab3.class);
            register(DOUBLE_STONE_BLOCK_SLAB4, BlockDoubleStoneBlockSlab4.class);
            register(DRAGON_EGG, BlockDragonEgg.class);
            register(DRIED_KELP_BLOCK, BlockDriedKelpBlock.class);
            register(DRIPSTONE_BLOCK, BlockDripstoneBlock.class);
            register(DROPPER, BlockDropper.class);
            register(EMERALD_BLOCK, BlockEmeraldBlock.class);
            register(EMERALD_ORE, BlockEmeraldOre.class);
            register(ENCHANTING_TABLE, BlockEnchantingTable.class);
            register(END_BRICK_STAIRS, BlockEndBrickStairs.class);
            register(END_BRICKS, BlockEndBricks.class);
            register(END_GATEWAY, BlockEndGateway.class);
            register(END_PORTAL, BlockEndPortal.class);
            register(END_PORTAL_FRAME, BlockEndPortalFrame.class);
            register(END_ROD, BlockEndRod.class);
            register(END_STONE, BlockEndStone.class);
            register(ENDER_CHEST, BlockEnderChest.class);
            register(EXPOSED_CHISELED_COPPER, BlockExposedChiseledCopper.class);
            register(EXPOSED_COPPER, BlockExposedCopper.class);
            register(EXPOSED_COPPER_BULB, BlockExposedCopperBulb.class);
            register(EXPOSED_COPPER_DOOR, BlockExposedCopperDoor.class);
            register(EXPOSED_COPPER_GRATE, BlockExposedCopperGrate.class);
            register(EXPOSED_COPPER_TRAPDOOR, BlockExposedCopperTrapdoor.class);
            register(EXPOSED_CUT_COPPER, BlockExposedCutCopper.class);
            register(EXPOSED_CUT_COPPER_SLAB, BlockExposedCutCopperSlab.class);
            register(EXPOSED_CUT_COPPER_STAIRS, BlockExposedCutCopperStairs.class);
            register(EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockExposedDoubleCutCopperSlab.class);
            register(FARMLAND, BlockFarmland.class);
            register(FENCE_GATE, BlockFenceGate.class);
            register(FERN, BlockFern.class);
            register(FIRE, BlockFire.class);
            register(FIRE_CORAL, BlockFireCoral.class);
            register(FIRE_CORAL_BLOCK, BlockFireCoralBlock.class);
            register(FIRE_CORAL_FAN, BlockFireCoralFan.class);
            register(FLETCHING_TABLE, BlockFletchingTable.class);
            register(FLOWER_POT, BlockFlowerPot.class);
            register(FLOWERING_AZALEA, BlockFloweringAzalea.class);
            register(FLOWING_LAVA, BlockFlowingLava.class);
            register(FLOWING_WATER, BlockFlowingWater.class);
            register(FRAME, BlockFrame.class);
            register(FROG_SPAWN, BlockFrogSpawn.class);
            register(FROSTED_ICE, BlockFrostedIce.class);
            register(FURNACE, BlockFurnace.class);
            register(GILDED_BLACKSTONE, BlockGildedBlackstone.class);
            register(GLASS, BlockGlass.class);
            register(GLASS_PANE, BlockGlassPane.class);
            register(GLOW_FRAME, BlockGlowFrame.class);
            register(GLOW_LICHEN, BlockGlowLichen.class);
            register(GLOWINGOBSIDIAN, BlockGlowingobsidian.class);
            register(GLOWSTONE, BlockGlowstone.class);
            register(GOLD_BLOCK, BlockGoldBlock.class);
            register(GOLD_ORE, BlockGoldOre.class);
            register(GOLDEN_RAIL, BlockGoldenRail.class);
            register(GRANITE, BlockGranite.class);
            register(GRANITE_STAIRS, BlockGraniteStairs.class);
            register(GRASS_BLOCK, BlockGrassBlock.class);
            register(GRASS_PATH, BlockGrassPath.class);
            register(GRAVEL, BlockGravel.class);
            register(GRAY_CANDLE, BlockGrayCandle.class);
            register(GRAY_CANDLE_CAKE, BlockGrayCandleCake.class);
            register(GRAY_CARPET, BlockGrayCarpet.class);
            register(GRAY_CONCRETE, BlockGrayConcrete.class);
            register(GRAY_CONCRETE_POWDER, BlockGrayConcretePowder.class);
            register(GRAY_GLAZED_TERRACOTTA, BlockGrayGlazedTerracotta.class);
            register(GRAY_SHULKER_BOX, BlockGrayShulkerBox.class);
            register(GRAY_STAINED_GLASS, BlockGrayStainedGlass.class);
            register(GRAY_STAINED_GLASS_PANE, BlockGrayStainedGlassPane.class);
            register(GRAY_TERRACOTTA, BlockGrayTerracotta.class);
            register(GRAY_WOOL, BlockGrayWool.class);
            register(GREEN_CANDLE, BlockGreenCandle.class);
            register(GREEN_CANDLE_CAKE, BlockGreenCandleCake.class);
            register(GREEN_CARPET, BlockGreenCarpet.class);
            register(GREEN_CONCRETE, BlockGreenConcrete.class);
            register(GREEN_CONCRETE_POWDER, BlockGreenConcretePowder.class);
            register(GREEN_GLAZED_TERRACOTTA, BlockGreenGlazedTerracotta.class);
            register(GREEN_SHULKER_BOX, BlockGreenShulkerBox.class);
            register(GREEN_STAINED_GLASS, BlockGreenStainedGlass.class);
            register(GREEN_STAINED_GLASS_PANE, BlockGreenStainedGlassPane.class);
            register(GREEN_TERRACOTTA, BlockGreenTerracotta.class);
            register(GREEN_WOOL, BlockGreenWool.class);
            register(GRINDSTONE, BlockGrindstone.class);
            register(HANGING_ROOTS, BlockHangingRoots.class);
            register(HARDENED_CLAY, BlockHardenedClay.class);
            register(HAY_BLOCK, BlockHayBlock.class);
            register(HEAVY_CORE, BlockHeavyCore.class);
            register(HEAVY_WEIGHTED_PRESSURE_PLATE, BlockHeavyWeightedPressurePlate.class);
            register(HONEY_BLOCK, BlockHoneyBlock.class);
            register(HONEYCOMB_BLOCK, BlockHoneycombBlock.class);
            register(HOPPER, BlockHopper.class);
            register(HORN_CORAL, BlockHornCoral.class);
            register(HORN_CORAL_BLOCK, BlockHornCoralBlock.class);
            register(HORN_CORAL_FAN, BlockHornCoralFan.class);
            register(ICE, BlockIce.class);
            register(INFESTED_DEEPSLATE, BlockInfestedDeepslate.class);
            register(INFO_UPDATE, BlockInfoUpdate.class);
            register(INFO_UPDATE2, BlockInfoUpdate2.class);
            register(INVISIBLE_BEDROCK, BlockInvisibleBedrock.class);
            register(IRON_BARS, BlockIronBars.class);
            register(IRON_BLOCK, BlockIronBlock.class);
            register(IRON_DOOR, BlockIronDoor.class);
            register(IRON_ORE, BlockIronOre.class);
            register(IRON_TRAPDOOR, BlockIronTrapdoor.class);
            register(JIGSAW, BlockJigsaw.class);
            register(JUKEBOX, BlockJukebox.class);
            register(JUNGLE_BUTTON, BlockJungleButton.class);
            register(JUNGLE_DOOR, BlockJungleDoor.class);
            register(JUNGLE_DOUBLE_SLAB, BlockJungleDoubleSlab.class);
            register(JUNGLE_FENCE, BlockJungleFence.class);
            register(JUNGLE_FENCE_GATE, BlockJungleFenceGate.class);
            register(JUNGLE_HANGING_SIGN, BlockJungleHangingSign.class);
            register(JUNGLE_LEAVES, BlockJungleLeaves.class);
            register(JUNGLE_LOG, BlockJungleLog.class);
            register(JUNGLE_PLANKS, BlockJunglePlanks.class);
            register(JUNGLE_PRESSURE_PLATE, BlockJunglePressurePlate.class);
            register(JUNGLE_SAPLING, BlockJungleSapling.class);
            register(JUNGLE_SLAB, BlockJungleSlab.class);
            register(JUNGLE_STAIRS, BlockJungleStairs.class);
            register(JUNGLE_STANDING_SIGN, BlockJungleStandingSign.class);
            register(JUNGLE_TRAPDOOR, BlockJungleTrapdoor.class);
            register(JUNGLE_WALL_SIGN, BlockJungleWallSign.class);
            register(JUNGLE_WOOD, BlockJungleWood.class);
            register(KELP, BlockKelp.class);
            register(LADDER, BlockLadder.class);
            register(LANTERN, BlockLantern.class);
            register(LAPIS_BLOCK, BlockLapisBlock.class);
            register(LAPIS_ORE, BlockLapisOre.class);
            register(LARGE_AMETHYST_BUD, BlockLargeAmethystBud.class);
            register(LARGE_FERN, BlockLargeFern.class);
            register(LAVA, BlockLava.class);
            register(LECTERN, BlockLectern.class);
            register(LEVER, BlockLever.class);
            register(LIGHT_BLOCK, BlockLightBlock.class);
            register(LIGHT_BLUE_CANDLE, BlockLightBlueCandle.class);
            register(LIGHT_BLUE_CANDLE_CAKE, BlockLightBlueCandleCake.class);
            register(LIGHT_BLUE_CARPET, BlockLightBlueCarpet.class);
            register(LIGHT_BLUE_CONCRETE, BlockLightBlueConcrete.class);
            register(LIGHT_BLUE_CONCRETE_POWDER, BlockLightBlueConcretePowder.class);
            register(LIGHT_BLUE_GLAZED_TERRACOTTA, BlockLightBlueGlazedTerracotta.class);
            register(LIGHT_BLUE_SHULKER_BOX, BlockLightBlueShulkerBox.class);
            register(LIGHT_BLUE_STAINED_GLASS, BlockLightBlueStainedGlass.class);
            register(LIGHT_BLUE_STAINED_GLASS_PANE, BlockLightBlueStainedGlassPane.class);
            register(LIGHT_BLUE_TERRACOTTA, BlockLightBlueTerracotta.class);
            register(LIGHT_BLUE_WOOL, BlockLightBlueWool.class);
            register(LIGHT_GRAY_CANDLE, BlockLightGrayCandle.class);
            register(LIGHT_GRAY_CANDLE_CAKE, BlockLightGrayCandleCake.class);
            register(LIGHT_GRAY_CARPET, BlockLightGrayCarpet.class);
            register(LIGHT_GRAY_CONCRETE, BlockLightGrayConcrete.class);
            register(LIGHT_GRAY_CONCRETE_POWDER, BlockLightGrayConcretePowder.class);
            register(LIGHT_GRAY_SHULKER_BOX, BlockLightGrayShulkerBox.class);
            register(LIGHT_GRAY_STAINED_GLASS, BlockLightGrayStainedGlass.class);
            register(LIGHT_GRAY_STAINED_GLASS_PANE, BlockLightGrayStainedGlassPane.class);
            register(LIGHT_GRAY_TERRACOTTA, BlockLightGrayTerracotta.class);
            register(LIGHT_GRAY_WOOL, BlockLightGrayWool.class);
            register(LIGHT_WEIGHTED_PRESSURE_PLATE, BlockLightWeightedPressurePlate.class);
            register(LIGHTNING_ROD, BlockLightningRod.class);
            register(LILAC, BlockLilac.class);
            register(LILY_OF_THE_VALLEY, BlockLilyOfTheValley.class);
            register(LIME_CANDLE, BlockLimeCandle.class);
            register(LIME_CANDLE_CAKE, BlockLimeCandleCake.class);
            register(LIME_CARPET, BlockLimeCarpet.class);
            register(LIME_CONCRETE, BlockLimeConcrete.class);
            register(LIME_CONCRETE_POWDER, BlockLimeConcretePowder.class);
            register(LIME_GLAZED_TERRACOTTA, BlockLimeGlazedTerracotta.class);
            register(LIME_SHULKER_BOX, BlockLimeShulkerBox.class);
            register(LIME_STAINED_GLASS, BlockLimeStainedGlass.class);
            register(LIME_STAINED_GLASS_PANE, BlockLimeStainedGlassPane.class);
            register(LIME_TERRACOTTA, BlockLimeTerracotta.class);
            register(LIME_WOOL, BlockLimeWool.class);
            register(LIT_BLAST_FURNACE, BlockLitBlastFurnace.class);
            register(LIT_DEEPSLATE_REDSTONE_ORE, BlockLitDeepslateRedstoneOre.class);
            register(LIT_FURNACE, BlockLitFurnace.class);
            register(LIT_PUMPKIN, BlockLitPumpkin.class);
            register(LIT_REDSTONE_LAMP, BlockLitRedstoneLamp.class);
            register(LIT_REDSTONE_ORE, BlockLitRedstoneOre.class);
            register(LIT_SMOKER, BlockLitSmoker.class);
            register(LODESTONE, BlockLodestone.class);
            register(LOOM, BlockLoom.class);
            register(MAGENTA_CANDLE, BlockMagentaCandle.class);
            register(MAGENTA_CANDLE_CAKE, BlockMagentaCandleCake.class);
            register(MAGENTA_CARPET, BlockMagentaCarpet.class);
            register(MAGENTA_CONCRETE, BlockMagentaConcrete.class);
            register(MAGENTA_CONCRETE_POWDER, BlockMagentaConcretePowder.class);
            register(MAGENTA_GLAZED_TERRACOTTA, BlockMagentaGlazedTerracotta.class);
            register(MAGENTA_SHULKER_BOX, BlockMagentaShulkerBox.class);
            register(MAGENTA_STAINED_GLASS, BlockMagentaStainedGlass.class);
            register(MAGENTA_STAINED_GLASS_PANE, BlockMagentaStainedGlassPane.class);
            register(MAGENTA_TERRACOTTA, BlockMagentaTerracotta.class);
            register(MAGENTA_WOOL, BlockMagentaWool.class);
            register(MAGMA, BlockMagma.class);
            register(MANGROVE_BUTTON, BlockMangroveButton.class);
            register(MANGROVE_DOOR, BlockMangroveDoor.class);
            register(MANGROVE_DOUBLE_SLAB, BlockMangroveDoubleSlab.class);
            register(MANGROVE_FENCE, BlockMangroveFence.class);
            register(MANGROVE_FENCE_GATE, BlockMangroveFenceGate.class);
            register(MANGROVE_HANGING_SIGN, BlockMangroveHangingSign.class);
            register(MANGROVE_LEAVES, BlockMangroveLeaves.class);
            register(MANGROVE_LOG, BlockMangroveLog.class);
            register(MANGROVE_PLANKS, BlockMangrovePlanks.class);
            register(MANGROVE_PRESSURE_PLATE, BlockMangrovePressurePlate.class);
            register(MANGROVE_PROPAGULE, BlockMangrovePropagule.class);
            register(MANGROVE_ROOTS, BlockMangroveRoots.class);
            register(MANGROVE_SLAB, BlockMangroveSlab.class);
            register(MANGROVE_STAIRS, BlockMangroveStairs.class);
            register(MANGROVE_STANDING_SIGN, BlockMangroveStandingSign.class);
            register(MANGROVE_TRAPDOOR, BlockMangroveTrapdoor.class);
            register(MANGROVE_WALL_SIGN, BlockMangroveWallSign.class);
            register(MANGROVE_WOOD, BlockMangroveWood.class);
            register(MEDIUM_AMETHYST_BUD, BlockMediumAmethystBud.class);
            register(MELON_BLOCK, BlockMelonBlock.class);
            register(MELON_STEM, BlockMelonStem.class);
            register(MOB_SPAWNER, BlockMobSpawner.class);
            register(MONSTER_EGG, BlockMonsterEgg.class);
            register(MOSS_BLOCK, BlockMossBlock.class);
            register(MOSS_CARPET, BlockMossCarpet.class);
            register(MOSSY_COBBLESTONE, BlockMossyCobblestone.class);
            register(MOSSY_COBBLESTONE_STAIRS, BlockMossyCobblestoneStairs.class);
            register(MOSSY_STONE_BRICK_STAIRS, BlockMossyStoneBrickStairs.class);
            register(MOVING_BLOCK, BlockMovingBlock.class);
            register(MUD, BlockMud.class);
            register(MUD_BRICK_DOUBLE_SLAB, BlockMudBrickDoubleSlab.class);
            register(MUD_BRICK_SLAB, BlockMudBrickSlab.class);
            register(MUD_BRICK_STAIRS, BlockMudBrickStairs.class);
            register(MUD_BRICK_WALL, BlockMudBrickWall.class);
            register(MUD_BRICKS, BlockMudBricks.class);
            register(MUDDY_MANGROVE_ROOTS, BlockMuddyMangroveRoots.class);
            register(MYCELIUM, BlockMycelium.class);
            register(NETHER_BRICK, BlockNetherBrick.class);
            register(NETHER_BRICK_FENCE, BlockNetherBrickFence.class);
            register(NETHER_BRICK_SLAB, BlockNetherBrickSlab.class);
            register(NETHER_BRICK_STAIRS, BlockNetherBrickStairs.class);
            register(NETHER_GOLD_ORE, BlockNetherGoldOre.class);
            register(NETHER_SPROUTS, BlockNetherSprouts.class);
            register(NETHER_WART, BlockNetherWart.class);
            register(NETHER_WART_BLOCK, BlockNetherWartBlock.class);
            register(NETHERITE_BLOCK, BlockNetheriteBlock.class);
            register(NETHERRACK, BlockNetherrack.class);
            register(NETHERREACTOR, BlockNetherreactor.class);
            register(NORMAL_STONE_STAIRS, BlockNormalStoneStairs.class);
            register(NOTEBLOCK, BlockNoteblock.class);
            register(OAK_DOUBLE_SLAB, BlockOakDoubleSlab.class);
            register(OAK_FENCE, BlockOakFence.class);
            register(OAK_HANGING_SIGN, BlockOakHangingSign.class);
            register(OAK_LEAVES, BlockOakLeaves.class);
            register(OAK_LOG, BlockOakLog.class);
            register(OAK_PLANKS, BlockOakPlanks.class);
            register(OAK_SAPLING, BlockOakSapling.class);
            register(OAK_SLAB, BlockOakSlab.class);
            register(OAK_STAIRS, BlockOakStairs.class);
            register(OAK_WOOD, BlockOakWood.class);
            register(OBSERVER, BlockObserver.class);
            register(OBSIDIAN, BlockObsidian.class);
            register(OCHRE_FROGLIGHT, BlockOchreFroglight.class);
            register(ORANGE_CANDLE, BlockOrangeCandle.class);
            register(ORANGE_CANDLE_CAKE, BlockOrangeCandleCake.class);
            register(ORANGE_CARPET, BlockOrangeCarpet.class);
            register(ORANGE_CONCRETE, BlockOrangeConcrete.class);
            register(ORANGE_CONCRETE_POWDER, BlockOrangeConcretePowder.class);
            register(ORANGE_GLAZED_TERRACOTTA, BlockOrangeGlazedTerracotta.class);
            register(ORANGE_SHULKER_BOX, BlockOrangeShulkerBox.class);
            register(ORANGE_STAINED_GLASS, BlockOrangeStainedGlass.class);
            register(ORANGE_STAINED_GLASS_PANE, BlockOrangeStainedGlassPane.class);
            register(ORANGE_TERRACOTTA, BlockOrangeTerracotta.class);
            register(ORANGE_TULIP, BlockOrangeTulip.class);
            register(ORANGE_WOOL, BlockOrangeWool.class);
            register(OXEYE_DAISY, BlockOxeyeDaisy.class);
            register(OXIDIZED_CHISELED_COPPER, BlockOxidizedChiseledCopper.class);
            register(OXIDIZED_COPPER, BlockOxidizedCopper.class);
            register(OXIDIZED_COPPER_BULB, BlockOxidizedCopperBulb.class);
            register(OXIDIZED_COPPER_DOOR, BlockOxidizedCopperDoor.class);
            register(OXIDIZED_COPPER_GRATE, BlockOxidizedCopperGrate.class);
            register(OXIDIZED_COPPER_TRAPDOOR, BlockOxidizedCopperTrapdoor.class);
            register(OXIDIZED_CUT_COPPER, BlockOxidizedCutCopper.class);
            register(OXIDIZED_CUT_COPPER_SLAB, BlockOxidizedCutCopperSlab.class);
            register(OXIDIZED_CUT_COPPER_STAIRS, BlockOxidizedCutCopperStairs.class);
            register(OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockOxidizedDoubleCutCopperSlab.class);
            register(PACKED_ICE, BlockPackedIce.class);
            register(PACKED_MUD, BlockPackedMud.class);
            register(PEARLESCENT_FROGLIGHT, BlockPearlescentFroglight.class);
            register(PEONY, BlockPeony.class);
            register(PETRIFIED_OAK_SLAB, BlockPetrifiedOakSlab.class);
            register(PINK_CANDLE, BlockPinkCandle.class);
            register(PINK_CANDLE_CAKE, BlockPinkCandleCake.class);
            register(PINK_CARPET, BlockPinkCarpet.class);
            register(PINK_CONCRETE, BlockPinkConcrete.class);
            register(PINK_CONCRETE_POWDER, BlockPinkConcretePowder.class);
            register(PINK_GLAZED_TERRACOTTA, BlockPinkGlazedTerracotta.class);
            register(PINK_PETALS, BlockPinkPetals.class);
            register(PINK_SHULKER_BOX, BlockPinkShulkerBox.class);
            register(PINK_STAINED_GLASS, BlockPinkStainedGlass.class);
            register(PINK_STAINED_GLASS_PANE, BlockPinkStainedGlassPane.class);
            register(PINK_TERRACOTTA, BlockPinkTerracotta.class);
            register(PINK_TULIP, BlockPinkTulip.class);
            register(PINK_WOOL, BlockPinkWool.class);
            register(PISTON, BlockPiston.class);
            register(PISTON_ARM_COLLISION, BlockPistonArmCollision.class);
            register(PITCHER_CROP, BlockPitcherCrop.class);
            register(PITCHER_PLANT, BlockPitcherPlant.class);
            register(PODZOL, BlockPodzol.class);
            register(POINTED_DRIPSTONE, BlockPointedDripstone.class);
            register(POLISHED_ANDESITE, BlockPolishedAndesite.class);
            register(POLISHED_ANDESITE_STAIRS, BlockPolishedAndesiteStairs.class);
            register(POLISHED_BASALT, BlockPolishedBasalt.class);
            register(POLISHED_BLACKSTONE, BlockPolishedBlackstone.class);
            register(POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, BlockPolishedBlackstoneBrickDoubleSlab.class);
            register(POLISHED_BLACKSTONE_BRICK_SLAB, BlockPolishedBlackstoneBrickSlab.class);
            register(POLISHED_BLACKSTONE_BRICK_STAIRS, BlockPolishedBlackstoneBrickStairs.class);
            register(POLISHED_BLACKSTONE_BRICK_WALL, BlockPolishedBlackstoneBrickWall.class);
            register(POLISHED_BLACKSTONE_BRICKS, BlockPolishedBlackstoneBricks.class);
            register(POLISHED_BLACKSTONE_BUTTON, BlockPolishedBlackstoneButton.class);
            register(POLISHED_BLACKSTONE_DOUBLE_SLAB, BlockPolishedBlackstoneDoubleSlab.class);
            register(POLISHED_BLACKSTONE_PRESSURE_PLATE, BlockPolishedBlackstonePressurePlate.class);
            register(POLISHED_BLACKSTONE_SLAB, BlockPolishedBlackstoneSlab.class);
            register(POLISHED_BLACKSTONE_STAIRS, BlockPolishedBlackstoneStairs.class);
            register(POLISHED_BLACKSTONE_WALL, BlockPolishedBlackstoneWall.class);
            register(POLISHED_DEEPSLATE, BlockPolishedDeepslate.class);
            register(POLISHED_DEEPSLATE_DOUBLE_SLAB, BlockPolishedDeepslateDoubleSlab.class);
            register(POLISHED_DEEPSLATE_SLAB, BlockPolishedDeepslateSlab.class);
            register(POLISHED_DEEPSLATE_STAIRS, BlockPolishedDeepslateStairs.class);
            register(POLISHED_DEEPSLATE_WALL, BlockPolishedDeepslateWall.class);
            register(POLISHED_DIORITE, BlockPolishedDiorite.class);
            register(POLISHED_DIORITE_STAIRS, BlockPolishedDioriteStairs.class);
            register(POLISHED_GRANITE, BlockPolishedGranite.class);
            register(POLISHED_GRANITE_STAIRS, BlockPolishedGraniteStairs.class);
            register(POLISHED_TUFF, BlockPolishedTuff.class);
            register(POLISHED_TUFF_DOUBLE_SLAB, BlockPolishedTuffDoubleSlab.class);
            register(POLISHED_TUFF_SLAB, BlockPolishedTuffSlab.class);
            register(POLISHED_TUFF_STAIRS, BlockPolishedTuffStairs.class);
            register(POLISHED_TUFF_WALL, BlockPolishedTuffWall.class);
            register(POPPY, BlockPoppy.class);
            register(PORTAL, BlockPortal.class);
            register(POTATOES, BlockPotatoes.class);
            register(POWDER_SNOW, BlockPowderSnow.class);
            register(POWERED_COMPARATOR, BlockPoweredComparator.class);
            register(POWERED_REPEATER, BlockPoweredRepeater.class);
            register(PRISMARINE, BlockPrismarine.class);
            register(PRISMARINE_BRICKS_STAIRS, BlockPrismarineBricksStairs.class);
            register(PRISMARINE_STAIRS, BlockPrismarineStairs.class);
            register(PUMPKIN, BlockPumpkin.class);
            register(PUMPKIN_STEM, BlockPumpkinStem.class);
            register(PURPLE_CANDLE, BlockPurpleCandle.class);
            register(PURPLE_CANDLE_CAKE, BlockPurpleCandleCake.class);
            register(PURPLE_CARPET, BlockPurpleCarpet.class);
            register(PURPLE_CONCRETE, BlockPurpleConcrete.class);
            register(PURPLE_CONCRETE_POWDER, BlockPurpleConcretePowder.class);
            register(PURPLE_GLAZED_TERRACOTTA, BlockPurpleGlazedTerracotta.class);
            register(PURPLE_SHULKER_BOX, BlockPurpleShulkerBox.class);
            register(PURPLE_STAINED_GLASS, BlockPurpleStainedGlass.class);
            register(PURPLE_STAINED_GLASS_PANE, BlockPurpleStainedGlassPane.class);
            register(PURPLE_TERRACOTTA, BlockPurpleTerracotta.class);
            register(PURPLE_WOOL, BlockPurpleWool.class);
            register(PURPUR_BLOCK, BlockPurpurBlock.class);
            register(PURPUR_STAIRS, BlockPurpurStairs.class);
            register(QUARTZ_BLOCK, BlockQuartzBlock.class);
            register(QUARTZ_BRICKS, BlockQuartzBricks.class);
            register(QUARTZ_ORE, BlockQuartzOre.class);
            register(QUARTZ_SLAB, BlockQuartzSlab.class);
            register(QUARTZ_STAIRS, BlockQuartzStairs.class);
            register(RAIL, BlockRail.class);
            register(RAW_COPPER_BLOCK, BlockRawCopperBlock.class);
            register(RAW_GOLD_BLOCK, BlockRawGoldBlock.class);
            register(RAW_IRON_BLOCK, BlockRawIronBlock.class);
            register(RED_CANDLE, BlockRedCandle.class);
            register(RED_CANDLE_CAKE, BlockRedCandleCake.class);
            register(RED_CARPET, BlockRedCarpet.class);
            register(RED_CONCRETE, BlockRedConcrete.class);
            register(RED_CONCRETE_POWDER, BlockRedConcretePowder.class);
            register(RED_GLAZED_TERRACOTTA, BlockRedGlazedTerracotta.class);
            register(RED_MUSHROOM, BlockRedMushroom.class);
            register(RED_MUSHROOM_BLOCK, BlockRedMushroomBlock.class);
            register(RED_NETHER_BRICK, BlockRedNetherBrick.class);
            register(RED_NETHER_BRICK_STAIRS, BlockRedNetherBrickStairs.class);
            register(RED_SANDSTONE, BlockRedSandstone.class);
            register(RED_SANDSTONE_STAIRS, BlockRedSandstoneStairs.class);
            register(RED_SHULKER_BOX, BlockRedShulkerBox.class);
            register(RED_STAINED_GLASS, BlockRedStainedGlass.class);
            register(RED_STAINED_GLASS_PANE, BlockRedStainedGlassPane.class);
            register(RED_TERRACOTTA, BlockRedTerracotta.class);
            register(RED_TULIP, BlockRedTulip.class);
            register(RED_WOOL, BlockRedWool.class);
            register(REDSTONE_BLOCK, BlockRedstoneBlock.class);
            register(REDSTONE_LAMP, BlockRedstoneLamp.class);
            register(REDSTONE_ORE, BlockRedstoneOre.class);
            register(REDSTONE_TORCH, BlockRedstoneTorch.class);
            register(REDSTONE_WIRE, BlockRedstoneWire.class);
            register(REEDS, BlockReeds.class);
            register(REINFORCED_DEEPSLATE, BlockReinforcedDeepslate.class);
            register(REPEATING_COMMAND_BLOCK, BlockRepeatingCommandBlock.class);
            register(RESERVED6, BlockReserved6.class);
            register(RESPAWN_ANCHOR, BlockRespawnAnchor.class);
            register(ROSE_BUSH, BlockRoseBush.class);
            register(SAND, BlockSand.class);
            register(SANDSTONE, BlockSandstone.class);
            register(SANDSTONE_SLAB, BlockSandstoneSlab.class);
            register(SANDSTONE_STAIRS, BlockSandstoneStairs.class);
            register(SCAFFOLDING, BlockScaffolding.class);
            register(SCULK, BlockSculk.class);
            register(SCULK_CATALYST, BlockSculkCatalyst.class);
            register(SCULK_SENSOR, BlockSculkSensor.class);
            register(SCULK_SHRIEKER, BlockSculkShrieker.class);
            register(SCULK_VEIN, BlockSculkVein.class);
            register(SEA_LANTERN, BlockSeaLantern.class);
            register(SEA_PICKLE, BlockSeaPickle.class);
            register(SEAGRASS, BlockSeagrass.class);
            register(SHORT_GRASS, BlockShortGrass.class);
            register(SHROOMLIGHT, BlockShroomlight.class);
            register(SILVER_GLAZED_TERRACOTTA, BlockSilverGlazedTerracotta.class);
            register(SKULL, BlockSkull.class);
            register(SLIME, BlockSlime.class);
            register(SMALL_AMETHYST_BUD, BlockSmallAmethystBud.class);
            register(SMALL_DRIPLEAF_BLOCK, BlockSmallDripleafBlock.class);
            register(SMITHING_TABLE, BlockSmithingTable.class);
            register(SMOKER, BlockSmoker.class);
            register(SMOOTH_BASALT, BlockSmoothBasalt.class);
            register(SMOOTH_QUARTZ_STAIRS, BlockSmoothQuartzStairs.class);
            register(SMOOTH_RED_SANDSTONE_STAIRS, BlockSmoothRedSandstoneStairs.class);
            register(SMOOTH_SANDSTONE_STAIRS, BlockSmoothSandstoneStairs.class);
            register(SMOOTH_STONE, BlockSmoothStone.class);
            register(SMOOTH_STONE_SLAB, BlockSmoothStoneSlab.class);
            register(SNIFFER_EGG, BlockSnifferEgg.class);
            register(SNOW, BlockSnow.class);
            register(SNOW_LAYER, BlockSnowLayer.class);
            register(SOUL_CAMPFIRE, BlockSoulCampfire.class);
            register(SOUL_FIRE, BlockSoulFire.class);
            register(SOUL_LANTERN, BlockSoulLantern.class);
            register(SOUL_SAND, BlockSoulSand.class);
            register(SOUL_SOIL, BlockSoulSoil.class);
            register(SOUL_TORCH, BlockSoulTorch.class);
            register(SPONGE, BlockSponge.class);
            register(SPORE_BLOSSOM, BlockSporeBlossom.class);
            register(SPRUCE_BUTTON, BlockSpruceButton.class);
            register(SPRUCE_DOOR, BlockSpruceDoor.class);
            register(SPRUCE_DOUBLE_SLAB, BlockSpruceDoubleSlab.class);
            register(SPRUCE_FENCE, BlockSpruceFence.class);
            register(SPRUCE_FENCE_GATE, BlockSpruceFenceGate.class);
            register(SPRUCE_HANGING_SIGN, BlockSpruceHangingSign.class);
            register(SPRUCE_LEAVES, BlockSpruceLeaves.class);
            register(SPRUCE_LOG, BlockSpruceLog.class);
            register(SPRUCE_PLANKS, BlockSprucePlanks.class);
            register(SPRUCE_PRESSURE_PLATE, BlockSprucePressurePlate.class);
            register(SPRUCE_SAPLING, BlockSpruceSapling.class);
            register(SPRUCE_SLAB, BlockSpruceSlab.class);
            register(SPRUCE_STAIRS, BlockSpruceStairs.class);
            register(SPRUCE_STANDING_SIGN, BlockSpruceStandingSign.class);
            register(SPRUCE_TRAPDOOR, BlockSpruceTrapdoor.class);
            register(SPRUCE_WALL_SIGN, BlockSpruceWallSign.class);
            register(SPRUCE_WOOD, BlockSpruceWood.class);
            register(STANDING_BANNER, BlockStandingBanner.class);
            register(STANDING_SIGN, BlockStandingSign.class);
            register(STICKY_PISTON, BlockStickyPiston.class);
            register(STICKY_PISTON_ARM_COLLISION, BlockStickyPistonArmCollision.class);
            register(STONE, BlockStone.class);
            register(STONE_BLOCK_SLAB2, BlockStoneBlockSlab2.class);
            register(STONE_BLOCK_SLAB3, BlockStoneBlockSlab3.class);
            register(STONE_BLOCK_SLAB4, BlockStoneBlockSlab4.class);
            register(STONE_BRICK_SLAB, BlockStoneBrickSlab.class);
            register(STONE_BRICK_STAIRS, BlockStoneBrickStairs.class);
            register(STONE_BUTTON, BlockStoneButton.class);
            register(STONE_PRESSURE_PLATE, BlockStonePressurePlate.class);
            register(STONE_STAIRS, BlockStoneStairs.class);
            register(STONEBRICK, BlockStonebrick.class);
            register(STONECUTTER, BlockStonecutter.class);
            register(STONECUTTER_BLOCK, BlockStonecutterBlock.class);
            register(STRIPPED_ACACIA_LOG, BlockStrippedAcaciaLog.class);
            register(STRIPPED_ACACIA_WOOD, BlockStrippedAcaciaWood.class);
            register(STRIPPED_BAMBOO_BLOCK, BlockStrippedBambooBlock.class);
            register(STRIPPED_BIRCH_LOG, BlockStrippedBirchLog.class);
            register(STRIPPED_BIRCH_WOOD, BlockStrippedBirchWood.class);
            register(STRIPPED_CHERRY_LOG, BlockStrippedCherryLog.class);
            register(STRIPPED_CHERRY_WOOD, BlockStrippedCherryWood.class);
            register(STRIPPED_CRIMSON_HYPHAE, BlockStrippedCrimsonHyphae.class);
            register(STRIPPED_CRIMSON_STEM, BlockStrippedCrimsonStem.class);
            register(STRIPPED_DARK_OAK_LOG, BlockStrippedDarkOakLog.class);
            register(STRIPPED_DARK_OAK_WOOD, BlockStrippedDarkOakWood.class);
            register(STRIPPED_JUNGLE_LOG, BlockStrippedJungleLog.class);
            register(STRIPPED_JUNGLE_WOOD, BlockStrippedJungleWood.class);
            register(STRIPPED_MANGROVE_LOG, BlockStrippedMangroveLog.class);
            register(STRIPPED_MANGROVE_WOOD, BlockStrippedMangroveWood.class);
            register(STRIPPED_OAK_LOG, BlockStrippedOakLog.class);
            register(STRIPPED_OAK_WOOD, BlockStrippedOakWood.class);
            register(STRIPPED_SPRUCE_LOG, BlockStrippedSpruceLog.class);
            register(STRIPPED_SPRUCE_WOOD, BlockStrippedSpruceWood.class);
            register(STRIPPED_WARPED_HYPHAE, BlockStrippedWarpedHyphae.class);
            register(STRIPPED_WARPED_STEM, BlockStrippedWarpedStem.class);
            register(STRUCTURE_BLOCK, BlockStructureBlock.class);
            register(STRUCTURE_VOID, BlockStructureVoid.class);
            register(SUNFLOWER, BlockSunflower.class);
            register(SUSPICIOUS_GRAVEL, BlockSuspiciousGravel.class);
            register(SUSPICIOUS_SAND, BlockSuspiciousSand.class);
            register(SWEET_BERRY_BUSH, BlockSweetBerryBush.class);
            register(TALL_GRASS, BlockTallGrass.class);
            register(TARGET, BlockTarget.class);
            register(TINTED_GLASS, BlockTintedGlass.class);
            register(TNT, BlockTnt.class);
            register(TORCH, BlockTorch.class);
            register(TORCHFLOWER, BlockTorchflower.class);
            register(TORCHFLOWER_CROP, BlockTorchflowerCrop.class);
            register(TRAPDOOR, BlockTrapdoor.class);
            register(TRAPPED_CHEST, BlockTrappedChest.class);
            register(TRIAL_SPAWNER, BlockTrialSpawner.class);
            register(TRIP_WIRE, BlockTripWire.class);
            register(TRIPWIRE_HOOK, BlockTripwireHook.class);
            register(TUBE_CORAL, BlockTubeCoral.class);
            register(TUBE_CORAL_BLOCK, BlockTubeCoralBlock.class);
            register(TUBE_CORAL_FAN, BlockTubeCoralFan.class);
            register(TUFF, BlockTuff.class);
            register(TUFF_BRICK_DOUBLE_SLAB, BlockTuffBrickDoubleSlab.class);
            register(TUFF_BRICK_SLAB, BlockTuffBrickSlab.class);
            register(TUFF_BRICK_STAIRS, BlockTuffBrickStairs.class);
            register(TUFF_BRICK_WALL, BlockTuffBrickWall.class);
            register(TUFF_BRICKS, BlockTuffBricks.class);
            register(TUFF_DOUBLE_SLAB, BlockTuffDoubleSlab.class);
            register(TUFF_SLAB, BlockTuffSlab.class);
            register(TUFF_STAIRS, BlockTuffStairs.class);
            register(TUFF_WALL, BlockTuffWall.class);
            register(TURTLE_EGG, BlockTurtleEgg.class);
            register(TWISTING_VINES, BlockTwistingVines.class);
            register(UNDYED_SHULKER_BOX, BlockUndyedShulkerBox.class);
            register(UNKNOWN, BlockUnknown.class);
            register(UNLIT_REDSTONE_TORCH, BlockUnlitRedstoneTorch.class);
            register(UNPOWERED_COMPARATOR, BlockUnpoweredComparator.class);
            register(UNPOWERED_REPEATER, BlockUnpoweredRepeater.class);
            register(VAULT, BlockVault.class);
            register(VERDANT_FROGLIGHT, BlockVerdantFroglight.class);
            register(VINE, BlockVine.class);
            register(WALL_BANNER, BlockWallBanner.class);
            register(WALL_SIGN, BlockWallSign.class);
            register(WARPED_BUTTON, BlockWarpedButton.class);
            register(WARPED_DOOR, BlockWarpedDoor.class);
            register(WARPED_DOUBLE_SLAB, BlockWarpedDoubleSlab.class);
            register(WARPED_FENCE, BlockWarpedFence.class);
            register(WARPED_FENCE_GATE, BlockWarpedFenceGate.class);
            register(WARPED_FUNGUS, BlockWarpedFungus.class);
            register(WARPED_HANGING_SIGN, BlockWarpedHangingSign.class);
            register(WARPED_HYPHAE, BlockWarpedHyphae.class);
            register(WARPED_NYLIUM, BlockWarpedNylium.class);
            register(WARPED_PLANKS, BlockWarpedPlanks.class);
            register(WARPED_PRESSURE_PLATE, BlockWarpedPressurePlate.class);
            register(WARPED_ROOTS, BlockWarpedRoots.class);
            register(WARPED_SLAB, BlockWarpedSlab.class);
            register(WARPED_STAIRS, BlockWarpedStairs.class);
            register(WARPED_STANDING_SIGN, BlockWarpedStandingSign.class);
            register(WARPED_STEM, BlockWarpedStem.class);
            register(WARPED_TRAPDOOR, BlockWarpedTrapdoor.class);
            register(WARPED_WALL_SIGN, BlockWarpedWallSign.class);
            register(WARPED_WART_BLOCK, BlockWarpedWartBlock.class);
            register(WATER, BlockWater.class);
            register(WATERLILY, BlockWaterlily.class);
            register(WAXED_CHISELED_COPPER, BlockWaxedChiseledCopper.class);
            register(WAXED_COPPER, BlockWaxedCopper.class);
            register(WAXED_COPPER_BULB, BlockWaxedCopperBulb.class);
            register(WAXED_COPPER_DOOR, BlockWaxedCopperDoor.class);
            register(WAXED_COPPER_GRATE, BlockWaxedCopperGrate.class);
            register(WAXED_COPPER_TRAPDOOR, BlockWaxedCopperTrapdoor.class);
            register(WAXED_CUT_COPPER, BlockWaxedCutCopper.class);
            register(WAXED_CUT_COPPER_SLAB, BlockWaxedCutCopperSlab.class);
            register(WAXED_CUT_COPPER_STAIRS, BlockWaxedCutCopperStairs.class);
            register(WAXED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedDoubleCutCopperSlab.class);
            register(WAXED_EXPOSED_CHISELED_COPPER, BlockWaxedExposedChiseledCopper.class);
            register(WAXED_EXPOSED_COPPER, BlockWaxedExposedCopper.class);
            register(WAXED_EXPOSED_COPPER_BULB, BlockWaxedExposedCopperBulb.class);
            register(WAXED_EXPOSED_COPPER_DOOR, BlockWaxedExposedCopperDoor.class);
            register(WAXED_EXPOSED_COPPER_GRATE, BlockWaxedExposedCopperGrate.class);
            register(WAXED_EXPOSED_COPPER_TRAPDOOR, BlockWaxedExposedCopperTrapdoor.class);
            register(WAXED_EXPOSED_CUT_COPPER, BlockWaxedExposedCutCopper.class);
            register(WAXED_EXPOSED_CUT_COPPER_SLAB, BlockWaxedExposedCutCopperSlab.class);
            register(WAXED_EXPOSED_CUT_COPPER_STAIRS, BlockWaxedExposedCutCopperStairs.class);
            register(WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedExposedDoubleCutCopperSlab.class);
            register(WAXED_OXIDIZED_CHISELED_COPPER, BlockWaxedOxidizedChiseledCopper.class);
            register(WAXED_OXIDIZED_COPPER, BlockWaxedOxidizedCopper.class);
            register(WAXED_OXIDIZED_COPPER_BULB, BlockWaxedOxidizedCopperBulb.class);
            register(WAXED_OXIDIZED_COPPER_DOOR, BlockWaxedOxidizedCopperDoor.class);
            register(WAXED_OXIDIZED_COPPER_GRATE, BlockWaxedOxidizedCopperGrate.class);
            register(WAXED_OXIDIZED_COPPER_TRAPDOOR, BlockWaxedOxidizedCopperTrapdoor.class);
            register(WAXED_OXIDIZED_CUT_COPPER, BlockWaxedOxidizedCutCopper.class);
            register(WAXED_OXIDIZED_CUT_COPPER_SLAB, BlockWaxedOxidizedCutCopperSlab.class);
            register(WAXED_OXIDIZED_CUT_COPPER_STAIRS, BlockWaxedOxidizedCutCopperStairs.class);
            register(WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedOxidizedDoubleCutCopperSlab.class);
            register(WAXED_WEATHERED_CHISELED_COPPER, BlockWaxedWeatheredChiseledCopper.class);
            register(WAXED_WEATHERED_COPPER, BlockWaxedWeatheredCopper.class);
            register(WAXED_WEATHERED_COPPER_BULB, BlockWaxedWeatheredCopperBulb.class);
            register(WAXED_WEATHERED_COPPER_DOOR, BlockWaxedWeatheredCopperDoor.class);
            register(WAXED_WEATHERED_COPPER_GRATE, BlockWaxedWeatheredCopperGrate.class);
            register(WAXED_WEATHERED_COPPER_TRAPDOOR, BlockWaxedWeatheredCopperTrapdoor.class);
            register(WAXED_WEATHERED_CUT_COPPER, BlockWaxedWeatheredCutCopper.class);
            register(WAXED_WEATHERED_CUT_COPPER_SLAB, BlockWaxedWeatheredCutCopperSlab.class);
            register(WAXED_WEATHERED_CUT_COPPER_STAIRS, BlockWaxedWeatheredCutCopperStairs.class);
            register(WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedWeatheredDoubleCutCopperSlab.class);
            register(WEATHERED_CHISELED_COPPER, BlockWeatheredChiseledCopper.class);
            register(WEATHERED_COPPER, BlockWeatheredCopper.class);
            register(WEATHERED_COPPER_BULB, BlockWeatheredCopperBulb.class);
            register(WEATHERED_COPPER_DOOR, BlockWeatheredCopperDoor.class);
            register(WEATHERED_COPPER_GRATE, BlockWeatheredCopperGrate.class);
            register(WEATHERED_COPPER_TRAPDOOR, BlockWeatheredCopperTrapdoor.class);
            register(WEATHERED_CUT_COPPER, BlockWeatheredCutCopper.class);
            register(WEATHERED_CUT_COPPER_SLAB, BlockWeatheredCutCopperSlab.class);
            register(WEATHERED_CUT_COPPER_STAIRS, BlockWeatheredCutCopperStairs.class);
            register(WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockWeatheredDoubleCutCopperSlab.class);
            register(WEB, BlockWeb.class);
            register(WEEPING_VINES, BlockWeepingVines.class);
            register(WHEAT, BlockWheat.class);
            register(WHITE_CANDLE, BlockWhiteCandle.class);
            register(WHITE_CANDLE_CAKE, BlockWhiteCandleCake.class);
            register(WHITE_CARPET, BlockWhiteCarpet.class);
            register(WHITE_CONCRETE, BlockWhiteConcrete.class);
            register(WHITE_CONCRETE_POWDER, BlockWhiteConcretePowder.class);
            register(WHITE_GLAZED_TERRACOTTA, BlockWhiteGlazedTerracotta.class);
            register(WHITE_SHULKER_BOX, BlockWhiteShulkerBox.class);
            register(WHITE_STAINED_GLASS, BlockWhiteStainedGlass.class);
            register(WHITE_STAINED_GLASS_PANE, BlockWhiteStainedGlassPane.class);
            register(WHITE_TERRACOTTA, BlockWhiteTerracotta.class);
            register(WHITE_TULIP, BlockWhiteTulip.class);
            register(WHITE_WOOL, BlockWhiteWool.class);
            register(WITHER_ROSE, BlockWitherRose.class);
            register(WOODEN_BUTTON, BlockWoodenButton.class);
            register(WOODEN_DOOR, BlockWoodenDoor.class);
            register(WOODEN_PRESSURE_PLATE, BlockWoodenPressurePlate.class);
            register(YELLOW_CANDLE, BlockYellowCandle.class);
            register(YELLOW_CANDLE_CAKE, BlockYellowCandleCake.class);
            register(YELLOW_CARPET, BlockYellowCarpet.class);
            register(YELLOW_CONCRETE, BlockYellowConcrete.class);
            register(YELLOW_CONCRETE_POWDER, BlockYellowConcretePowder.class);
            register(YELLOW_FLOWER, BlockYellowFlower.class);
            register(YELLOW_GLAZED_TERRACOTTA, BlockYellowGlazedTerracotta.class);
            register(YELLOW_SHULKER_BOX, BlockYellowShulkerBox.class);
            register(YELLOW_STAINED_GLASS, BlockYellowStainedGlass.class);
            register(YELLOW_STAINED_GLASS_PANE, BlockYellowStainedGlassPane.class);
            register(YELLOW_TERRACOTTA, BlockYellowTerracotta.class);
            register(YELLOW_WOOL, BlockYellowWool.class);
        } catch (RegisterException ignore) {
        }
    }

    public void trim() {
        CACHE_CONSTRUCTORS.trim();
        PROPERTIES.trim();
    }

    @UnmodifiableView
    public Set<String> getKeySet() {
        return Collections.unmodifiableSet(KEYSET);
    }

    @Override
    public void register(String key, Class<? extends Block> value) throws RegisterException {
        if (skipBlockSet.contains(key)) return;// skip for experimental or educational blocks
        if (Modifier.isAbstract(value.getModifiers())) {
            throw new RegisterException("You can't register a abstract block class!");
        }
        try {
            Field properties = value.getDeclaredField("PROPERTIES");
            properties.setAccessible(true);
            int modifiers = properties.getModifiers();

            try {
                if (value.getMethod("getProperties").getDeclaringClass() != value) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception noSuchMethodException) {
                throw new RegisterException("There block: %s must override a method \n@Override\n".formatted(key) +
                        "    public @NotNull BlockProperties getProperties() {\n" +
                        "        return PROPERTIES;\n" +
                        "    } in this class!");
            }
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && properties.getType() == BlockProperties.class) {
                BlockProperties blockProperties = (BlockProperties) properties.get(value);
                FastConstructor<? extends Block> c = FastConstructor.create(value.getConstructor(BlockState.class));
                if (CACHE_CONSTRUCTORS.putIfAbsent(blockProperties.getIdentifier(), c) != null) {
                    throw new RegisterException("This block has already been registered with the identifier: " + blockProperties.getIdentifier());
                } else {
                    KEYSET.add(key);
                    PROPERTIES.put(key, blockProperties);
                    blockProperties.getSpecialValueMap().values().forEach(Registries.BLOCKSTATE::registerInternal);
                }
            } else {
                throw new RegisterException("There block: %s must define a field `public static final BlockProperties PROPERTIES` in this class!".formatted(key));
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            throw new RegisterException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * register custom item
     */
    @SafeVarargs
    public final void registerCustomBlock(Plugin plugin, Class<? extends Block>... values) throws RegisterException {
        for (var c : values) {
            registerCustomBlock(plugin, c);
        }
    }

    /**
     * register custom block
     */
    public void registerCustomBlock(Plugin plugin, Class<? extends Block> value) throws RegisterException {
        if (Modifier.isAbstract(value.getModifiers())) {
            throw new RegisterException("You can't register a abstract block class!");
        }
        try {
            Field properties = value.getDeclaredField("PROPERTIES");
            properties.setAccessible(true);
            int modifiers = properties.getModifiers();
            BlockProperties blockProperties;
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && properties.getType() == BlockProperties.class) {
                blockProperties = (BlockProperties) properties.get(value);
            } else {
                throw new RegisterException("There custom block class: %s must define a field `public static final BlockProperties PROPERTIES` in this class!".formatted(value.getSimpleName()));
            }
            try {
                if (value.getMethod("getProperties").getDeclaringClass() != value) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception noSuchMethodException) {
                throw new RegisterException("There custom block class: %s must override a method \n@Override\n".formatted(value.getSimpleName()) +
                        "    public @NotNull BlockProperties getProperties() {\n" +
                        "        return PROPERTIES;\n" +
                        "    } in this class!");
            }
            String key = blockProperties.getIdentifier();
            FastMemberLoader memberLoader = fastMemberLoaderCache.computeIfAbsent(plugin.getName(), p -> new FastMemberLoader(plugin.getPluginClassLoader()));
            FastConstructor<? extends Block> c = FastConstructor.create(value.getConstructor(BlockState.class), memberLoader, false);
            if (CACHE_CONSTRUCTORS.putIfAbsent(key, c) == null) {
                if (CustomBlock.class.isAssignableFrom(value)) {
                    CustomBlock customBlock = (CustomBlock) c.invoke((Object) null);
                    List<CustomBlockDefinition> customBlockDefinitions = CUSTOM_BLOCK_DEFINITIONS.computeIfAbsent(plugin, (p) -> new ArrayList<>());
                    customBlockDefinitions.add(customBlock.getDefinition());
                    int rid = 255 - CustomBlockDefinition.getRuntimeId(customBlock.getId());
                    Registries.ITEM_RUNTIMEID.registerCustomRuntimeItem(new ItemRuntimeIdRegistry.RuntimeEntry(customBlock.getId(), rid, false));
                    if (customBlock.shouldBeRegisteredInCreative()) {
                        ItemBlock itemBlock = new ItemBlock(customBlock.toBlock());
                        itemBlock.setNetId(null);
                        Registries.CREATIVE.addCreativeItem(itemBlock);
                    }
                    KEYSET.add(key);
                    PROPERTIES.put(key, blockProperties);
                    blockProperties.getSpecialValueMap().values().forEach(Registries.BLOCKSTATE::registerInternal);
                } else {
                    throw new RegisterException("Register Error,must implement the CustomBlock interface!");
                }
            } else {
                throw new RegisterException("There custom block has already been registered with the identifier: " + key);
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            throw new RegisterException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void register0(String key, Class<? extends Block> value) {
        try {
            register(key, value);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @UnmodifiableView
    public List<CustomBlockDefinition> getCustomBlockDefinitionList() {
        return CUSTOM_BLOCK_DEFINITIONS.values().stream().flatMap(List::stream).toList();
    }

    public void reload() {
        isLoad.set(false);
        KEYSET.clear();
        CACHE_CONSTRUCTORS.clear();
        PROPERTIES.clear();
        CUSTOM_BLOCK_DEFINITIONS.clear();
        init();
    }

    public BlockProperties getBlockProperties(String identifier) {
        BlockProperties properties = PROPERTIES.get(identifier);
        if (properties == null) {
            throw new IllegalArgumentException("Get the Block State from a unknown id: " + identifier);
        } else return properties;
    }

    @Override
    public Block get(String identifier) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return null;
        try {
            return (Block) constructor.invoke((Object) null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Block get(String identifier, int x, int y, int z) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return null;
        try {
            var b = (Block) constructor.invoke((Object) null);
            b.x = x;
            b.y = y;
            b.z = z;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Block get(String identifier, int x, int y, int z, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return null;
        try {
            var b = (Block) constructor.invoke((Object) null);
            b.x = x;
            b.y = y;
            b.z = z;
            b.level = level;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Block get(String identifier, int x, int y, int z, int layer, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return null;
        try {
            var b = (Block) constructor.invoke((Object) null);
            b.x = x;
            b.y = y;
            b.z = z;
            b.level = level;
            b.layer = layer;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Block get(BlockState blockState) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return null;
        try {
            return (Block) constructor.invoke(blockState);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Block get(BlockState blockState, int x, int y, int z) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return null;
        try {
            var b = (Block) constructor.invoke(blockState);
            b.x = x;
            b.y = y;
            b.z = z;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Block get(BlockState blockState, int x, int y, int z, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return null;
        try {
            var b = (Block) constructor.invoke(blockState);
            b.x = x;
            b.y = y;
            b.z = z;
            b.level = level;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Block get(BlockState blockState, int x, int y, int z, int layer, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return null;
        try {
            var b = (Block) constructor.invoke(blockState);
            b.x = x;
            b.y = y;
            b.z = z;
            b.level = level;
            b.layer = layer;
            return b;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
