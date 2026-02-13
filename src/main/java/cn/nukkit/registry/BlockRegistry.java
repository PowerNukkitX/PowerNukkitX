package cn.nukkit.registry;

import cn.nukkit.block.*;
import cn.nukkit.block.copper.bars.*;
import cn.nukkit.block.copper.bulb.*;
import cn.nukkit.block.copper.chain.*;
import cn.nukkit.block.copper.chest.*;
import cn.nukkit.block.copper.golem.*;
import cn.nukkit.block.copper.lantern.*;
import cn.nukkit.block.copper.lightningrod.*;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.block.shelf.*;
import cn.nukkit.education.Education;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.CompoundTag;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

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
    private static final Map<String, CustomBlockDefinition> CUSTOM_BLOCK_DEFINITION_BY_ID = new HashMap<>();

    public static final List<String> skipBlocks = List.of(
            "minecraft:deprecated_anvil",
            "minecraft:deprecated_purpur_block_1",
            "minecraft:deprecated_purpur_block_2"
    );

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;

        register0(ACACIA_BUTTON, BlockAcaciaButton.class);
        register0(ACACIA_DOOR, BlockAcaciaDoor.class);
        register0(ACACIA_DOUBLE_SLAB, BlockAcaciaDoubleSlab.class);
        register0(ACACIA_FENCE, BlockAcaciaFence.class);
        register0(ACACIA_FENCE_GATE, BlockAcaciaFenceGate.class);
        register0(ACACIA_HANGING_SIGN, BlockAcaciaHangingSign.class);
        register0(ACACIA_LEAVES, BlockAcaciaLeaves.class);
        register0(ACACIA_LOG, BlockAcaciaLog.class);
        register0(ACACIA_PLANKS, BlockAcaciaPlanks.class);
        register0(ACACIA_PRESSURE_PLATE, BlockAcaciaPressurePlate.class);
        register0(ACACIA_SAPLING, BlockAcaciaSapling.class);
        register0(ACACIA_SLAB, BlockAcaciaSlab.class);
        register0(ACACIA_STAIRS, BlockAcaciaStairs.class);
        register0(ACACIA_STANDING_SIGN, BlockAcaciaStandingSign.class);
        register0(ACACIA_TRAPDOOR, BlockAcaciaTrapdoor.class);
        register0(ACACIA_WALL_SIGN, BlockAcaciaWallSign.class);
        register0(ACACIA_WOOD, BlockAcaciaWood.class);
        register0(ACTIVATOR_RAIL, BlockActivatorRail.class);
        register0(AIR, BlockAir.class);
        register0(ALLIUM, BlockAllium.class);
        register0(ALLOW, BlockAllow.class);
        register0(AMETHYST_BLOCK, BlockAmethystBlock.class);
        register0(AMETHYST_CLUSTER, BlockAmethystCluster.class);
        register0(ANCIENT_DEBRIS, BlockAncientDebris.class);
        register0(ANDESITE, BlockAndesite.class);
        register0(ANDESITE_DOUBLE_SLAB, BlockAndesiteDoubleSlab.class);
        register0(ANDESITE_SLAB, BlockAndesiteSlab.class);
        register0(ANDESITE_STAIRS, BlockAndesiteStairs.class);
        register0(ANDESITE_WALL, BlockAndesiteWall.class);
        register0(ANVIL, BlockAnvil.class);
        register0(AZALEA, BlockAzalea.class);
        register0(AZALEA_LEAVES, BlockAzaleaLeaves.class);
        register0(AZALEA_LEAVES_FLOWERED, BlockAzaleaLeavesFlowered.class);
        register0(AZURE_BLUET, BlockAzureBluet.class);
        register0(BAMBOO, BlockBamboo.class);
        register0(BAMBOO_BLOCK, BlockBambooBlock.class);
        register0(BAMBOO_BUTTON, BlockBambooButton.class);
        register0(BAMBOO_DOOR, BlockBambooDoor.class);
        register0(BAMBOO_DOUBLE_SLAB, BlockBambooDoubleSlab.class);
        register0(BAMBOO_FENCE, BlockBambooFence.class);
        register0(BAMBOO_FENCE_GATE, BlockBambooFenceGate.class);
        register0(BAMBOO_HANGING_SIGN, BlockBambooHangingSign.class);
        register0(BAMBOO_MOSAIC, BlockBambooMosaic.class);
        register0(BAMBOO_MOSAIC_DOUBLE_SLAB, BlockBambooMosaicDoubleSlab.class);
        register0(BAMBOO_MOSAIC_SLAB, BlockBambooMosaicSlab.class);
        register0(BAMBOO_MOSAIC_STAIRS, BlockBambooMosaicStairs.class);
        register0(BAMBOO_PLANKS, BlockBambooPlanks.class);
        register0(BAMBOO_PRESSURE_PLATE, BlockBambooPressurePlate.class);
        register0(BAMBOO_SAPLING, BlockBambooSapling.class);
        register0(BAMBOO_SLAB, BlockBambooSlab.class);
        register0(BAMBOO_STAIRS, BlockBambooStairs.class);
        register0(BAMBOO_STANDING_SIGN, BlockBambooStandingSign.class);
        register0(BAMBOO_TRAPDOOR, BlockBambooTrapdoor.class);
        register0(BAMBOO_WALL_SIGN, BlockBambooWallSign.class);
        register0(BARREL, BlockBarrel.class);
        register0(BARRIER, BlockBarrier.class);
        register0(BASALT, BlockBasalt.class);
        register0(BEACON, BlockBeacon.class);
        register0(BED, BlockBed.class);
        register0(BEDROCK, BlockBedrock.class);
        register0(BEE_NEST, BlockBeeNest.class);
        register0(BEEHIVE, BlockBeehive.class);
        register0(BEETROOT, BlockBeetroot.class);
        register0(BELL, BlockBell.class);
        register0(BIG_DRIPLEAF, BlockBigDripleaf.class);
        register0(BIRCH_BUTTON, BlockBirchButton.class);
        register0(BIRCH_DOOR, BlockBirchDoor.class);
        register0(BIRCH_DOUBLE_SLAB, BlockBirchDoubleSlab.class);
        register0(BIRCH_FENCE, BlockBirchFence.class);
        register0(BIRCH_FENCE_GATE, BlockBirchFenceGate.class);
        register0(BIRCH_HANGING_SIGN, BlockBirchHangingSign.class);
        register0(BIRCH_LEAVES, BlockBirchLeaves.class);
        register0(BIRCH_LOG, BlockBirchLog.class);
        register0(BIRCH_PLANKS, BlockBirchPlanks.class);
        register0(BIRCH_PRESSURE_PLATE, BlockBirchPressurePlate.class);
        register0(BIRCH_SAPLING, BlockBirchSapling.class);
        register0(BIRCH_SLAB, BlockBirchSlab.class);
        register0(BIRCH_STAIRS, BlockBirchStairs.class);
        register0(BIRCH_STANDING_SIGN, BlockBirchStandingSign.class);
        register0(BIRCH_TRAPDOOR, BlockBirchTrapdoor.class);
        register0(BIRCH_WALL_SIGN, BlockBirchWallSign.class);
        register0(BIRCH_WOOD, BlockBirchWood.class);
        register0(BLACK_CANDLE, BlockBlackCandle.class);
        register0(BLACK_CANDLE_CAKE, BlockBlackCandleCake.class);
        register0(BLACK_CARPET, BlockBlackCarpet.class);
        register0(BLACK_CONCRETE, BlockBlackConcrete.class);
        register0(BLACK_CONCRETE_POWDER, BlockBlackConcretePowder.class);
        register0(BLACK_GLAZED_TERRACOTTA, BlockBlackGlazedTerracotta.class);
        register0(BLACK_SHULKER_BOX, BlockBlackShulkerBox.class);
        register0(BLACK_STAINED_GLASS, BlockBlackStainedGlass.class);
        register0(BLACK_STAINED_GLASS_PANE, BlockBlackStainedGlassPane.class);
        register0(BLACK_TERRACOTTA, BlockBlackTerracotta.class);
        register0(BLACK_WOOL, BlockBlackWool.class);
        register0(BLACKSTONE, BlockBlackstone.class);
        register0(BLACKSTONE_DOUBLE_SLAB, BlockBlackstoneDoubleSlab.class);
        register0(BLACKSTONE_SLAB, BlockBlackstoneSlab.class);
        register0(BLACKSTONE_STAIRS, BlockBlackstoneStairs.class);
        register0(BLACKSTONE_WALL, BlockBlackstoneWall.class);
        register0(BLAST_FURNACE, BlockBlastFurnace.class);
        register0(BLUE_CANDLE, BlockBlueCandle.class);
        register0(BLUE_CANDLE_CAKE, BlockBlueCandleCake.class);
        register0(BLUE_CARPET, BlockBlueCarpet.class);
        register0(BLUE_CONCRETE, BlockBlueConcrete.class);
        register0(BLUE_CONCRETE_POWDER, BlockBlueConcretePowder.class);
        register0(BLUE_GLAZED_TERRACOTTA, BlockBlueGlazedTerracotta.class);
        register0(BLUE_ICE, BlockBlueIce.class);
        register0(BLUE_ORCHID, BlockBlueOrchid.class);
        register0(BLUE_SHULKER_BOX, BlockBlueShulkerBox.class);
        register0(BLUE_STAINED_GLASS, BlockBlueStainedGlass.class);
        register0(BLUE_STAINED_GLASS_PANE, BlockBlueStainedGlassPane.class);
        register0(BLUE_TERRACOTTA, BlockBlueTerracotta.class);
        register0(BLUE_WOOL, BlockBlueWool.class);
        register0(BONE_BLOCK, BlockBoneBlock.class);
        register0(BOOKSHELF, BlockBookshelf.class);
        register0(BORDER_BLOCK, BlockBorderBlock.class);
        register0(BRAIN_CORAL, BlockBrainCoral.class);
        register0(BRAIN_CORAL_BLOCK, BlockBrainCoralBlock.class);
        register0(BRAIN_CORAL_FAN, BlockBrainCoralFan.class);
        register0(BRAIN_CORAL_WALL_FAN, BlockBrainCoralWallFan.class);
        register0(BREWING_STAND, BlockBrewingStand.class);
        register0(BRICK_BLOCK, BlockBrickBlock.class);
        register0(BRICK_DOUBLE_SLAB, BlockBrickDoubleSlab.class);
        register0(BRICK_SLAB, BlockBrickSlab.class);
        register0(BRICK_STAIRS, BlockBrickStairs.class);
        register0(BRICK_WALL, BlockBrickWall.class);
        register0(BROWN_CANDLE, BlockBrownCandle.class);
        register0(BROWN_CANDLE_CAKE, BlockBrownCandleCake.class);
        register0(BROWN_CARPET, BlockBrownCarpet.class);
        register0(BROWN_CONCRETE, BlockBrownConcrete.class);
        register0(BROWN_CONCRETE_POWDER, BlockBrownConcretePowder.class);
        register0(BROWN_GLAZED_TERRACOTTA, BlockBrownGlazedTerracotta.class);
        register0(BROWN_MUSHROOM, BlockBrownMushroom.class);
        register0(BROWN_MUSHROOM_BLOCK, BlockBrownMushroomBlock.class);
        register0(BROWN_SHULKER_BOX, BlockBrownShulkerBox.class);
        register0(BROWN_STAINED_GLASS, BlockBrownStainedGlass.class);
        register0(BROWN_STAINED_GLASS_PANE, BlockBrownStainedGlassPane.class);
        register0(BROWN_TERRACOTTA, BlockBrownTerracotta.class);
        register0(BROWN_WOOL, BlockBrownWool.class);
        register0(BUBBLE_COLUMN, BlockBubbleColumn.class);
        register0(BUBBLE_CORAL, BlockBubbleCoral.class);
        register0(BUBBLE_CORAL_BLOCK, BlockBubbleCoralBlock.class);
        register0(BUBBLE_CORAL_FAN, BlockBubbleCoralFan.class);
        register0(BUBBLE_CORAL_WALL_FAN, BlockBubbleCoralWallFan.class);
        register0(BUDDING_AMETHYST, BlockBuddingAmethyst.class);
        register0(BUSH, BlockBush.class);
        register0(CACTUS, BlockCactus.class);
        register0(CACTUS_FLOWER, BlockCactusFlower.class);
        register0(CAKE, BlockCake.class);
        register0(CALCITE, BlockCalcite.class);
        register0(CALIBRATED_SCULK_SENSOR, BlockCalibratedSculkSensor.class);
        register0(CAMPFIRE, BlockCampfire.class);
        register0(CANDLE, BlockCandle.class);
        register0(CANDLE_CAKE, BlockCandleCake.class);
        register0(CARROTS, BlockCarrots.class);
        register0(CARTOGRAPHY_TABLE, BlockCartographyTable.class);
        register0(CARVED_PUMPKIN, BlockCarvedPumpkin.class);
        register0(CAULDRON, BlockCauldron.class);
        register0(CAVE_VINES, BlockCaveVines.class);
        register0(CAVE_VINES_BODY_WITH_BERRIES, BlockCaveVinesBodyWithBerries.class);
        register0(CAVE_VINES_HEAD_WITH_BERRIES, BlockCaveVinesHeadWithBerries.class);
        register0(IRON_CHAIN, BlockIronChain.class);
        register0(CHAIN_COMMAND_BLOCK, BlockChainCommandBlock.class);
        register0(CHERRY_BUTTON, BlockCherryButton.class);
        register0(CHERRY_DOOR, BlockCherryDoor.class);
        register0(CHERRY_DOUBLE_SLAB, BlockCherryDoubleSlab.class);
        register0(CHERRY_FENCE, BlockCherryFence.class);
        register0(CHERRY_FENCE_GATE, BlockCherryFenceGate.class);
        register0(CHERRY_HANGING_SIGN, BlockCherryHangingSign.class);
        register0(CHERRY_LEAVES, BlockCherryLeaves.class);
        register0(CHERRY_LOG, BlockCherryLog.class);
        register0(CHERRY_PLANKS, BlockCherryPlanks.class);
        register0(CHERRY_PRESSURE_PLATE, BlockCherryPressurePlate.class);
        register0(CHERRY_SAPLING, BlockCherrySapling.class);
        register0(CHERRY_SLAB, BlockCherrySlab.class);
        register0(CHERRY_STAIRS, BlockCherryStairs.class);
        register0(CHERRY_STANDING_SIGN, BlockCherryStandingSign.class);
        register0(CHERRY_TRAPDOOR, BlockCherryTrapdoor.class);
        register0(CHERRY_WALL_SIGN, BlockCherryWallSign.class);
        register0(CHERRY_WOOD, BlockCherryWood.class);
        register0(CHEST, BlockChest.class);
        register0(CHIPPED_ANVIL, BlockChippedAnvil.class);
        register0(CHISELED_BOOKSHELF, BlockChiseledBookshelf.class);
        register0(CHISELED_COPPER, BlockChiseledCopper.class);
        register0(CHISELED_DEEPSLATE, BlockChiseledDeepslate.class);
        register0(CHISELED_NETHER_BRICKS, BlockChiseledNetherBricks.class);
        register0(CHISELED_POLISHED_BLACKSTONE, BlockChiseledPolishedBlackstone.class);
        register0(CHISELED_QUARTZ_BLOCK, BlockChiseledQuartzBlock.class);
        register0(CHISELED_RED_SANDSTONE, BlockChiseledRedSandstone.class);
        register0(CHISELED_RESIN_BRICKS, BlockChiseledResinBricks.class);
        register0(CHISELED_SANDSTONE, BlockChiseledSandstone.class);
        register0(CHISELED_STONE_BRICKS, BlockChiseledStoneBricks.class);
        register0(CHISELED_TUFF, BlockChiseledTuff.class);
        register0(CHISELED_TUFF_BRICKS, BlockChiseledTuffBricks.class);
        register0(CHORUS_FLOWER, BlockChorusFlower.class);
        register0(CHORUS_PLANT, BlockChorusPlant.class);
        register0(CLAY, BlockClay.class);
        register0(CLIENT_REQUEST_PLACEHOLDER_BLOCK, BlockClientRequestPlaceholderBlock.class);
        register0(CLOSED_EYEBLOSSOM, BlockClosedEyeblossom.class);
        register0(COAL_BLOCK, BlockCoalBlock.class);
        register0(COAL_ORE, BlockCoalOre.class);
        register0(COARSE_DIRT, BlockCoarseDirt.class);
        register0(COBBLED_DEEPSLATE, BlockCobbledDeepslate.class);
        register0(COBBLED_DEEPSLATE_DOUBLE_SLAB, BlockCobbledDeepslateDoubleSlab.class);
        register0(COBBLED_DEEPSLATE_SLAB, BlockCobbledDeepslateSlab.class);
        register0(COBBLED_DEEPSLATE_STAIRS, BlockCobbledDeepslateStairs.class);
        register0(COBBLED_DEEPSLATE_WALL, BlockCobbledDeepslateWall.class);
        register0(COBBLESTONE, BlockCobblestone.class);
        register0(COBBLESTONE_DOUBLE_SLAB, BlockCobblestoneDoubleSlab.class);
        register0(COBBLESTONE_SLAB, BlockCobblestoneSlab.class);
        register0(COBBLESTONE_WALL, BlockCobblestoneWall.class);
        register0(COCOA, BlockCocoa.class);
        register0(COMMAND_BLOCK, BlockCommandBlock.class);
        register0(COMPOSTER, BlockComposter.class);
        register0(CONDUIT, BlockConduit.class);
        register0(COPPER_BLOCK, BlockCopperBlock.class);
        register0(COPPER_BULB, BlockCopperBulb.class);
        register0(COPPER_DOOR, BlockCopperDoor.class);
        register0(COPPER_GRATE, BlockCopperGrate.class);
        register0(COPPER_ORE, BlockCopperOre.class);
        register0(COPPER_TRAPDOOR, BlockCopperTrapdoor.class);
        register0(CORNFLOWER, BlockCornflower.class);
        register0(CRACKED_DEEPSLATE_BRICKS, BlockCrackedDeepslateBricks.class);
        register0(CRACKED_DEEPSLATE_TILES, BlockCrackedDeepslateTiles.class);
        register0(CRACKED_NETHER_BRICKS, BlockCrackedNetherBricks.class);
        register0(CRACKED_POLISHED_BLACKSTONE_BRICKS, BlockCrackedPolishedBlackstoneBricks.class);
        register0(CRACKED_STONE_BRICKS, BlockCrackedStoneBricks.class);
        register0(CRAFTER, BlockCrafter.class);
        register0(CRAFTING_TABLE, BlockCraftingTable.class);
        register0(CREAKING_HEART, BlockCreakingHeart.class);
        register0(CREEPER_HEAD, BlockCreeperHead.class);
        register0(CRIMSON_BUTTON, BlockCrimsonButton.class);
        register0(CRIMSON_DOOR, BlockCrimsonDoor.class);
        register0(CRIMSON_DOUBLE_SLAB, BlockCrimsonDoubleSlab.class);
        register0(CRIMSON_FENCE, BlockCrimsonFence.class);
        register0(CRIMSON_FENCE_GATE, BlockCrimsonFenceGate.class);
        register0(CRIMSON_FUNGUS, BlockCrimsonFungus.class);
        register0(CRIMSON_HANGING_SIGN, BlockCrimsonHangingSign.class);
        register0(CRIMSON_HYPHAE, BlockCrimsonHyphae.class);
        register0(CRIMSON_NYLIUM, BlockCrimsonNylium.class);
        register0(CRIMSON_PLANKS, BlockCrimsonPlanks.class);
        register0(CRIMSON_PRESSURE_PLATE, BlockCrimsonPressurePlate.class);
        register0(CRIMSON_ROOTS, BlockCrimsonRoots.class);
        register0(CRIMSON_SLAB, BlockCrimsonSlab.class);
        register0(CRIMSON_STAIRS, BlockCrimsonStairs.class);
        register0(CRIMSON_STANDING_SIGN, BlockCrimsonStandingSign.class);
        register0(CRIMSON_STEM, BlockCrimsonStem.class);
        register0(CRIMSON_TRAPDOOR, BlockCrimsonTrapdoor.class);
        register0(CRIMSON_WALL_SIGN, BlockCrimsonWallSign.class);
        register0(CRYING_OBSIDIAN, BlockCryingObsidian.class);
        register0(CUT_COPPER, BlockCutCopper.class);
        register0(CUT_COPPER_SLAB, BlockCutCopperSlab.class);
        register0(CUT_COPPER_STAIRS, BlockCutCopperStairs.class);
        register0(CUT_RED_SANDSTONE, BlockCutRedSandstone.class);
        register0(CUT_RED_SANDSTONE_DOUBLE_SLAB, BlockCutRedSandstoneDoubleSlab.class);
        register0(CUT_RED_SANDSTONE_SLAB, BlockCutRedSandstoneSlab.class);
        register0(CUT_SANDSTONE, BlockCutSandstone.class);
        register0(CUT_SANDSTONE_DOUBLE_SLAB, BlockCutSandstoneDoubleSlab.class);
        register0(CUT_SANDSTONE_SLAB, BlockCutSandstoneSlab.class);
        register0(CYAN_CANDLE, BlockCyanCandle.class);
        register0(CYAN_CANDLE_CAKE, BlockCyanCandleCake.class);
        register0(CYAN_CARPET, BlockCyanCarpet.class);
        register0(CYAN_CONCRETE, BlockCyanConcrete.class);
        register0(CYAN_CONCRETE_POWDER, BlockCyanConcretePowder.class);
        register0(CYAN_GLAZED_TERRACOTTA, BlockCyanGlazedTerracotta.class);
        register0(CYAN_SHULKER_BOX, BlockCyanShulkerBox.class);
        register0(CYAN_STAINED_GLASS, BlockCyanStainedGlass.class);
        register0(CYAN_STAINED_GLASS_PANE, BlockCyanStainedGlassPane.class);
        register0(CYAN_TERRACOTTA, BlockCyanTerracotta.class);
        register0(CYAN_WOOL, BlockCyanWool.class);
        register0(DAMAGED_ANVIL, BlockDamagedAnvil.class);
        register0(DARK_OAK_BUTTON, BlockDarkOakButton.class);
        register0(DARK_OAK_DOOR, BlockDarkOakDoor.class);
        register0(DARK_OAK_DOUBLE_SLAB, BlockDarkOakDoubleSlab.class);
        register0(DARK_OAK_FENCE, BlockDarkOakFence.class);
        register0(DARK_OAK_FENCE_GATE, BlockDarkOakFenceGate.class);
        register0(DARK_OAK_HANGING_SIGN, BlockDarkOakHangingSign.class);
        register0(DARK_OAK_LEAVES, BlockDarkOakLeaves.class);
        register0(DARK_OAK_LOG, BlockDarkOakLog.class);
        register0(DARK_OAK_PLANKS, BlockDarkOakPlanks.class);
        register0(DARK_OAK_PRESSURE_PLATE, BlockDarkOakPressurePlate.class);
        register0(DARK_OAK_SAPLING, BlockDarkOakSapling.class);
        register0(DARK_OAK_SLAB, BlockDarkOakSlab.class);
        register0(DARK_OAK_STAIRS, BlockDarkOakStairs.class);
        register0(DARK_OAK_TRAPDOOR, BlockDarkOakTrapdoor.class);
        register0(DARK_OAK_WOOD, BlockDarkOakWood.class);
        register0(DARK_PRISMARINE, BlockDarkPrismarine.class);
        register0(DARK_PRISMARINE_DOUBLE_SLAB, BlockDarkPrismarineDoubleSlab.class);
        register0(DARK_PRISMARINE_SLAB, BlockDarkPrismarineSlab.class);
        register0(DARK_PRISMARINE_STAIRS, BlockDarkPrismarineStairs.class);
        register0(DARKOAK_STANDING_SIGN, BlockDarkoakStandingSign.class);
        register0(DARKOAK_WALL_SIGN, BlockDarkoakWallSign.class);
        register0(DAYLIGHT_DETECTOR, BlockDaylightDetector.class);
        register0(DAYLIGHT_DETECTOR_INVERTED, BlockDaylightDetectorInverted.class);
        register0(DEAD_BRAIN_CORAL, BlockDeadBrainCoral.class);
        register0(DEAD_BRAIN_CORAL_BLOCK, BlockDeadBrainCoralBlock.class);
        register0(DEAD_BRAIN_CORAL_FAN, BlockDeadBrainCoralFan.class);
        register0(DEAD_BRAIN_CORAL_WALL_FAN, BlockDeadBrainCoralWallFan.class);
        register0(DEAD_BUBBLE_CORAL, BlockDeadBubbleCoral.class);
        register0(DEAD_BUBBLE_CORAL_BLOCK, BlockDeadBubbleCoralBlock.class);
        register0(DEAD_BUBBLE_CORAL_FAN, BlockDeadBubbleCoralFan.class);
        register0(DEAD_BUBBLE_CORAL_WALL_FAN, BlockDeadBubbleCoralWallFan.class);
        register0(DEAD_FIRE_CORAL, BlockDeadFireCoral.class);
        register0(DEAD_FIRE_CORAL_BLOCK, BlockDeadFireCoralBlock.class);
        register0(DEAD_FIRE_CORAL_FAN, BlockDeadFireCoralFan.class);
        register0(DEAD_FIRE_CORAL_WALL_FAN, BlockDeadFireCoralWallFan.class);
        register0(DEAD_HORN_CORAL, BlockDeadHornCoral.class);
        register0(DEAD_HORN_CORAL_BLOCK, BlockDeadHornCoralBlock.class);
        register0(DEAD_HORN_CORAL_FAN, BlockDeadHornCoralFan.class);
        register0(DEAD_HORN_CORAL_WALL_FAN, BlockDeadHornCoralWallFan.class);
        register0(DEAD_TUBE_CORAL, BlockDeadTubeCoral.class);
        register0(DEAD_TUBE_CORAL_BLOCK, BlockDeadTubeCoralBlock.class);
        register0(DEAD_TUBE_CORAL_FAN, BlockDeadTubeCoralFan.class);
        register0(DEAD_TUBE_CORAL_WALL_FAN, BlockDeadTubeCoralWallFan.class);
        register0(DEADBUSH, BlockDeadbush.class);
        register0(DECORATED_POT, BlockDecoratedPot.class);
        register0(DEEPSLATE, BlockDeepslate.class);
        register0(DEEPSLATE_BRICK_DOUBLE_SLAB, BlockDeepslateBrickDoubleSlab.class);
        register0(DEEPSLATE_BRICK_SLAB, BlockDeepslateBrickSlab.class);
        register0(DEEPSLATE_BRICK_STAIRS, BlockDeepslateBrickStairs.class);
        register0(DEEPSLATE_BRICK_WALL, BlockDeepslateBrickWall.class);
        register0(DEEPSLATE_BRICKS, BlockDeepslateBricks.class);
        register0(DEEPSLATE_COAL_ORE, BlockDeepslateCoalOre.class);
        register0(DEEPSLATE_COPPER_ORE, BlockDeepslateCopperOre.class);
        register0(DEEPSLATE_DIAMOND_ORE, BlockDeepslateDiamondOre.class);
        register0(DEEPSLATE_EMERALD_ORE, BlockDeepslateEmeraldOre.class);
        register0(DEEPSLATE_GOLD_ORE, BlockDeepslateGoldOre.class);
        register0(DEEPSLATE_IRON_ORE, BlockDeepslateIronOre.class);
        register0(DEEPSLATE_LAPIS_ORE, BlockDeepslateLapisOre.class);
        register0(DEEPSLATE_REDSTONE_ORE, BlockDeepslateRedstoneOre.class);
        register0(DEEPSLATE_TILE_DOUBLE_SLAB, BlockDeepslateTileDoubleSlab.class);
        register0(DEEPSLATE_TILE_SLAB, BlockDeepslateTileSlab.class);
        register0(DEEPSLATE_TILE_STAIRS, BlockDeepslateTileStairs.class);
        register0(DEEPSLATE_TILE_WALL, BlockDeepslateTileWall.class);
        register0(DEEPSLATE_TILES, BlockDeepslateTiles.class);
        register0(DENY, BlockDeny.class);
        register0(DETECTOR_RAIL, BlockDetectorRail.class);
        register0(DIAMOND_BLOCK, BlockDiamondBlock.class);
        register0(DIAMOND_ORE, BlockDiamondOre.class);
        register0(DIORITE, BlockDiorite.class);
        register0(DIORITE_DOUBLE_SLAB, BlockDioriteDoubleSlab.class);
        register0(DIORITE_SLAB, BlockDioriteSlab.class);
        register0(DIORITE_STAIRS, BlockDioriteStairs.class);
        register0(DIORITE_WALL, BlockDioriteWall.class);
        register0(DIRT, BlockDirt.class);
        register0(DIRT_WITH_ROOTS, BlockDirtWithRoots.class);
        register0(DISPENSER, BlockDispenser.class);
        register0(DOUBLE_CUT_COPPER_SLAB, BlockDoubleCutCopperSlab.class);
        register0(DRAGON_EGG, BlockDragonEgg.class);
        register0(DRAGON_HEAD, BlockDragonHead.class);
        register0(DRIED_KELP_BLOCK, BlockDriedKelpBlock.class);
        register0(DRIPSTONE_BLOCK, BlockDripstoneBlock.class);
        register0(DROPPER, BlockDropper.class);
        register0(EMERALD_BLOCK, BlockEmeraldBlock.class);
        register0(EMERALD_ORE, BlockEmeraldOre.class);
        register0(ENCHANTING_TABLE, BlockEnchantingTable.class);
        register0(END_BRICK_STAIRS, BlockEndBrickStairs.class);
        register0(END_BRICKS, BlockEndBricks.class);
        register0(END_GATEWAY, BlockEndGateway.class);
        register0(END_PORTAL, BlockEndPortal.class);
        register0(END_PORTAL_FRAME, BlockEndPortalFrame.class);
        register0(END_ROD, BlockEndRod.class);
        register0(END_STONE, BlockEndStone.class);
        register0(END_STONE_BRICK_DOUBLE_SLAB, BlockEndStoneBrickDoubleSlab.class);
        register0(END_STONE_BRICK_SLAB, BlockEndStoneBrickSlab.class);
        register0(END_STONE_BRICK_WALL, BlockEndStoneBrickWall.class);
        register0(ENDER_CHEST, BlockEnderChest.class);
        register0(EXPOSED_CHISELED_COPPER, BlockExposedChiseledCopper.class);
        register0(EXPOSED_COPPER, BlockExposedCopper.class);
        register0(EXPOSED_COPPER_BULB, BlockExposedCopperBulb.class);
        register0(EXPOSED_COPPER_DOOR, BlockExposedCopperDoor.class);
        register0(EXPOSED_COPPER_GRATE, BlockExposedCopperGrate.class);
        register0(EXPOSED_COPPER_TRAPDOOR, BlockExposedCopperTrapdoor.class);
        register0(EXPOSED_CUT_COPPER, BlockExposedCutCopper.class);
        register0(EXPOSED_CUT_COPPER_SLAB, BlockExposedCutCopperSlab.class);
        register0(EXPOSED_CUT_COPPER_STAIRS, BlockExposedCutCopperStairs.class);
        register0(EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockExposedDoubleCutCopperSlab.class);
        register0(FARMLAND, BlockFarmland.class);
        register0(FENCE_GATE, BlockFenceGate.class);
        register0(FERN, BlockFern.class);
        register0(FIRE, BlockFire.class);
        register0(FIREFLY_BUSH, BlockFireflyBush.class);
        register0(FIRE_CORAL, BlockFireCoral.class);
        register0(FIRE_CORAL_BLOCK, BlockFireCoralBlock.class);
        register0(FIRE_CORAL_FAN, BlockFireCoralFan.class);
        register0(FIRE_CORAL_WALL_FAN, BlockFireCoralWallFan.class);
        register0(FLETCHING_TABLE, BlockFletchingTable.class);
        register0(FLOWER_POT, BlockFlowerPot.class);
        register0(FLOWERING_AZALEA, BlockFloweringAzalea.class);
        register0(FLOWING_LAVA, BlockFlowingLava.class);
        register0(FLOWING_WATER, BlockFlowingWater.class);
        register0(FRAME, BlockFrame.class);
        register0(FROG_SPAWN, BlockFrogSpawn.class);
        register0(FROSTED_ICE, BlockFrostedIce.class);
        register0(FURNACE, BlockFurnace.class);
        register0(GILDED_BLACKSTONE, BlockGildedBlackstone.class);
        register0(GLASS, BlockGlass.class);
        register0(GLASS_PANE, BlockGlassPane.class);
        register0(GLOW_FRAME, BlockGlowFrame.class);
        register0(GLOW_LICHEN, BlockGlowLichen.class);
        register0(GLOWINGOBSIDIAN, BlockGlowingobsidian.class);
        register0(GLOWSTONE, BlockGlowstone.class);
        register0(GOLD_BLOCK, BlockGoldBlock.class);
        register0(GOLD_ORE, BlockGoldOre.class);
        register0(GOLDEN_RAIL, BlockGoldenRail.class);
        register0(GRANITE, BlockGranite.class);
        register0(GRANITE_DOUBLE_SLAB, BlockGraniteDoubleSlab.class);
        register0(GRANITE_SLAB, BlockGraniteSlab.class);
        register0(GRANITE_STAIRS, BlockGraniteStairs.class);
        register0(GRANITE_WALL, BlockGraniteWall.class);
        register0(GRASS_BLOCK, BlockGrassBlock.class);
        register0(GRASS_PATH, BlockGrassPath.class);
        register0(GRAVEL, BlockGravel.class);
        register0(GRAY_CANDLE, BlockGrayCandle.class);
        register0(GRAY_CANDLE_CAKE, BlockGrayCandleCake.class);
        register0(GRAY_CARPET, BlockGrayCarpet.class);
        register0(GRAY_CONCRETE, BlockGrayConcrete.class);
        register0(GRAY_CONCRETE_POWDER, BlockGrayConcretePowder.class);
        register0(GRAY_GLAZED_TERRACOTTA, BlockGrayGlazedTerracotta.class);
        register0(GRAY_SHULKER_BOX, BlockGrayShulkerBox.class);
        register0(GRAY_STAINED_GLASS, BlockGrayStainedGlass.class);
        register0(GRAY_STAINED_GLASS_PANE, BlockGrayStainedGlassPane.class);
        register0(GRAY_TERRACOTTA, BlockGrayTerracotta.class);
        register0(GRAY_WOOL, BlockGrayWool.class);
        register0(GREEN_CANDLE, BlockGreenCandle.class);
        register0(GREEN_CANDLE_CAKE, BlockGreenCandleCake.class);
        register0(GREEN_CARPET, BlockGreenCarpet.class);
        register0(GREEN_CONCRETE, BlockGreenConcrete.class);
        register0(GREEN_CONCRETE_POWDER, BlockGreenConcretePowder.class);
        register0(GREEN_GLAZED_TERRACOTTA, BlockGreenGlazedTerracotta.class);
        register0(GREEN_SHULKER_BOX, BlockGreenShulkerBox.class);
        register0(GREEN_STAINED_GLASS, BlockGreenStainedGlass.class);
        register0(GREEN_STAINED_GLASS_PANE, BlockGreenStainedGlassPane.class);
        register0(GREEN_TERRACOTTA, BlockGreenTerracotta.class);
        register0(GREEN_WOOL, BlockGreenWool.class);
        register0(GRINDSTONE, BlockGrindstone.class);
        register0(HANGING_ROOTS, BlockHangingRoots.class);
        register0(HARDENED_CLAY, BlockHardenedClay.class);
        register0(HAY_BLOCK, BlockHayBlock.class);
        register0(HEAVY_CORE, BlockHeavyCore.class);
        register0(HEAVY_WEIGHTED_PRESSURE_PLATE, BlockHeavyWeightedPressurePlate.class);
        register0(HONEY_BLOCK, BlockHoneyBlock.class);
        register0(HONEYCOMB_BLOCK, BlockHoneycombBlock.class);
        register0(HOPPER, BlockHopper.class);
        register0(HORN_CORAL, BlockHornCoral.class);
        register0(HORN_CORAL_BLOCK, BlockHornCoralBlock.class);
        register0(HORN_CORAL_FAN, BlockHornCoralFan.class);
        register0(HORN_CORAL_WALL_FAN, BlockHornCoralWallFan.class);
        register0(ICE, BlockIce.class);
        register0(INFESTED_CHISELED_STONE_BRICKS, BlockInfestedChiseledStoneBricks.class);
        register0(INFESTED_COBBLESTONE, BlockInfestedCobblestone.class);
        register0(INFESTED_CRACKED_STONE_BRICKS, BlockInfestedCrackedStoneBricks.class);
        register0(INFESTED_DEEPSLATE, BlockInfestedDeepslate.class);
        register0(INFESTED_MOSSY_STONE_BRICKS, BlockInfestedMossyStoneBricks.class);
        register0(INFESTED_STONE, BlockInfestedStone.class);
        register0(INFESTED_STONE_BRICKS, BlockInfestedStoneBricks.class);
        register0(INFO_UPDATE, BlockInfoUpdate.class);
        register0(INFO_UPDATE2, BlockInfoUpdate2.class);
        register0(INVISIBLE_BEDROCK, BlockInvisibleBedrock.class);
        register0(IRON_BARS, BlockIronBars.class);
        register0(IRON_BLOCK, BlockIronBlock.class);
        register0(IRON_DOOR, BlockIronDoor.class);
        register0(IRON_ORE, BlockIronOre.class);
        register0(IRON_TRAPDOOR, BlockIronTrapdoor.class);
        register0(JIGSAW, BlockJigsaw.class);
        register0(JUKEBOX, BlockJukebox.class);
        register0(JUNGLE_BUTTON, BlockJungleButton.class);
        register0(JUNGLE_DOOR, BlockJungleDoor.class);
        register0(JUNGLE_DOUBLE_SLAB, BlockJungleDoubleSlab.class);
        register0(JUNGLE_FENCE, BlockJungleFence.class);
        register0(JUNGLE_FENCE_GATE, BlockJungleFenceGate.class);
        register0(JUNGLE_HANGING_SIGN, BlockJungleHangingSign.class);
        register0(JUNGLE_LEAVES, BlockJungleLeaves.class);
        register0(JUNGLE_LOG, BlockJungleLog.class);
        register0(JUNGLE_PLANKS, BlockJunglePlanks.class);
        register0(JUNGLE_PRESSURE_PLATE, BlockJunglePressurePlate.class);
        register0(JUNGLE_SAPLING, BlockJungleSapling.class);
        register0(JUNGLE_SLAB, BlockJungleSlab.class);
        register0(JUNGLE_STAIRS, BlockJungleStairs.class);
        register0(JUNGLE_STANDING_SIGN, BlockJungleStandingSign.class);
        register0(JUNGLE_TRAPDOOR, BlockJungleTrapdoor.class);
        register0(JUNGLE_WALL_SIGN, BlockJungleWallSign.class);
        register0(JUNGLE_WOOD, BlockJungleWood.class);
        register0(KELP, BlockKelp.class);
        register0(LADDER, BlockLadder.class);
        register0(LANTERN, BlockLantern.class);
        register0(LAPIS_BLOCK, BlockLapisBlock.class);
        register0(LAPIS_ORE, BlockLapisOre.class);
        register0(LARGE_AMETHYST_BUD, BlockLargeAmethystBud.class);
        register0(LARGE_FERN, BlockLargeFern.class);
        register0(LAVA, BlockLava.class);
        register0(LEAF_LITTER, BlockLeafLitter.class);
        register0(LECTERN, BlockLectern.class);
        register0(LEVER, BlockLever.class);
        register0(LIGHT_BLOCK_0, BlockLightBlock0.class);
        register0(LIGHT_BLOCK_1, BlockLightBlock1.class);
        register0(LIGHT_BLOCK_2, BlockLightBlock2.class);
        register0(LIGHT_BLOCK_3, BlockLightBlock3.class);
        register0(LIGHT_BLOCK_4, BlockLightBlock4.class);
        register0(LIGHT_BLOCK_5, BlockLightBlock5.class);
        register0(LIGHT_BLOCK_6, BlockLightBlock6.class);
        register0(LIGHT_BLOCK_7, BlockLightBlock7.class);
        register0(LIGHT_BLOCK_8, BlockLightBlock8.class);
        register0(LIGHT_BLOCK_9, BlockLightBlock9.class);
        register0(LIGHT_BLOCK_10, BlockLightBlock10.class);
        register0(LIGHT_BLOCK_11, BlockLightBlock11.class);
        register0(LIGHT_BLOCK_12, BlockLightBlock12.class);
        register0(LIGHT_BLOCK_13, BlockLightBlock13.class);
        register0(LIGHT_BLOCK_14, BlockLightBlock14.class);
        register0(LIGHT_BLOCK_15, BlockLightBlock15.class);
        register0(LIGHT_BLUE_CANDLE, BlockLightBlueCandle.class);
        register0(LIGHT_BLUE_CANDLE_CAKE, BlockLightBlueCandleCake.class);
        register0(LIGHT_BLUE_CARPET, BlockLightBlueCarpet.class);
        register0(LIGHT_BLUE_CONCRETE, BlockLightBlueConcrete.class);
        register0(LIGHT_BLUE_CONCRETE_POWDER, BlockLightBlueConcretePowder.class);
        register0(LIGHT_BLUE_GLAZED_TERRACOTTA, BlockLightBlueGlazedTerracotta.class);
        register0(LIGHT_BLUE_SHULKER_BOX, BlockLightBlueShulkerBox.class);
        register0(LIGHT_BLUE_STAINED_GLASS, BlockLightBlueStainedGlass.class);
        register0(LIGHT_BLUE_STAINED_GLASS_PANE, BlockLightBlueStainedGlassPane.class);
        register0(LIGHT_BLUE_TERRACOTTA, BlockLightBlueTerracotta.class);
        register0(LIGHT_BLUE_WOOL, BlockLightBlueWool.class);
        register0(LIGHT_GRAY_CANDLE, BlockLightGrayCandle.class);
        register0(LIGHT_GRAY_CANDLE_CAKE, BlockLightGrayCandleCake.class);
        register0(LIGHT_GRAY_CARPET, BlockLightGrayCarpet.class);
        register0(LIGHT_GRAY_CONCRETE, BlockLightGrayConcrete.class);
        register0(LIGHT_GRAY_CONCRETE_POWDER, BlockLightGrayConcretePowder.class);
        register0(LIGHT_GRAY_SHULKER_BOX, BlockLightGrayShulkerBox.class);
        register0(LIGHT_GRAY_STAINED_GLASS, BlockLightGrayStainedGlass.class);
        register0(LIGHT_GRAY_STAINED_GLASS_PANE, BlockLightGrayStainedGlassPane.class);
        register0(LIGHT_GRAY_TERRACOTTA, BlockLightGrayTerracotta.class);
        register0(LIGHT_GRAY_WOOL, BlockLightGrayWool.class);
        register0(LIGHT_WEIGHTED_PRESSURE_PLATE, BlockLightWeightedPressurePlate.class);
        register0(LILAC, BlockLilac.class);
        register0(LILY_OF_THE_VALLEY, BlockLilyOfTheValley.class);
        register0(LIME_CANDLE, BlockLimeCandle.class);
        register0(LIME_CANDLE_CAKE, BlockLimeCandleCake.class);
        register0(LIME_CARPET, BlockLimeCarpet.class);
        register0(LIME_CONCRETE, BlockLimeConcrete.class);
        register0(LIME_CONCRETE_POWDER, BlockLimeConcretePowder.class);
        register0(LIME_GLAZED_TERRACOTTA, BlockLimeGlazedTerracotta.class);
        register0(LIME_SHULKER_BOX, BlockLimeShulkerBox.class);
        register0(LIME_STAINED_GLASS, BlockLimeStainedGlass.class);
        register0(LIME_STAINED_GLASS_PANE, BlockLimeStainedGlassPane.class);
        register0(LIME_TERRACOTTA, BlockLimeTerracotta.class);
        register0(LIME_WOOL, BlockLimeWool.class);
        register0(LIT_BLAST_FURNACE, BlockLitBlastFurnace.class);
        register0(LIT_DEEPSLATE_REDSTONE_ORE, BlockLitDeepslateRedstoneOre.class);
        register0(LIT_FURNACE, BlockLitFurnace.class);
        register0(LIT_PUMPKIN, BlockLitPumpkin.class);
        register0(LIT_REDSTONE_LAMP, BlockLitRedstoneLamp.class);
        register0(LIT_REDSTONE_ORE, BlockLitRedstoneOre.class);
        register0(LIT_SMOKER, BlockLitSmoker.class);
        register0(LODESTONE, BlockLodestone.class);
        register0(LOOM, BlockLoom.class);
        register0(MAGENTA_CANDLE, BlockMagentaCandle.class);
        register0(MAGENTA_CANDLE_CAKE, BlockMagentaCandleCake.class);
        register0(MAGENTA_CARPET, BlockMagentaCarpet.class);
        register0(MAGENTA_CONCRETE, BlockMagentaConcrete.class);
        register0(MAGENTA_CONCRETE_POWDER, BlockMagentaConcretePowder.class);
        register0(MAGENTA_GLAZED_TERRACOTTA, BlockMagentaGlazedTerracotta.class);
        register0(MAGENTA_SHULKER_BOX, BlockMagentaShulkerBox.class);
        register0(MAGENTA_STAINED_GLASS, BlockMagentaStainedGlass.class);
        register0(MAGENTA_STAINED_GLASS_PANE, BlockMagentaStainedGlassPane.class);
        register0(MAGENTA_TERRACOTTA, BlockMagentaTerracotta.class);
        register0(MAGENTA_WOOL, BlockMagentaWool.class);
        register0(MAGMA, BlockMagma.class);
        register0(MANGROVE_BUTTON, BlockMangroveButton.class);
        register0(MANGROVE_DOOR, BlockMangroveDoor.class);
        register0(MANGROVE_DOUBLE_SLAB, BlockMangroveDoubleSlab.class);
        register0(MANGROVE_FENCE, BlockMangroveFence.class);
        register0(MANGROVE_FENCE_GATE, BlockMangroveFenceGate.class);
        register0(MANGROVE_HANGING_SIGN, BlockMangroveHangingSign.class);
        register0(MANGROVE_LEAVES, BlockMangroveLeaves.class);
        register0(MANGROVE_LOG, BlockMangroveLog.class);
        register0(MANGROVE_PLANKS, BlockMangrovePlanks.class);
        register0(MANGROVE_PRESSURE_PLATE, BlockMangrovePressurePlate.class);
        register0(MANGROVE_PROPAGULE, BlockMangrovePropagule.class);
        register0(MANGROVE_ROOTS, BlockMangroveRoots.class);
        register0(MANGROVE_SLAB, BlockMangroveSlab.class);
        register0(MANGROVE_STAIRS, BlockMangroveStairs.class);
        register0(MANGROVE_STANDING_SIGN, BlockMangroveStandingSign.class);
        register0(MANGROVE_TRAPDOOR, BlockMangroveTrapdoor.class);
        register0(MANGROVE_WALL_SIGN, BlockMangroveWallSign.class);
        register0(MANGROVE_WOOD, BlockMangroveWood.class);
        register0(MEDIUM_AMETHYST_BUD, BlockMediumAmethystBud.class);
        register0(MELON_BLOCK, BlockMelonBlock.class);
        register0(MELON_STEM, BlockMelonStem.class);
        register0(MOB_SPAWNER, BlockMobSpawner.class);
        //register0(MONSTER_EGG, BlockMonsterEgg.class);
        register0(MOSS_BLOCK, BlockMossBlock.class);
        register0(MOSS_CARPET, BlockMossCarpet.class);
        register0(MOSSY_COBBLESTONE, BlockMossyCobblestone.class);
        register0(MOSSY_COBBLESTONE_DOUBLE_SLAB, BlockMossyCobblestoneDoubleSlab.class);
        register0(MOSSY_COBBLESTONE_SLAB, BlockMossyCobblestoneSlab.class);
        register0(MOSSY_COBBLESTONE_STAIRS, BlockMossyCobblestoneStairs.class);
        register0(MOSSY_COBBLESTONE_WALL, BlockMossyCobblestoneWall.class);
        register0(MOSSY_STONE_BRICK_DOUBLE_SLAB, BlockMossyStoneBrickDoubleSlab.class);
        register0(MOSSY_STONE_BRICK_SLAB, BlockMossyStoneBrickSlab.class);
        register0(MOSSY_STONE_BRICKS, BlockMossyStoneBricks.class);
        register0(MOSSY_STONE_BRICK_STAIRS, BlockMossyStoneBrickStairs.class);
        register0(MOSSY_STONE_BRICK_WALL, BlockMossyStoneBrickWall.class);
        register0(MOVING_BLOCK, BlockMovingBlock.class);
        register0(MUD, BlockMud.class);
        register0(MUD_BRICK_DOUBLE_SLAB, BlockMudBrickDoubleSlab.class);
        register0(MUD_BRICK_SLAB, BlockMudBrickSlab.class);
        register0(MUD_BRICK_STAIRS, BlockMudBrickStairs.class);
        register0(MUD_BRICK_WALL, BlockMudBrickWall.class);
        register0(MUD_BRICKS, BlockMudBricks.class);
        register0(MUDDY_MANGROVE_ROOTS, BlockMuddyMangroveRoots.class);
        register0(MUSHROOM_STEM, BlockMushroomStem.class);
        register0(MYCELIUM, BlockMycelium.class);
        register0(NETHER_BRICK, BlockNetherBrick.class);
        register0(NETHER_BRICK_DOUBLE_SLAB, BlockNetherBrickDoubleSlab.class);
        register0(NETHER_BRICK_FENCE, BlockNetherBrickFence.class);
        register0(NETHER_BRICK_SLAB, BlockNetherBrickSlab.class);
        register0(NETHER_BRICK_STAIRS, BlockNetherBrickStairs.class);
        register0(NETHER_BRICK_WALL, BlockNetherBrickWall.class);
        register0(NETHER_GOLD_ORE, BlockNetherGoldOre.class);
        register0(NETHER_SPROUTS, BlockNetherSprouts.class);
        register0(NETHER_WART, BlockNetherWart.class);
        register0(NETHER_WART_BLOCK, BlockNetherWartBlock.class);
        register0(NETHERITE_BLOCK, BlockNetheriteBlock.class);
        register0(NETHERRACK, BlockNetherrack.class);
        register0(NETHERREACTOR, BlockNetherreactor.class);
        register0(NORMAL_STONE_SLAB, BlockNormalStoneSlab.class);
        register0(NORMAL_STONE_DOUBLE_SLAB, BlockNormalStoneDoubleSlab.class);
        register0(NORMAL_STONE_STAIRS, BlockNormalStoneStairs.class);
        register0(NOTEBLOCK, BlockNoteblock.class);
        register0(OAK_DOUBLE_SLAB, BlockOakDoubleSlab.class);
        register0(OAK_FENCE, BlockOakFence.class);
        register0(OAK_HANGING_SIGN, BlockOakHangingSign.class);
        register0(OAK_LEAVES, BlockOakLeaves.class);
        register0(OAK_LOG, BlockOakLog.class);
        register0(OAK_PLANKS, BlockOakPlanks.class);
        register0(OAK_SAPLING, BlockOakSapling.class);
        register0(OAK_SLAB, BlockOakSlab.class);
        register0(OAK_STAIRS, BlockOakStairs.class);
        register0(OAK_WOOD, BlockOakWood.class);
        register0(OBSERVER, BlockObserver.class);
        register0(OBSIDIAN, BlockObsidian.class);
        register0(OCHRE_FROGLIGHT, BlockOchreFroglight.class);
        register0(OPEN_EYEBLOSSOM, BlockOpenEyeblossom.class);
        register0(ORANGE_CANDLE, BlockOrangeCandle.class);
        register0(ORANGE_CANDLE_CAKE, BlockOrangeCandleCake.class);
        register0(ORANGE_CARPET, BlockOrangeCarpet.class);
        register0(ORANGE_CONCRETE, BlockOrangeConcrete.class);
        register0(ORANGE_CONCRETE_POWDER, BlockOrangeConcretePowder.class);
        register0(ORANGE_GLAZED_TERRACOTTA, BlockOrangeGlazedTerracotta.class);
        register0(ORANGE_SHULKER_BOX, BlockOrangeShulkerBox.class);
        register0(ORANGE_STAINED_GLASS, BlockOrangeStainedGlass.class);
        register0(ORANGE_STAINED_GLASS_PANE, BlockOrangeStainedGlassPane.class);
        register0(ORANGE_TERRACOTTA, BlockOrangeTerracotta.class);
        register0(ORANGE_TULIP, BlockOrangeTulip.class);
        register0(ORANGE_WOOL, BlockOrangeWool.class);
        register0(OXEYE_DAISY, BlockOxeyeDaisy.class);
        register0(OXIDIZED_CHISELED_COPPER, BlockOxidizedChiseledCopper.class);
        register0(OXIDIZED_COPPER, BlockOxidizedCopper.class);
        register0(OXIDIZED_COPPER_BULB, BlockOxidizedCopperBulb.class);
        register0(OXIDIZED_COPPER_DOOR, BlockOxidizedCopperDoor.class);
        register0(OXIDIZED_COPPER_GRATE, BlockOxidizedCopperGrate.class);
        register0(OXIDIZED_COPPER_TRAPDOOR, BlockOxidizedCopperTrapdoor.class);
        register0(OXIDIZED_CUT_COPPER, BlockOxidizedCutCopper.class);
        register0(OXIDIZED_CUT_COPPER_SLAB, BlockOxidizedCutCopperSlab.class);
        register0(OXIDIZED_CUT_COPPER_STAIRS, BlockOxidizedCutCopperStairs.class);
        register0(OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockOxidizedDoubleCutCopperSlab.class);
        register0(PACKED_ICE, BlockPackedIce.class);
        register0(PACKED_MUD, BlockPackedMud.class);
        register0(PALE_HANGING_MOSS, BlockPaleHangingMoss.class);
        register0(PALE_MOSS_BLOCK, BlockPaleMossBlock.class);
        register0(PALE_MOSS_CARPET, BlockPaleMossCarpet.class);
        register0(PALE_OAK_BUTTON, BlockPaleOakButton.class);
        register0(PALE_OAK_DOOR, BlockPaleOakDoor.class);
        register0(PALE_OAK_FENCE, BlockPaleOakFence.class);
        register0(PALE_OAK_FENCE_GATE, BlockPaleOakFenceGate.class);
        register0(PALE_OAK_HANGING_SIGN, BlockPaleOakHangingSign.class);
        register0(PALE_OAK_LEAVES, BlockPaleOakLeaves.class);
        register0(PALE_OAK_LOG, BlockPaleOakLog.class);
        register0(PALE_OAK_DOUBLE_SLAB, BlockPaleOakDoubleSlab.class);
        register0(PALE_OAK_PLANKS, BlockPaleOakPlanks.class);
        register0(PALE_OAK_PRESSURE_PLATE, BlockPaleOakPressurePlate.class);
        register0(PALE_OAK_SAPLING, BlockPaleOakSapling.class);
        register0(PALE_OAK_SLAB, BlockPaleOakSlab.class);
        register0(PALE_OAK_STAIRS, BlockPaleOakStairs.class);
        register0(PALE_OAK_STANDING_SIGN, BlockPaleOakStandingSign.class);
        register0(PALE_OAK_TRAPDOOR, BlockPaleOakTrapdoor.class);
        register0(PALE_OAK_WALL_SIGN, BlockPaleOakWallSign.class);
        register0(PALE_OAK_WOOD, BlockPaleOakWood.class);
        register0(PEARLESCENT_FROGLIGHT, BlockPearlescentFroglight.class);
        register0(PEONY, BlockPeony.class);
        register0(PETRIFIED_OAK_DOUBLE_SLAB, BlockPetrifiedOakDoubleSlab.class);
        register0(PETRIFIED_OAK_SLAB, BlockPetrifiedOakSlab.class);
        register0(PIGLIN_HEAD, BlockPiglinHead.class);
        register0(PINK_CANDLE, BlockPinkCandle.class);
        register0(PINK_CANDLE_CAKE, BlockPinkCandleCake.class);
        register0(PINK_CARPET, BlockPinkCarpet.class);
        register0(PINK_CONCRETE, BlockPinkConcrete.class);
        register0(PINK_CONCRETE_POWDER, BlockPinkConcretePowder.class);
        register0(PINK_GLAZED_TERRACOTTA, BlockPinkGlazedTerracotta.class);
        register0(PINK_PETALS, BlockPinkPetals.class);
        register0(PINK_SHULKER_BOX, BlockPinkShulkerBox.class);
        register0(PINK_STAINED_GLASS, BlockPinkStainedGlass.class);
        register0(PINK_STAINED_GLASS_PANE, BlockPinkStainedGlassPane.class);
        register0(PINK_TERRACOTTA, BlockPinkTerracotta.class);
        register0(PINK_TULIP, BlockPinkTulip.class);
        register0(PINK_WOOL, BlockPinkWool.class);
        register0(PISTON, BlockPiston.class);
        register0(PISTON_ARM_COLLISION, BlockPistonArmCollision.class);
        register0(PITCHER_CROP, BlockPitcherCrop.class);
        register0(PITCHER_PLANT, BlockPitcherPlant.class);
        register0(PLAYER_HEAD, BlockPlayerHead.class);
        register0(PODZOL, BlockPodzol.class);
        register0(POINTED_DRIPSTONE, BlockPointedDripstone.class);
        register0(POLISHED_ANDESITE, BlockPolishedAndesite.class);
        register0(POLISHED_ANDESITE_DOUBLE_SLAB, BlockPolishedAndesiteDoubleSlab.class);
        register0(POLISHED_ANDESITE_SLAB, BlockPolishedAndesiteSlab.class);
        register0(POLISHED_ANDESITE_STAIRS, BlockPolishedAndesiteStairs.class);
        register0(POLISHED_BASALT, BlockPolishedBasalt.class);
        register0(POLISHED_BLACKSTONE, BlockPolishedBlackstone.class);
        register0(POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, BlockPolishedBlackstoneBrickDoubleSlab.class);
        register0(POLISHED_BLACKSTONE_BRICK_SLAB, BlockPolishedBlackstoneBrickSlab.class);
        register0(POLISHED_BLACKSTONE_BRICK_STAIRS, BlockPolishedBlackstoneBrickStairs.class);
        register0(POLISHED_BLACKSTONE_BRICK_WALL, BlockPolishedBlackstoneBrickWall.class);
        register0(POLISHED_BLACKSTONE_BRICKS, BlockPolishedBlackstoneBricks.class);
        register0(POLISHED_BLACKSTONE_BUTTON, BlockPolishedBlackstoneButton.class);
        register0(POLISHED_BLACKSTONE_DOUBLE_SLAB, BlockPolishedBlackstoneDoubleSlab.class);
        register0(POLISHED_BLACKSTONE_PRESSURE_PLATE, BlockPolishedBlackstonePressurePlate.class);
        register0(POLISHED_BLACKSTONE_SLAB, BlockPolishedBlackstoneSlab.class);
        register0(POLISHED_BLACKSTONE_STAIRS, BlockPolishedBlackstoneStairs.class);
        register0(POLISHED_BLACKSTONE_WALL, BlockPolishedBlackstoneWall.class);
        register0(POLISHED_DEEPSLATE, BlockPolishedDeepslate.class);
        register0(POLISHED_DEEPSLATE_DOUBLE_SLAB, BlockPolishedDeepslateDoubleSlab.class);
        register0(POLISHED_DEEPSLATE_SLAB, BlockPolishedDeepslateSlab.class);
        register0(POLISHED_DEEPSLATE_STAIRS, BlockPolishedDeepslateStairs.class);
        register0(POLISHED_DEEPSLATE_WALL, BlockPolishedDeepslateWall.class);
        register0(POLISHED_DIORITE, BlockPolishedDiorite.class);
        register0(POLISHED_DIORITE_DOUBLE_SLAB, BlockPolishedDioriteDoubleSlab.class);
        register0(POLISHED_DIORITE_SLAB, BlockPolishedDioriteSlab.class);
        register0(POLISHED_DIORITE_STAIRS, BlockPolishedDioriteStairs.class);
        register0(POLISHED_GRANITE, BlockPolishedGranite.class);
        register0(POLISHED_GRANITE_DOUBLE_SLAB, BlockPolishedGraniteDoubleSlab.class);
        register0(POLISHED_GRANITE_SLAB, BlockPolishedGraniteSlab.class);
        register0(POLISHED_GRANITE_STAIRS, BlockPolishedGraniteStairs.class);
        register0(POLISHED_TUFF, BlockPolishedTuff.class);
        register0(POLISHED_TUFF_DOUBLE_SLAB, BlockPolishedTuffDoubleSlab.class);
        register0(POLISHED_TUFF_SLAB, BlockPolishedTuffSlab.class);
        register0(POLISHED_TUFF_STAIRS, BlockPolishedTuffStairs.class);
        register0(POLISHED_TUFF_WALL, BlockPolishedTuffWall.class);
        register0(POPPY, BlockPoppy.class);
        register0(PORTAL, BlockPortal.class);
        register0(POTATOES, BlockPotatoes.class);
        register0(POWDER_SNOW, BlockPowderSnow.class);
        register0(POWERED_COMPARATOR, BlockPoweredComparator.class);
        register0(POWERED_REPEATER, BlockPoweredRepeater.class);
        register0(PRISMARINE, BlockPrismarine.class);
        register0(PRISMARINE_BRICK_DOUBLE_SLAB, BlockPrismarineBrickDoubleSlab.class);
        register0(PRISMARINE_BRICK_SLAB, BlockPrismarineBrickSlab.class);
        register0(PRISMARINE_BRICKS, BlockPrismarineBricks.class);
        register0(PRISMARINE_BRICKS_STAIRS, BlockPrismarineBricksStairs.class);
        register0(PRISMARINE_DOUBLE_SLAB, BlockPrismarineDoubleSlab.class);
        register0(PRISMARINE_SLAB, BlockPrismarineSlab.class);
        register0(PRISMARINE_STAIRS, BlockPrismarineStairs.class);
        register0(PRISMARINE_WALL, BlockPrismarineWall.class);
        register0(PUMPKIN, BlockPumpkin.class);
        register0(PUMPKIN_STEM, BlockPumpkinStem.class);
        register0(PURPLE_CANDLE, BlockPurpleCandle.class);
        register0(PURPLE_CANDLE_CAKE, BlockPurpleCandleCake.class);
        register0(PURPLE_CARPET, BlockPurpleCarpet.class);
        register0(PURPLE_CONCRETE, BlockPurpleConcrete.class);
        register0(PURPLE_CONCRETE_POWDER, BlockPurpleConcretePowder.class);
        register0(PURPLE_GLAZED_TERRACOTTA, BlockPurpleGlazedTerracotta.class);
        register0(PURPLE_SHULKER_BOX, BlockPurpleShulkerBox.class);
        register0(PURPLE_STAINED_GLASS, BlockPurpleStainedGlass.class);
        register0(PURPLE_STAINED_GLASS_PANE, BlockPurpleStainedGlassPane.class);
        register0(PURPLE_TERRACOTTA, BlockPurpleTerracotta.class);
        register0(PURPLE_WOOL, BlockPurpleWool.class);
        register0(PURPUR_BLOCK, BlockPurpurBlock.class);
        register0(PURPUR_DOUBLE_SLAB, BlockPurpurDoubleSlab.class);
        register0(PURPUR_PILLAR, BlockPurpurPillar.class);
        register0(PURPUR_SLAB, BlockPurpurSlab.class);
        register0(PURPUR_STAIRS, BlockPurpurStairs.class);
        register0(QUARTZ_BLOCK, BlockQuartzBlock.class);
        register0(QUARTZ_BRICKS, BlockQuartzBricks.class);
        register0(QUARTZ_DOUBLE_SLAB, BlockQuartzDoubleSlab.class);
        register0(QUARTZ_ORE, BlockQuartzOre.class);
        register0(QUARTZ_PILLAR, BlockQuartzPillar.class);
        register0(QUARTZ_SLAB, BlockQuartzSlab.class);
        register0(QUARTZ_STAIRS, BlockQuartzStairs.class);
        register0(RAIL, BlockRail.class);
        register0(RAW_COPPER_BLOCK, BlockRawCopperBlock.class);
        register0(RAW_GOLD_BLOCK, BlockRawGoldBlock.class);
        register0(RAW_IRON_BLOCK, BlockRawIronBlock.class);
        register0(RED_CANDLE, BlockRedCandle.class);
        register0(RED_CANDLE_CAKE, BlockRedCandleCake.class);
        register0(RED_CARPET, BlockRedCarpet.class);
        register0(RED_CONCRETE, BlockRedConcrete.class);
        register0(RED_CONCRETE_POWDER, BlockRedConcretePowder.class);
        register0(RED_GLAZED_TERRACOTTA, BlockRedGlazedTerracotta.class);
        register0(RED_MUSHROOM, BlockRedMushroom.class);
        register0(RED_MUSHROOM_BLOCK, BlockRedMushroomBlock.class);
        register0(RED_NETHER_BRICK, BlockRedNetherBrick.class);
        register0(RED_NETHER_BRICK_DOUBLE_SLAB, BlockRedNetherBrickDoubleSlab.class);
        register0(RED_NETHER_BRICK_SLAB, BlockRedNetherBrickSlab.class);
        register0(RED_NETHER_BRICK_STAIRS, BlockRedNetherBrickStairs.class);
        register0(RED_NETHER_BRICK_WALL, BlockRedNetherBrickWall.class);
        register0(RED_SAND, BlockRedSand.class);
        register0(RED_SANDSTONE, BlockRedSandstone.class);
        register0(RED_SANDSTONE_DOUBLE_SLAB, BlockRedSandstoneDoubleSlab.class);
        register0(RED_SANDSTONE_SLAB, BlockRedSandstoneSlab.class);
        register0(RED_SANDSTONE_STAIRS, BlockRedSandstoneStairs.class);
        register0(RED_SANDSTONE_WALL, BlockRedSandstoneWall.class);
        register0(RED_SHULKER_BOX, BlockRedShulkerBox.class);
        register0(RED_STAINED_GLASS, BlockRedStainedGlass.class);
        register0(RED_STAINED_GLASS_PANE, BlockRedStainedGlassPane.class);
        register0(RED_TERRACOTTA, BlockRedTerracotta.class);
        register0(RED_TULIP, BlockRedTulip.class);
        register0(RED_WOOL, BlockRedWool.class);
        register0(REDSTONE_BLOCK, BlockRedstoneBlock.class);
        register0(REDSTONE_LAMP, BlockRedstoneLamp.class);
        register0(REDSTONE_ORE, BlockRedstoneOre.class);
        register0(REDSTONE_TORCH, BlockRedstoneTorch.class);
        register0(REDSTONE_WIRE, BlockRedstoneWire.class);
        register0(REEDS, BlockReeds.class);
        register0(REINFORCED_DEEPSLATE, BlockReinforcedDeepslate.class);
        register0(REPEATING_COMMAND_BLOCK, BlockRepeatingCommandBlock.class);
        register0(RESERVED6, BlockReserved6.class);
        register0(RESIN_BLOCK, BlockResin.class);
        register0(RESIN_BRICKS, BlockResinBricks.class);
        register0(RESIN_BRICK_DOUBLE_SLAB, BlockResinBrickDoubleSlab.class);
        register0(RESIN_BRICK_SLAB, BlockResinBrickSlab.class);
        register0(RESIN_BRICK_STAIRS, BlockResinBrickStairs.class);
        register0(RESIN_BRICK_WALL, BlockResinBrickWall.class);
        register0(RESIN_CLUMP, BlockResinClump.class);
        register0(RESPAWN_ANCHOR, BlockRespawnAnchor.class);
        register0(ROSE_BUSH, BlockRoseBush.class);
        register0(SAND, BlockSand.class);
        register0(SANDSTONE, BlockSandstone.class);
        register0(SANDSTONE_DOUBLE_SLAB, BlockSandstoneDoubleSlab.class);
        register0(SANDSTONE_SLAB, BlockSandstoneSlab.class);
        register0(SANDSTONE_STAIRS, BlockSandstoneStairs.class);
        register0(SANDSTONE_WALL, BlockSandstoneWall.class);
        register0(SCAFFOLDING, BlockScaffolding.class);
        register0(SCULK, BlockSculk.class);
        register0(SCULK_CATALYST, BlockSculkCatalyst.class);
        register0(SCULK_SENSOR, BlockSculkSensor.class);
        register0(SCULK_SHRIEKER, BlockSculkShrieker.class);
        register0(SCULK_VEIN, BlockSculkVein.class);
        register0(SEA_LANTERN, BlockSeaLantern.class);
        register0(SEA_PICKLE, BlockSeaPickle.class);
        register0(SEAGRASS, BlockSeagrass.class);
        register0(SHORT_DRY_GRASS, BlockShortDryGrass.class);
        register0(SHORT_GRASS, BlockShortGrass.class);
        register0(SHROOMLIGHT, BlockShroomlight.class);
        register0(SILVER_GLAZED_TERRACOTTA, BlockSilverGlazedTerracotta.class);
        register0(SKELETON_SKULL, BlockSkeletonSkull.class);
        register0(SLIME, BlockSlime.class);
        register0(SMALL_AMETHYST_BUD, BlockSmallAmethystBud.class);
        register0(SMALL_DRIPLEAF_BLOCK, BlockSmallDripleafBlock.class);
        register0(SMITHING_TABLE, BlockSmithingTable.class);
        register0(SMOKER, BlockSmoker.class);
        register0(SMOOTH_BASALT, BlockSmoothBasalt.class);
        register0(SMOOTH_QUARTZ, BlockSmoothQuartz.class);
        register0(SMOOTH_QUARTZ_DOUBLE_SLAB, BlockSmoothQuartzDoubleSlab.class);
        register0(SMOOTH_QUARTZ_SLAB, BlockSmoothQuartzSlab.class);
        register0(SMOOTH_QUARTZ_STAIRS, BlockSmoothQuartzStairs.class);
        register0(SMOOTH_RED_SANDSTONE, BlockSmoothRedSandstone.class);
        register0(SMOOTH_RED_SANDSTONE_DOUBLE_SLAB, BlockSmoothRedSandstoneDoubleSlab.class);
        register0(SMOOTH_RED_SANDSTONE_SLAB, BlockSmoothRedSandstoneSlab.class);
        register0(SMOOTH_RED_SANDSTONE_STAIRS, BlockSmoothRedSandstoneStairs.class);
        register0(SMOOTH_SANDSTONE, BlockSmoothSandstone.class);
        register0(SMOOTH_SANDSTONE_DOUBLE_SLAB, BlockSmoothSandstoneDoubleSlab.class);
        register0(SMOOTH_SANDSTONE_SLAB, BlockSmoothSandstoneSlab.class);
        register0(SMOOTH_SANDSTONE_STAIRS, BlockSmoothSandstoneStairs.class);
        register0(SMOOTH_STONE, BlockSmoothStone.class);
        register0(SMOOTH_STONE_DOUBLE_SLAB, BlockSmoothStoneDoubleSlab.class);
        register0(SMOOTH_STONE_SLAB, BlockSmoothStoneSlab.class);
        register0(SNIFFER_EGG, BlockSnifferEgg.class);
        register0(SNOW, BlockSnow.class);
        register0(SNOW_LAYER, BlockSnowLayer.class);
        register0(SOUL_CAMPFIRE, BlockSoulCampfire.class);
        register0(SOUL_FIRE, BlockSoulFire.class);
        register0(SOUL_LANTERN, BlockSoulLantern.class);
        register0(SOUL_SAND, BlockSoulSand.class);
        register0(SOUL_SOIL, BlockSoulSoil.class);
        register0(SOUL_TORCH, BlockSoulTorch.class);
        register0(SPONGE, BlockSponge.class);
        register0(SPORE_BLOSSOM, BlockSporeBlossom.class);
        register0(SPRUCE_BUTTON, BlockSpruceButton.class);
        register0(SPRUCE_DOOR, BlockSpruceDoor.class);
        register0(SPRUCE_DOUBLE_SLAB, BlockSpruceDoubleSlab.class);
        register0(SPRUCE_FENCE, BlockSpruceFence.class);
        register0(SPRUCE_FENCE_GATE, BlockSpruceFenceGate.class);
        register0(SPRUCE_HANGING_SIGN, BlockSpruceHangingSign.class);
        register0(SPRUCE_LEAVES, BlockSpruceLeaves.class);
        register0(SPRUCE_LOG, BlockSpruceLog.class);
        register0(SPRUCE_PLANKS, BlockSprucePlanks.class);
        register0(SPRUCE_PRESSURE_PLATE, BlockSprucePressurePlate.class);
        register0(SPRUCE_SAPLING, BlockSpruceSapling.class);
        register0(SPRUCE_SLAB, BlockSpruceSlab.class);
        register0(SPRUCE_STAIRS, BlockSpruceStairs.class);
        register0(SPRUCE_STANDING_SIGN, BlockSpruceStandingSign.class);
        register0(SPRUCE_TRAPDOOR, BlockSpruceTrapdoor.class);
        register0(SPRUCE_WALL_SIGN, BlockSpruceWallSign.class);
        register0(SPRUCE_WOOD, BlockSpruceWood.class);
        register0(STANDING_BANNER, BlockStandingBanner.class);
        register0(STANDING_SIGN, BlockStandingSign.class);
        register0(STICKY_PISTON, BlockStickyPiston.class);
        register0(STICKY_PISTON_ARM_COLLISION, BlockStickyPistonArmCollision.class);
        register0(STONE, BlockStone.class);
        register0(STONE_BRICK_DOUBLE_SLAB, BlockStoneBrickDoubleSlab.class);
        register0(STONE_BRICK_SLAB, BlockStoneBrickSlab.class);
        register0(STONE_BRICK_STAIRS, BlockStoneBrickStairs.class);
        register0(STONE_BRICK_WALL, BlockStoneBrickWall.class);
        register0(STONE_BUTTON, BlockStoneButton.class);
        register0(STONE_PRESSURE_PLATE, BlockStonePressurePlate.class);
        register0(STONE_STAIRS, BlockStoneStairs.class);
        register0(STONE_BRICKS, BlockStoneBricks.class);
        register0(STONECUTTER, BlockStonecutter.class);
        register0(STONECUTTER_BLOCK, BlockStonecutterBlock.class);
        register0(STRIPPED_ACACIA_LOG, BlockStrippedAcaciaLog.class);
        register0(STRIPPED_ACACIA_WOOD, BlockStrippedAcaciaWood.class);
        register0(STRIPPED_BAMBOO_BLOCK, BlockStrippedBambooBlock.class);
        register0(STRIPPED_BIRCH_LOG, BlockStrippedBirchLog.class);
        register0(STRIPPED_BIRCH_WOOD, BlockStrippedBirchWood.class);
        register0(STRIPPED_CHERRY_LOG, BlockStrippedCherryLog.class);
        register0(STRIPPED_CHERRY_WOOD, BlockStrippedCherryWood.class);
        register0(STRIPPED_CRIMSON_HYPHAE, BlockStrippedCrimsonHyphae.class);
        register0(STRIPPED_CRIMSON_STEM, BlockStrippedCrimsonStem.class);
        register0(STRIPPED_DARK_OAK_LOG, BlockStrippedDarkOakLog.class);
        register0(STRIPPED_DARK_OAK_WOOD, BlockStrippedDarkOakWood.class);
        register0(STRIPPED_JUNGLE_LOG, BlockStrippedJungleLog.class);
        register0(STRIPPED_JUNGLE_WOOD, BlockStrippedJungleWood.class);
        register0(STRIPPED_MANGROVE_LOG, BlockStrippedMangroveLog.class);
        register0(STRIPPED_MANGROVE_WOOD, BlockStrippedMangroveWood.class);
        register0(STRIPPED_OAK_LOG, BlockStrippedOakLog.class);
        register0(STRIPPED_OAK_WOOD, BlockStrippedOakWood.class);
        register0(STRIPPED_PALE_OAK_LOG, BlockStrippedPaleOakLog.class);
        register0(STRIPPED_PALE_OAK_WOOD, BlockStrippedPaleOakWood.class);
        register0(STRIPPED_SPRUCE_LOG, BlockStrippedSpruceLog.class);
        register0(STRIPPED_SPRUCE_WOOD, BlockStrippedSpruceWood.class);
        register0(STRIPPED_WARPED_HYPHAE, BlockStrippedWarpedHyphae.class);
        register0(STRIPPED_WARPED_STEM, BlockStrippedWarpedStem.class);
        register0(STRUCTURE_BLOCK, BlockStructureBlock.class);
        register0(STRUCTURE_VOID, BlockStructureVoid.class);
        register0(SUNFLOWER, BlockSunflower.class);
        register0(SUSPICIOUS_GRAVEL, BlockSuspiciousGravel.class);
        register0(SUSPICIOUS_SAND, BlockSuspiciousSand.class);
        register0(SWEET_BERRY_BUSH, BlockSweetBerryBush.class);
        register0(TALL_GRASS, BlockTallGrass.class);
        register0(TALL_DRY_GRASS, BlockTallDryGrass.class);
        register0(TARGET, BlockTarget.class);
        register0(TINTED_GLASS, BlockTintedGlass.class);
        register0(TNT, BlockTnt.class);
        register0(TORCH, BlockTorch.class);
        register0(TORCHFLOWER, BlockTorchflower.class);
        register0(TORCHFLOWER_CROP, BlockTorchflowerCrop.class);
        register0(TRAPDOOR, BlockTrapdoor.class);
        register0(TRAPPED_CHEST, BlockTrappedChest.class);
        register0(TRIAL_SPAWNER, BlockTrialSpawner.class);
        register0(TRIP_WIRE, BlockTripWire.class);
        register0(TRIPWIRE_HOOK, BlockTripwireHook.class);
        register0(TUBE_CORAL, BlockTubeCoral.class);
        register0(TUBE_CORAL_BLOCK, BlockTubeCoralBlock.class);
        register0(TUBE_CORAL_FAN, BlockTubeCoralFan.class);
        register0(TUBE_CORAL_WALL_FAN, BlockTubeCoralWallFan.class);
        register0(TUFF, BlockTuff.class);
        register0(TUFF_BRICK_DOUBLE_SLAB, BlockTuffBrickDoubleSlab.class);
        register0(TUFF_BRICK_SLAB, BlockTuffBrickSlab.class);
        register0(TUFF_BRICK_STAIRS, BlockTuffBrickStairs.class);
        register0(TUFF_BRICK_WALL, BlockTuffBrickWall.class);
        register0(TUFF_BRICKS, BlockTuffBricks.class);
        register0(TUFF_DOUBLE_SLAB, BlockTuffDoubleSlab.class);
        register0(TUFF_SLAB, BlockTuffSlab.class);
        register0(TUFF_STAIRS, BlockTuffStairs.class);
        register0(TUFF_WALL, BlockTuffWall.class);
        register0(TURTLE_EGG, BlockTurtleEgg.class);
        register0(TWISTING_VINES, BlockTwistingVines.class);
        register0(UNDYED_SHULKER_BOX, BlockUndyedShulkerBox.class);
        register0(UNKNOWN, BlockUnknown.class);
        register0(UNLIT_REDSTONE_TORCH, BlockUnlitRedstoneTorch.class);
        register0(UNPOWERED_COMPARATOR, BlockUnpoweredComparator.class);
        register0(UNPOWERED_REPEATER, BlockUnpoweredRepeater.class);
        register0(VAULT, BlockVault.class);
        register0(VERDANT_FROGLIGHT, BlockVerdantFroglight.class);
        register0(VINE, BlockVine.class);
        register0(WALL_BANNER, BlockWallBanner.class);
        register0(WALL_SIGN, BlockWallSign.class);
        register0(WARPED_BUTTON, BlockWarpedButton.class);
        register0(WARPED_DOOR, BlockWarpedDoor.class);
        register0(WARPED_DOUBLE_SLAB, BlockWarpedDoubleSlab.class);
        register0(WARPED_FENCE, BlockWarpedFence.class);
        register0(WARPED_FENCE_GATE, BlockWarpedFenceGate.class);
        register0(WARPED_FUNGUS, BlockWarpedFungus.class);
        register0(WARPED_HANGING_SIGN, BlockWarpedHangingSign.class);
        register0(WARPED_HYPHAE, BlockWarpedHyphae.class);
        register0(WARPED_NYLIUM, BlockWarpedNylium.class);
        register0(WARPED_PLANKS, BlockWarpedPlanks.class);
        register0(WARPED_PRESSURE_PLATE, BlockWarpedPressurePlate.class);
        register0(WARPED_ROOTS, BlockWarpedRoots.class);
        register0(WARPED_SLAB, BlockWarpedSlab.class);
        register0(WARPED_STAIRS, BlockWarpedStairs.class);
        register0(WARPED_STANDING_SIGN, BlockWarpedStandingSign.class);
        register0(WARPED_STEM, BlockWarpedStem.class);
        register0(WARPED_TRAPDOOR, BlockWarpedTrapdoor.class);
        register0(WARPED_WALL_SIGN, BlockWarpedWallSign.class);
        register0(WARPED_WART_BLOCK, BlockWarpedWartBlock.class);
        register0(WATER, BlockWater.class);
        register0(WATERLILY, BlockWaterlily.class);
        register0(WAXED_CHISELED_COPPER, BlockWaxedChiseledCopper.class);
        register0(WAXED_COPPER, BlockWaxedCopper.class);
        register0(WAXED_COPPER_BULB, BlockWaxedCopperBulb.class);
        register0(WAXED_COPPER_DOOR, BlockWaxedCopperDoor.class);
        register0(WAXED_COPPER_GRATE, BlockWaxedCopperGrate.class);
        register0(WAXED_COPPER_TRAPDOOR, BlockWaxedCopperTrapdoor.class);
        register0(WAXED_CUT_COPPER, BlockWaxedCutCopper.class);
        register0(WAXED_CUT_COPPER_SLAB, BlockWaxedCutCopperSlab.class);
        register0(WAXED_CUT_COPPER_STAIRS, BlockWaxedCutCopperStairs.class);
        register0(WAXED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedDoubleCutCopperSlab.class);
        register0(WAXED_EXPOSED_CHISELED_COPPER, BlockWaxedExposedChiseledCopper.class);
        register0(WAXED_EXPOSED_COPPER, BlockWaxedExposedCopper.class);
        register0(WAXED_EXPOSED_COPPER_BULB, BlockWaxedExposedCopperBulb.class);
        register0(WAXED_EXPOSED_COPPER_DOOR, BlockWaxedExposedCopperDoor.class);
        register0(WAXED_EXPOSED_COPPER_GRATE, BlockWaxedExposedCopperGrate.class);
        register0(WAXED_EXPOSED_COPPER_TRAPDOOR, BlockWaxedExposedCopperTrapdoor.class);
        register0(WAXED_EXPOSED_CUT_COPPER, BlockWaxedExposedCutCopper.class);
        register0(WAXED_EXPOSED_CUT_COPPER_SLAB, BlockWaxedExposedCutCopperSlab.class);
        register0(WAXED_EXPOSED_CUT_COPPER_STAIRS, BlockWaxedExposedCutCopperStairs.class);
        register0(WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedExposedDoubleCutCopperSlab.class);
        register0(WAXED_OXIDIZED_CHISELED_COPPER, BlockWaxedOxidizedChiseledCopper.class);
        register0(WAXED_OXIDIZED_COPPER, BlockWaxedOxidizedCopper.class);
        register0(WAXED_OXIDIZED_COPPER_BULB, BlockWaxedOxidizedCopperBulb.class);
        register0(WAXED_OXIDIZED_COPPER_DOOR, BlockWaxedOxidizedCopperDoor.class);
        register0(WAXED_OXIDIZED_COPPER_GRATE, BlockWaxedOxidizedCopperGrate.class);
        register0(WAXED_OXIDIZED_COPPER_TRAPDOOR, BlockWaxedOxidizedCopperTrapdoor.class);
        register0(WAXED_OXIDIZED_CUT_COPPER, BlockWaxedOxidizedCutCopper.class);
        register0(WAXED_OXIDIZED_CUT_COPPER_SLAB, BlockWaxedOxidizedCutCopperSlab.class);
        register0(WAXED_OXIDIZED_CUT_COPPER_STAIRS, BlockWaxedOxidizedCutCopperStairs.class);
        register0(WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedOxidizedDoubleCutCopperSlab.class);
        register0(WAXED_WEATHERED_CHISELED_COPPER, BlockWaxedWeatheredChiseledCopper.class);
        register0(WAXED_WEATHERED_COPPER, BlockWaxedWeatheredCopper.class);
        register0(WAXED_WEATHERED_COPPER_BULB, BlockWaxedWeatheredCopperBulb.class);
        register0(WAXED_WEATHERED_COPPER_DOOR, BlockWaxedWeatheredCopperDoor.class);
        register0(WAXED_WEATHERED_COPPER_GRATE, BlockWaxedWeatheredCopperGrate.class);
        register0(WAXED_WEATHERED_COPPER_TRAPDOOR, BlockWaxedWeatheredCopperTrapdoor.class);
        register0(WAXED_WEATHERED_CUT_COPPER, BlockWaxedWeatheredCutCopper.class);
        register0(WAXED_WEATHERED_CUT_COPPER_SLAB, BlockWaxedWeatheredCutCopperSlab.class);
        register0(WAXED_WEATHERED_CUT_COPPER_STAIRS, BlockWaxedWeatheredCutCopperStairs.class);
        register0(WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedWeatheredDoubleCutCopperSlab.class);
        register0(WEATHERED_CHISELED_COPPER, BlockWeatheredChiseledCopper.class);
        register0(WEATHERED_COPPER, BlockWeatheredCopper.class);
        register0(WEATHERED_COPPER_BULB, BlockWeatheredCopperBulb.class);
        register0(WEATHERED_COPPER_DOOR, BlockWeatheredCopperDoor.class);
        register0(WEATHERED_COPPER_GRATE, BlockWeatheredCopperGrate.class);
        register0(WEATHERED_COPPER_TRAPDOOR, BlockWeatheredCopperTrapdoor.class);
        register0(WEATHERED_CUT_COPPER, BlockWeatheredCutCopper.class);
        register0(WEATHERED_CUT_COPPER_SLAB, BlockWeatheredCutCopperSlab.class);
        register0(WEATHERED_CUT_COPPER_STAIRS, BlockWeatheredCutCopperStairs.class);
        register0(WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockWeatheredDoubleCutCopperSlab.class);
        register0(WEB, BlockWeb.class);
        register0(WEEPING_VINES, BlockWeepingVines.class);
        register0(WET_SPONGE, BlockWetSponge.class);
        register0(WHEAT, BlockWheat.class);
        register0(WHITE_CANDLE, BlockWhiteCandle.class);
        register0(WHITE_CANDLE_CAKE, BlockWhiteCandleCake.class);
        register0(WHITE_CARPET, BlockWhiteCarpet.class);
        register0(WHITE_CONCRETE, BlockWhiteConcrete.class);
        register0(WHITE_CONCRETE_POWDER, BlockWhiteConcretePowder.class);
        register0(WHITE_GLAZED_TERRACOTTA, BlockWhiteGlazedTerracotta.class);
        register0(WHITE_SHULKER_BOX, BlockWhiteShulkerBox.class);
        register0(WHITE_STAINED_GLASS, BlockWhiteStainedGlass.class);
        register0(WHITE_STAINED_GLASS_PANE, BlockWhiteStainedGlassPane.class);
        register0(WHITE_TERRACOTTA, BlockWhiteTerracotta.class);
        register0(WHITE_TULIP, BlockWhiteTulip.class);
        register0(WHITE_WOOL, BlockWhiteWool.class);
        register0(WILDFLOWERS, BlockWildflowers.class);
        register0(WITHER_ROSE, BlockWitherRose.class);
        register0(WITHER_SKELETON_SKULL, BlockWitherSkeletonSkull.class);
        register0(WOODEN_BUTTON, BlockWoodenButton.class);
        register0(WOODEN_DOOR, BlockWoodenDoor.class);
        register0(WOODEN_PRESSURE_PLATE, BlockWoodenPressurePlate.class);
        register0(YELLOW_CANDLE, BlockYellowCandle.class);
        register0(YELLOW_CANDLE_CAKE, BlockYellowCandleCake.class);
        register0(YELLOW_CARPET, BlockYellowCarpet.class);
        register0(YELLOW_CONCRETE, BlockYellowConcrete.class);
        register0(YELLOW_CONCRETE_POWDER, BlockYellowConcretePowder.class);
        register0(DANDELION, BlockDandelion.class);
        register0(YELLOW_GLAZED_TERRACOTTA, BlockYellowGlazedTerracotta.class);
        register0(YELLOW_SHULKER_BOX, BlockYellowShulkerBox.class);
        register0(YELLOW_STAINED_GLASS, BlockYellowStainedGlass.class);
        register0(YELLOW_STAINED_GLASS_PANE, BlockYellowStainedGlassPane.class);
        register0(YELLOW_TERRACOTTA, BlockYellowTerracotta.class);
        register0(YELLOW_WOOL, BlockYellowWool.class);
        register0(ZOMBIE_HEAD, BlockZombieHead.class);
        register0(DRIED_GHAST, BlockDriedGhast.class);

        /**
         * @since 1.21.110
         */

        register0(COPPER_BARS, BlockCopperBars.class);
        register0(EXPOSED_COPPER_BARS, BlockExposedCopperBars.class);
        register0(WEATHERED_COPPER_BARS, BlockWeatheredCopperBars.class);
        register0(OXIDIZED_COPPER_BARS, BlockOxidizedCopperBars.class);
        register0(WAXED_COPPER_BARS, BlockWaxedCopperBars.class);
        register0(WAXED_EXPOSED_COPPER_BARS, BlockWaxedExposedCopperBars.class);
        register0(WAXED_WEATHERED_COPPER_BARS, BlockWaxedWeatheredCopperBars.class);
        register0(WAXED_OXIDIZED_COPPER_BARS, BlockWaxedOxidizedCopperBars.class);

        register0(COPPER_CHAIN, BlockCopperChain.class);
        register0(EXPOSED_COPPER_CHAIN, BlockExposedCopperChain.class);
        register0(WEATHERED_COPPER_CHAIN, BlockWeatheredCopperChain.class);
        register0(OXIDIZED_COPPER_CHAIN, BlockOxidizedCopperChain.class);
        register0(WAXED_COPPER_CHAIN, BlockWaxedCopperChain.class);
        register0(WAXED_EXPOSED_COPPER_CHAIN, BlockWaxedExposedCopperChain.class);
        register0(WAXED_WEATHERED_COPPER_CHAIN, BlockWaxedWeatheredCopperChain.class);
        register0(WAXED_OXIDIZED_COPPER_CHAIN, BlockWaxedOxidizedCopperChain.class);

        register0(COPPER_CHEST, BlockCopperChest.class);
        register0(EXPOSED_COPPER_CHEST, BlockExposedCopperChest.class);
        register0(WEATHERED_COPPER_CHEST, BlockWeatheredCopperChest.class);
        register0(OXIDIZED_COPPER_CHEST, BlockOxidizedCopperChest.class);
        register0(WAXED_COPPER_CHEST, BlockWaxedCopperChest.class);
        register0(WAXED_EXPOSED_COPPER_CHEST, BlockWaxedExposedCopperChest.class);
        register0(WAXED_WEATHERED_COPPER_CHEST, BlockWaxedWeatheredCopperChest.class);
        register0(WAXED_OXIDIZED_COPPER_CHEST, BlockWaxedOxidizedCopperChest.class);

        register0(COPPER_GOLEM_STATUE, BlockCopperGolemStatue.class);
        register0(EXPOSED_COPPER_GOLEM_STATUE, BlockExposedCopperGolemStatue.class);
        register0(WEATHERED_COPPER_GOLEM_STATUE, BlockWeatheredCopperGolemStatue.class);
        register0(OXIDIZED_COPPER_GOLEM_STATUE, BlockOxidizedCopperGolemStatue.class);
        register0(WAXED_COPPER_GOLEM_STATUE, BlockWaxedCopperGolemStatue.class);
        register0(WAXED_EXPOSED_COPPER_GOLEM_STATUE, BlockWaxedExposedCopperGolemStatue.class);
        register0(WAXED_WEATHERED_COPPER_GOLEM_STATUE, BlockWaxedWeatheredCopperGolemStatue.class);
        register0(WAXED_OXIDIZED_COPPER_GOLEM_STATUE, BlockWaxedOxidizedCopperGolemStatue.class);

        register0(COPPER_LANTERN, BlockCopperLantern.class);
        register0(EXPOSED_COPPER_LANTERN, BlockExposedCopperLantern.class);
        register0(WEATHERED_COPPER_LANTERN, BlockWeatheredCopperLantern.class);
        register0(OXIDIZED_COPPER_LANTERN, BlockOxidizedCopperLantern.class);
        register0(WAXED_COPPER_LANTERN, BlockWaxedCopperLantern.class);
        register0(WAXED_EXPOSED_COPPER_LANTERN, BlockWaxedExposedCopperLantern.class);
        register0(WAXED_WEATHERED_COPPER_LANTERN, BlockWaxedWeatheredCopperLantern.class);
        register0(WAXED_OXIDIZED_COPPER_LANTERN, BlockWaxedOxidizedCopperLantern.class);

        register0(COPPER_TORCH, BlockCopperTorch.class);

        register0(LIGHTNING_ROD, BlockLightningRod.class);
        register0(EXPOSED_LIGHTNING_ROD, BlockExposedLightningRod.class);
        register0(WEATHERED_LIGHTNING_ROD, BlockWeatheredLightningRod.class);
        register0(OXIDIZED_LIGHTNING_ROD, BlockOxidizedLightningRod.class);
        register0(WAXED_LIGHTNING_ROD, BlockWaxedLightningRod.class);
        register0(WAXED_EXPOSED_LIGHTNING_ROD, BlockWaxedExposedLightningRod.class);
        register0(WAXED_WEATHERED_LIGHTNING_ROD, BlockWaxedWeatheredLightningRod.class);
        register0(WAXED_OXIDIZED_LIGHTNING_ROD, BlockWaxedOxidizedLightningRod.class);

        register0(OAK_SHELF, BlockOakShelf.class);
        register0(SPRUCE_SHELF, BlockSpruceShelf.class);
        register0(BIRCH_SHELF, BlockBirchShelf.class);
        register0(JUNGLE_SHELF, BlockJungleShelf.class);
        register0(ACACIA_SHELF, BlockAcaciaShelf.class);
        register0(DARK_OAK_SHELF, BlockDarkOakShelf.class);
        register0(MANGROVE_SHELF, BlockMangroveShelf.class);
        register0(CHERRY_SHELF, BlockCherryShelf.class);
        register0(PALE_OAK_SHELF, BlockPaleOakShelf.class);
        register0(CRIMSON_SHELF, BlockCrimsonShelf.class);
        register0(WARPED_SHELF, BlockWarpedShelf.class);
        register0(BAMBOO_SHELF, BlockBambooShelf.class);
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
        if(shouldSkip(key)) return;
        if (Modifier.isAbstract(value.getModifiers())) {
            throw new RegisterException("You can't register0 a abstract block class!");
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
                    throw new RegisterException("This block has already been register0ed with the identifier: " + blockProperties.getIdentifier());
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

    private void register0(String key, Class<? extends Block> value) {
        try {
            register(key, value);
        } catch (RegisterException e) {
            log.error("Failed to register Block: {}", key, e);
        }
    }

    /**
     * Register custom item
     */
    @SafeVarargs
    public final void registerCustomBlock(Plugin plugin, Class<? extends Block>... values) throws RegisterException {
        for (var c : values) {
            registerCustomBlock(plugin, c);
        }
    }

    /**
     * Register custom block
     */
    public void registerCustomBlock(Plugin plugin, Class<? extends Block> value) throws RegisterException {
        if (Modifier.isAbstract(value.getModifiers())) {
            throw new RegisterException("You can't register0 a abstract block class!");
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
                    CustomBlockDefinition def = customBlock.getDefinition();
                    customBlockDefinitions.add(def);
                    CUSTOM_BLOCK_DEFINITION_BY_ID.put(customBlock.getId(), customBlock.getDefinition());
                    int rid = 255 - CustomBlockDefinition.getRuntimeId(customBlock.getId());
                    Registries.ITEM_RUNTIMEID.registerCustomRuntimeItem(new ItemRuntimeIdRegistry.RuntimeEntry(customBlock.getId(), rid, false));
                    CompoundTag nbt = def.nbt();
                    if (Registries.CREATIVE.shouldBeRegisteredBlock(nbt)) {
                        ItemBlock itemBlock = new ItemBlock(customBlock.toBlock());
                        itemBlock.setNetId(null);
                        int groupIndex = Registries.CREATIVE.resolveGroupIndexFromBlockDefinition(key, nbt);
                        Registries.CREATIVE.addCreativeItem(itemBlock, groupIndex);
                    }
                    KEYSET.add(key);
                    PROPERTIES.put(key, blockProperties);
                    blockProperties.getSpecialValueMap().values().forEach(Registries.BLOCKSTATE::registerInternal);
                } else {
                    throw new RegisterException("Register Error: Must implement the CustomBlock interface!");
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

    @UnmodifiableView
    public List<CustomBlockDefinition> getCustomBlockDefinitionList() {
        return CUSTOM_BLOCK_DEFINITIONS.values().stream().flatMap(List::stream).toList();
    }

    public static @Nullable CustomBlockDefinition getCustomBlockDefinitionByIdStatic(String id) {
        return CUSTOM_BLOCK_DEFINITION_BY_ID.get(id);
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
        if (blockState == null)
            return null;

        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());

        if (constructor == null)
            return null;

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

    public static boolean shouldSkip(String bid) {
        return (!Education.isEnabled() && Education.eduBlocks.contains(bid)) || skipBlocks.contains(bid);
    }

    public static CustomBlockDefinition getCustomBlockDefinition(String identifier) {
        return CUSTOM_BLOCK_DEFINITION_BY_ID.get(identifier);
    }
}