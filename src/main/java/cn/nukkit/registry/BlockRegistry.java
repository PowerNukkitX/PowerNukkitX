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
import java.util.concurrent.locks.ReentrantLock;

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

    private static final ReentrantLock lock = new ReentrantLock();

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        register0(TRAPDOOR, BlockTrapdoor.class);// done.
        register0(ACACIA_BUTTON, BlockAcaciaButton.class);// done.
        register0(ACACIA_DOOR, BlockAcaciaDoor.class);// done.
        register0(ACACIA_FENCE, BlockAcaciaFence.class);// done.
        register0(ACACIA_FENCE_GATE, BlockAcaciaFenceGate.class);// done.
        register0(ACACIA_HANGING_SIGN, BlockAcaciaHangingSign.class);// done.
        register0(ACACIA_LOG, BlockAcaciaLog.class);// done.
        register0(ACACIA_PLANKS, BlockAcaciaPlanks.class);// done.
        register0(ACACIA_PRESSURE_PLATE, BlockAcaciaPressurePlate.class);// done.
        register0(ACACIA_STAIRS, BlockAcaciaStairs.class);// done.
        register0(ACACIA_STANDING_SIGN, BlockAcaciaStandingSign.class);// done.
        register0(ACACIA_TRAPDOOR, BlockAcaciaTrapdoor.class);// done.
        register0(ACACIA_WALL_SIGN, BlockAcaciaWallSign.class);// done.
        register0(ACTIVATOR_RAIL, BlockActivatorRail.class);// done.
        register0(AIR, BlockAir.class);// done.
        register0(ALLOW, BlockAllow.class);// done.
        register0(AMETHYST_BLOCK, BlockAmethystBlock.class);// done.
        register0(AMETHYST_CLUSTER, BlockAmethystCluster.class);// done.
        register0(ANCIENT_DEBRIS, BlockAncientDebris.class);// done.
        register0(ANDESITE, BlockAndesite.class);// done.
        register0(ANDESITE_STAIRS, BlockAndesiteStairs.class);// done.
        register0(ANVIL, BlockAnvil.class);// done.
        register0(AZALEA, BlockAzalea.class);// done.
        register0(AZALEA_LEAVES, BlockAzaleaLeaves.class);// done.
        register0(AZALEA_LEAVES_FLOWERED, BlockAzaleaLeavesFlowered.class);// done.
        register0(BAMBOO, BlockBamboo.class);// done.
        register0(BAMBOO_BLOCK, BlockBambooBlock.class);// done.
        register0(BAMBOO_BUTTON, BlockBambooButton.class);// done.
        register0(BAMBOO_DOOR, BlockBambooDoor.class);// done.
        register0(BAMBOO_DOUBLE_SLAB, BlockBambooDoubleSlab.class);// done.
        register0(BAMBOO_FENCE, BlockBambooFence.class);// done.
        register0(BAMBOO_FENCE_GATE, BlockBambooFenceGate.class);// done.
        register0(BAMBOO_HANGING_SIGN, BlockBambooHangingSign.class);// done.
        register0(BAMBOO_MOSAIC, BlockBambooMosaic.class);// done.
        register0(BAMBOO_MOSAIC_DOUBLE_SLAB, BlockBambooMosaicDoubleSlab.class);// done.
        register0(BAMBOO_MOSAIC_SLAB, BlockBambooMosaicSlab.class);// done.
        register0(BAMBOO_MOSAIC_STAIRS, BlockBambooMosaicStairs.class);// done.
        register0(BAMBOO_PLANKS, BlockBambooPlanks.class);// done.
        register0(BAMBOO_PRESSURE_PLATE, BlockBambooPressurePlate.class);// done.
        register0(BAMBOO_SAPLING, BlockBambooSapling.class);// done.
        register0(BAMBOO_SLAB, BlockBambooSlab.class);// done.
        register0(BAMBOO_STAIRS, BlockBambooStairs.class);// done.
        register0(BAMBOO_STANDING_SIGN, BlockBambooStandingSign.class);// done.
        register0(BAMBOO_TRAPDOOR, BlockBambooTrapdoor.class);// done.
        register0(BAMBOO_WALL_SIGN, BlockBambooWallSign.class);// done.
        register0(BARREL, BlockBarrel.class);// done.
        register0(BARRIER, BlockBarrier.class);// done.
        register0(BASALT, BlockBasalt.class);// done.
        register0(BEACON, BlockBeacon.class);// done.
        register0(BED, BlockBed.class);// done.
        register0(BEDROCK, BlockBedrock.class);// done.
        register0(BEE_NEST, BlockBeeNest.class);// done.
        register0(BEEHIVE, BlockBeehive.class);// done.
        register0(BEETROOT, BlockBeetroot.class);// done.
        register0(BELL, BlockBell.class);// done.
        register0(BIG_DRIPLEAF, BlockBigDripleaf.class);// done.
        register0(BIRCH_BUTTON, BlockBirchButton.class);// done.
        register0(BIRCH_DOOR, BlockBirchDoor.class);// done.
        register0(BIRCH_FENCE, BlockBirchFence.class);// done.
        register0(BIRCH_FENCE_GATE, BlockBirchFenceGate.class);// done.
        register0(BIRCH_HANGING_SIGN, BlockBirchHangingSign.class);// done.
        register0(BIRCH_LOG, BlockBirchLog.class);// done.
        register0(BIRCH_PLANKS, BlockBirchPlanks.class);// done.
        register0(BIRCH_PRESSURE_PLATE, BlockBirchPressurePlate.class);// done.
        register0(BIRCH_STAIRS, BlockBirchStairs.class);// done.
        register0(BIRCH_STANDING_SIGN, BlockBirchStandingSign.class);// done.
        register0(BIRCH_TRAPDOOR, BlockBirchTrapdoor.class);// done.
        register0(BIRCH_WALL_SIGN, BlockBirchWallSign.class);// done.
        register0(BLACK_CANDLE, BlockBlackCandle.class);// done.
        register0(BLACK_CANDLE_CAKE, BlockBlackCandleCake.class);// done.
        register0(BLACK_CARPET, BlockBlackCarpet.class);// done.
        register0(BLACK_CONCRETE, BlockBlackConcrete.class);// done.
        register0(BLACK_CONCRETE_POWDER, BlockBlackConcretePowder.class);// done.
        register0(BLACK_GLAZED_TERRACOTTA, BlockBlackGlazedTerracotta.class);// done.
        register0(BLACK_SHULKER_BOX, BlockBlackShulkerBox.class);// done.
        register0(BLACK_STAINED_GLASS, BlockBlackStainedGlass.class);// done.
        register0(BLACK_STAINED_GLASS_PANE, BlockBlackStainedGlassPane.class);// done.
        register0(BLACK_TERRACOTTA, BlockBlackTerracotta.class);// done.
        register0(BLACK_WOOL, BlockBlackWool.class);// done.
        register0(BLACKSTONE, BlockBlackstone.class);// done.
        register0(BLACKSTONE_DOUBLE_SLAB, BlockBlackstoneDoubleSlab.class);// done.
        register0(BLACKSTONE_SLAB, BlockBlackstoneSlab.class);// done.
        register0(BLACKSTONE_STAIRS, BlockBlackstoneStairs.class);// done.
        register0(BLACKSTONE_WALL, BlockBlackstoneWall.class);// done.
        register0(BLAST_FURNACE, BlockBlastFurnace.class);// done.
        register0(BLUE_CANDLE, BlockBlueCandle.class);// done.
        register0(BLUE_CANDLE_CAKE, BlockBlueCandleCake.class);// done.
        register0(BLUE_CARPET, BlockBlueCarpet.class);// done.
        register0(BLUE_CONCRETE, BlockBlueConcrete.class);// done.
        register0(BLUE_CONCRETE_POWDER, BlockBlueConcretePowder.class);// done.
        register0(BLUE_GLAZED_TERRACOTTA, BlockBlueGlazedTerracotta.class);// done.
        register0(BLUE_ICE, BlockBlueIce.class);// done.
        register0(BLUE_SHULKER_BOX, BlockBlueShulkerBox.class);// done.
        register0(BLUE_STAINED_GLASS, BlockBlueStainedGlass.class);// done.
        register0(BLUE_STAINED_GLASS_PANE, BlockBlueStainedGlassPane.class);// done.
        register0(BLUE_TERRACOTTA, BlockBlueTerracotta.class);// done.
        register0(BLUE_WOOL, BlockBlueWool.class);// done.
        register0(BONE_BLOCK, BlockBoneBlock.class);// done.
        register0(BOOKSHELF, BlockBookshelf.class);// done.
        register0(BORDER_BLOCK, BlockBorderBlock.class);// done.
        register0(BRAIN_CORAL, BlockBrainCoral.class);// done.
        register0(BREWING_STAND, BlockBrewingStand.class);// done.
        register0(BRICK_BLOCK, BlockBrickBlock.class);// done.
        register0(BRICK_STAIRS, BlockBrickStairs.class);// done.
        register0(BROWN_CANDLE, BlockBrownCandle.class);// done.
        register0(BROWN_CANDLE_CAKE, BlockBrownCandleCake.class);// done.
        register0(BROWN_CARPET, BlockBrownCarpet.class);// done.
        register0(BROWN_CONCRETE, BlockBrownConcrete.class);// done.
        register0(BROWN_CONCRETE_POWDER, BlockBrownConcretePowder.class);// done.
        register0(BROWN_GLAZED_TERRACOTTA, BlockBrownGlazedTerracotta.class);// done.
        register0(BROWN_MUSHROOM, BlockBrownMushroom.class);// done.
        register0(BROWN_MUSHROOM_BLOCK, BlockBrownMushroomBlock.class);// done.
        register0(BROWN_SHULKER_BOX, BlockBrownShulkerBox.class);// done.
        register0(BROWN_STAINED_GLASS, BlockBrownStainedGlass.class);// done.
        register0(BROWN_STAINED_GLASS_PANE, BlockBrownStainedGlassPane.class);// done.
        register0(BROWN_TERRACOTTA, BlockBrownTerracotta.class);// done.
        register0(BROWN_WOOL, BlockBrownWool.class);// done.
        register0(BUBBLE_COLUMN, BlockBubbleColumn.class);// done.
        register0(BUBBLE_CORAL, BlockBubbleCoral.class);// done.
        register0(BUDDING_AMETHYST, BlockBuddingAmethyst.class);// done.
        register0(CACTUS, BlockCactus.class);// done.
        register0(CAKE, BlockCake.class);// done.
        register0(CALCITE, BlockCalcite.class);// done.
        register0(CALIBRATED_SCULK_SENSOR, BlockCalibratedSculkSensor.class);// done.
//        register0(CAMERA, BlockCamera.class);//edu
        register0(CAMPFIRE, BlockCampfire.class);// done.
        register0(CANDLE, BlockCandle.class);// done.
        register0(CANDLE_CAKE, BlockCandleCake.class);// done.
        register0(CARROTS, BlockCarrots.class);// done.
        register0(CARTOGRAPHY_TABLE, BlockCartographyTable.class);// done.
        register0(CARVED_PUMPKIN, BlockCarvedPumpkin.class);// done.
        register0(CAULDRON, BlockCauldron.class);// done.
        register0(CAVE_VINES, BlockCaveVines.class);// done.
        register0(CAVE_VINES_BODY_WITH_BERRIES, BlockCaveVinesBodyWithBerries.class);// done.
        register0(CAVE_VINES_HEAD_WITH_BERRIES, BlockCaveVinesHeadWithBerries.class);// done.
        register0(CHAIN, BlockChain.class);// done.
        register0(CHAIN_COMMAND_BLOCK, BlockChainCommandBlock.class);// done.
//        register0(CHEMICAL_HEAT, BlockChemicalHeat.class);//edu
//        register0(CHEMISTRY_TABLE, BlockChemistryTable.class);//edu
        register0(CHERRY_BUTTON, BlockCherryButton.class);// done.
        register0(CHERRY_DOOR, BlockCherryDoor.class);// done.
        register0(CHERRY_DOUBLE_SLAB, BlockCherryDoubleSlab.class);// done.
        register0(CHERRY_FENCE, BlockCherryFence.class);// done.
        register0(CHERRY_FENCE_GATE, BlockCherryFenceGate.class);// done.
        register0(CHERRY_HANGING_SIGN, BlockCherryHangingSign.class);// done.
        register0(CHERRY_LEAVES, BlockCherryLeaves.class);// done.
        register0(CHERRY_LOG, BlockCherryLog.class);// done.
        register0(CHERRY_PLANKS, BlockCherryPlanks.class);// done.
        register0(CHERRY_PRESSURE_PLATE, BlockCherryPressurePlate.class);// done.
        register0(CHERRY_SAPLING, BlockCherrySapling.class);// done.
        register0(CHERRY_SLAB, BlockCherrySlab.class);// done.
        register0(CHERRY_STAIRS, BlockCherryStairs.class);// done.
        register0(CHERRY_STANDING_SIGN, BlockCherryStandingSign.class);// done.
        register0(CHERRY_TRAPDOOR, BlockCherryTrapdoor.class);// done.
        register0(CHERRY_WALL_SIGN, BlockCherryWallSign.class);// done.
        register0(CHERRY_WOOD, BlockCherryWood.class);// done.
        register0(CHEST, BlockChest.class);// done.
        register0(CHISELED_BOOKSHELF, BlockChiseledBookshelf.class);// done.
//        register0(CHISELED_COPPER, BlockChiseledCopper.class);// experimental
        register0(CHISELED_DEEPSLATE, BlockChiseledDeepslate.class);// done.
        register0(CHISELED_NETHER_BRICKS, BlockChiseledNetherBricks.class);// done.
        register0(CHISELED_POLISHED_BLACKSTONE, BlockChiseledPolishedBlackstone.class);// done.
//        register0(CHISELED_TUFF, BlockChiseledTuff.class);// experimental
//        register0(CHISELED_TUFF_BRICKS, BlockChiseledTuffBricks.class);// experimental
        register0(CHORUS_FLOWER, BlockChorusFlower.class);// done.
        register0(CHORUS_PLANT, BlockChorusPlant.class);// done.
        register0(CLAY, BlockClay.class);// done.
        register0(CLIENT_REQUEST_PLACEHOLDER_BLOCK, BlockClientRequestPlaceholderBlock.class);// done.
        register0(COAL_BLOCK, BlockCoalBlock.class);// done.
        register0(COAL_ORE, BlockCoalOre.class);// done.
        register0(COBBLED_DEEPSLATE, BlockCobbledDeepslate.class);// done.
        register0(COBBLED_DEEPSLATE_DOUBLE_SLAB, BlockCobbledDeepslateDoubleSlab.class);// done.
        register0(COBBLED_DEEPSLATE_SLAB, BlockCobbledDeepslateSlab.class);// done.
        register0(COBBLED_DEEPSLATE_STAIRS, BlockCobbledDeepslateStairs.class);// done.
        register0(COBBLED_DEEPSLATE_WALL, BlockCobbledDeepslateWall.class);// done.
        register0(COBBLESTONE, BlockCobblestone.class);// done.
        register0(COBBLESTONE_WALL, BlockCobblestoneWall.class);// done.
        register0(COCOA, BlockCocoa.class);// done.
//        register0(COLORED_TORCH_BP, BlockColoredTorchBp.class);//edu
//        register0(COLORED_TORCH_RG, BlockColoredTorchRg.class);//edu
        register0(COMMAND_BLOCK, BlockCommandBlock.class);// done.
        register0(COMPOSTER, BlockComposter.class);// done.
        register0(CONDUIT, BlockConduit.class);// done.
        register0(COPPER_BLOCK, BlockCopperBlock.class);// done.
//        register0(COPPER_BULB, BlockCopperBulb.class);// experiment
        register0(COPPER_DOOR, BlockCopperDoor.class);// done.
//        register0(COPPER_GRATE, BlockCopperGrate.class);//experimental
        register0(COPPER_ORE, BlockCopperOre.class);// done.
//        register0(COPPER_TRAPDOOR, BlockCopperTrapdoor.class);// experimental
        register0(CORAL_BLOCK, BlockCoralBlock.class);// done.
        register0(CORAL_FAN, BlockCoralFan.class);// done.
        register0(CORAL_FAN_DEAD, BlockCoralFanDead.class);// done.
        register0(CORAL_FAN_HANG, BlockCoralFanHang.class);// done.
        register0(CORAL_FAN_HANG2, BlockCoralFanHang2.class);// done.
        register0(CORAL_FAN_HANG3, BlockCoralFanHang3.class);// done.
        register0(CRACKED_DEEPSLATE_BRICKS, BlockCrackedDeepslateBricks.class);// done.
        register0(CRACKED_DEEPSLATE_TILES, BlockCrackedDeepslateTiles.class);// done.
        register0(CRACKED_NETHER_BRICKS, BlockCrackedNetherBricks.class);// done.
        register0(CRACKED_POLISHED_BLACKSTONE_BRICKS, BlockCrackedPolishedBlackstoneBricks.class);// done.
//        register0(CRAFTER, BlockCrafter.class);//experimental
        register0(CRAFTING_TABLE, BlockCraftingTable.class);// done.
        register0(CRIMSON_BUTTON, BlockCrimsonButton.class);// done.
        register0(CRIMSON_DOOR, BlockCrimsonDoor.class);// done.
        register0(CRIMSON_DOUBLE_SLAB, BlockCrimsonDoubleSlab.class);// done.
        register0(CRIMSON_FENCE, BlockCrimsonFence.class);// done.
        register0(CRIMSON_FENCE_GATE, BlockCrimsonFenceGate.class);// done.
        register0(CRIMSON_FUNGUS, BlockCrimsonFungus.class);// done.
        register0(CRIMSON_HANGING_SIGN, BlockCrimsonHangingSign.class);// done.
        register0(CRIMSON_HYPHAE, BlockCrimsonHyphae.class);// done.
        register0(CRIMSON_NYLIUM, BlockCrimsonNylium.class);// done.
        register0(CRIMSON_PLANKS, BlockCrimsonPlanks.class);// done.
        register0(CRIMSON_PRESSURE_PLATE, BlockCrimsonPressurePlate.class);// done.
        register0(CRIMSON_ROOTS, BlockCrimsonRoots.class);// done.
        register0(CRIMSON_SLAB, BlockCrimsonSlab.class);// done.
        register0(CRIMSON_STAIRS, BlockCrimsonStairs.class);// done.
        register0(CRIMSON_STANDING_SIGN, BlockCrimsonStandingSign.class);// done.
        register0(CRIMSON_STEM, BlockCrimsonStem.class);// done.
        register0(CRIMSON_TRAPDOOR, BlockCrimsonTrapdoor.class);// done.
        register0(CRIMSON_WALL_SIGN, BlockCrimsonWallSign.class);// done.
        register0(CRYING_OBSIDIAN, BlockCryingObsidian.class);// done.
        register0(CUT_COPPER, BlockCutCopper.class);// done.
        register0(CUT_COPPER_SLAB, BlockCutCopperSlab.class);// done.
        register0(CUT_COPPER_STAIRS, BlockCutCopperStairs.class);// done.
        register0(CYAN_CANDLE, BlockCyanCandle.class);// done.
        register0(CYAN_CANDLE_CAKE, BlockCyanCandleCake.class);// done.
        register0(CYAN_CARPET, BlockCyanCarpet.class);// done.
        register0(CYAN_CONCRETE, BlockCyanConcrete.class);// done.
        register0(CYAN_CONCRETE_POWDER, BlockCyanConcretePowder.class);// done.
        register0(CYAN_GLAZED_TERRACOTTA, BlockCyanGlazedTerracotta.class);// done.
        register0(CYAN_SHULKER_BOX, BlockCyanShulkerBox.class);// done.
        register0(CYAN_STAINED_GLASS, BlockCyanStainedGlass.class);// done.
        register0(CYAN_STAINED_GLASS_PANE, BlockCyanStainedGlassPane.class);// done.
        register0(CYAN_TERRACOTTA, BlockCyanTerracotta.class);// done.
        register0(CYAN_WOOL, BlockCyanWool.class);// done.
        register0(DARK_OAK_BUTTON, BlockDarkOakButton.class);// done.
        register0(DARK_OAK_DOOR, BlockDarkOakDoor.class);// done.
        register0(DARK_OAK_FENCE, BlockDarkOakFence.class);// done.
        register0(DARK_OAK_FENCE_GATE, BlockDarkOakFenceGate.class);// done.
        register0(DARK_OAK_HANGING_SIGN, BlockDarkOakHangingSign.class);// done.
        register0(DARK_OAK_LOG, BlockDarkOakLog.class);// done.
        register0(DARK_OAK_PLANKS, BlockDarkOakPlanks.class);// done.
        register0(DARK_OAK_PRESSURE_PLATE, BlockDarkOakPressurePlate.class);// done.
        register0(DARK_OAK_STAIRS, BlockDarkOakStairs.class);// done.
        register0(DARK_OAK_TRAPDOOR, BlockDarkOakTrapdoor.class);// done.
        register0(DARK_PRISMARINE_STAIRS, BlockDarkPrismarineStairs.class);// done.
        register0(DARKOAK_STANDING_SIGN, BlockDarkoakStandingSign.class);// done.
        register0(DARKOAK_WALL_SIGN, BlockDarkoakWallSign.class);// done.
        register0(DAYLIGHT_DETECTOR, BlockDaylightDetector.class);// done.
        register0(DAYLIGHT_DETECTOR_INVERTED, BlockDaylightDetectorInverted.class);// done.
        register0(DEAD_BRAIN_CORAL, BlockDeadBrainCoral.class);// done.
        register0(DEAD_BUBBLE_CORAL, BlockDeadBubbleCoral.class);// done.
        register0(DEAD_FIRE_CORAL, BlockDeadFireCoral.class);// done.
        register0(DEAD_HORN_CORAL, BlockDeadHornCoral.class);// done.
        register0(DEAD_TUBE_CORAL, BlockDeadTubeCoral.class);// done.
        register0(DEADBUSH, BlockDeadbush.class);// done.
        register0(DECORATED_POT, BlockDecoratedPot.class);// done.
        register0(DEEPSLATE, BlockDeepslate.class);// done.
        register0(DEEPSLATE_BRICK_DOUBLE_SLAB, BlockDeepslateBrickDoubleSlab.class);// done.
        register0(DEEPSLATE_BRICK_SLAB, BlockDeepslateBrickSlab.class);// done.
        register0(DEEPSLATE_BRICK_STAIRS, BlockDeepslateBrickStairs.class);// done.
        register0(DEEPSLATE_BRICK_WALL, BlockDeepslateBrickWall.class);// done.
        register0(DEEPSLATE_BRICKS, BlockDeepslateBricks.class);// done.
        register0(DEEPSLATE_COAL_ORE, BlockDeepslateCoalOre.class);// done.
        register0(DEEPSLATE_COPPER_ORE, BlockDeepslateCopperOre.class);// done.
        register0(DEEPSLATE_DIAMOND_ORE, BlockDeepslateDiamondOre.class);// done.
        register0(DEEPSLATE_EMERALD_ORE, BlockDeepslateEmeraldOre.class);// done.
        register0(DEEPSLATE_GOLD_ORE, BlockDeepslateGoldOre.class);// done.
        register0(DEEPSLATE_IRON_ORE, BlockDeepslateIronOre.class);// done.
        register0(DEEPSLATE_LAPIS_ORE, BlockDeepslateLapisOre.class);// done.
        register0(DEEPSLATE_REDSTONE_ORE, BlockDeepslateRedstoneOre.class);// done.
        register0(DEEPSLATE_TILE_DOUBLE_SLAB, BlockDeepslateTileDoubleSlab.class);// done.
        register0(DEEPSLATE_TILE_SLAB, BlockDeepslateTileSlab.class);// done.
        register0(DEEPSLATE_TILE_STAIRS, BlockDeepslateTileStairs.class);// done.
        register0(DEEPSLATE_TILE_WALL, BlockDeepslateTileWall.class);// done.
        register0(DEEPSLATE_TILES, BlockDeepslateTiles.class);// done.
        register0(DENY, BlockDeny.class);// done.
        register0(DETECTOR_RAIL, BlockDetectorRail.class);// done.
        register0(DIAMOND_BLOCK, BlockDiamondBlock.class);// done.
        register0(DIAMOND_ORE, BlockDiamondOre.class);// done.
        register0(DIORITE, BlockDiorite.class);// done.
        register0(DIORITE_STAIRS, BlockDioriteStairs.class);// done.
        register0(DIRT, BlockDirt.class);// done.
        register0(DIRT_WITH_ROOTS, BlockDirtWithRoots.class);// done.
        register0(DISPENSER, BlockDispenser.class);// done.
        register0(DOUBLE_CUT_COPPER_SLAB, BlockDoubleCutCopperSlab.class);// done.
        register0(DOUBLE_PLANT, BlockDoublePlant.class);// done.
        register0(DOUBLE_STONE_BLOCK_SLAB, BlockDoubleStoneBlockSlab.class);// done.
        register0(DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleStoneBlockSlab2.class);// done.
        register0(DOUBLE_STONE_BLOCK_SLAB3, BlockDoubleStoneBlockSlab3.class);// done.
        register0(DOUBLE_STONE_BLOCK_SLAB4, BlockDoubleStoneBlockSlab4.class);// done.
        register0(DOUBLE_WOODEN_SLAB, BlockDoubleWoodenSlab.class);// done.
        register0(DRAGON_EGG, BlockDragonEgg.class);// done.
        register0(DRIED_KELP_BLOCK, BlockDriedKelpBlock.class);// done.
        register0(DRIPSTONE_BLOCK, BlockDripstoneBlock.class);// done.
        register0(DROPPER, BlockDropper.class);// done.
//        register0(ELEMENT_0, BlockElement0.class);
//        register0(ELEMENT_1, BlockElement1.class);
//        register0(ELEMENT_10, BlockElement10.class);
//        register0(ELEMENT_100, BlockElement100.class);
//        register0(ELEMENT_101, BlockElement101.class);
//        register0(ELEMENT_102, BlockElement102.class);
//        register0(ELEMENT_103, BlockElement103.class);
//        register0(ELEMENT_104, BlockElement104.class);
//        register0(ELEMENT_105, BlockElement105.class);
//        register0(ELEMENT_106, BlockElement106.class);
//        register0(ELEMENT_107, BlockElement107.class);
//        register0(ELEMENT_108, BlockElement108.class);
//        register0(ELEMENT_109, BlockElement109.class);
//        register0(ELEMENT_11, BlockElement11.class);
//        register0(ELEMENT_110, BlockElement110.class);
//        register0(ELEMENT_111, BlockElement111.class);
//        register0(ELEMENT_112, BlockElement112.class);
//        register0(ELEMENT_113, BlockElement113.class);
//        register0(ELEMENT_114, BlockElement114.class);
//        register0(ELEMENT_115, BlockElement115.class);
//        register0(ELEMENT_116, BlockElement116.class);
//        register0(ELEMENT_117, BlockElement117.class);
//        register0(ELEMENT_118, BlockElement118.class);
//        register0(ELEMENT_12, BlockElement12.class);
//        register0(ELEMENT_13, BlockElement13.class);
//        register0(ELEMENT_14, BlockElement14.class);
//        register0(ELEMENT_15, BlockElement15.class);
//        register0(ELEMENT_16, BlockElement16.class);
//        register0(ELEMENT_17, BlockElement17.class);
//        register0(ELEMENT_18, BlockElement18.class);
//        register0(ELEMENT_19, BlockElement19.class);
//        register0(ELEMENT_2, BlockElement2.class);
//        register0(ELEMENT_20, BlockElement20.class);
//        register0(ELEMENT_21, BlockElement21.class);
//        register0(ELEMENT_22, BlockElement22.class);
//        register0(ELEMENT_23, BlockElement23.class);
//        register0(ELEMENT_24, BlockElement24.class);
//        register0(ELEMENT_25, BlockElement25.class);
//        register0(ELEMENT_26, BlockElement26.class);
//        register0(ELEMENT_27, BlockElement27.class);
//        register0(ELEMENT_28, BlockElement28.class);
//        register0(ELEMENT_29, BlockElement29.class);
//        register0(ELEMENT_3, BlockElement3.class);
//        register0(ELEMENT_30, BlockElement30.class);
//        register0(ELEMENT_31, BlockElement31.class);
//        register0(ELEMENT_32, BlockElement32.class);
//        register0(ELEMENT_33, BlockElement33.class);
//        register0(ELEMENT_34, BlockElement34.class);
//        register0(ELEMENT_35, BlockElement35.class);
//        register0(ELEMENT_36, BlockElement36.class);
//        register0(ELEMENT_37, BlockElement37.class);
//        register0(ELEMENT_38, BlockElement38.class);
//        register0(ELEMENT_39, BlockElement39.class);
//        register0(ELEMENT_4, BlockElement4.class);
//        register0(ELEMENT_40, BlockElement40.class);
//        register0(ELEMENT_41, BlockElement41.class);
//        register0(ELEMENT_42, BlockElement42.class);
//        register0(ELEMENT_43, BlockElement43.class);
//        register0(ELEMENT_44, BlockElement44.class);
//        register0(ELEMENT_45, BlockElement45.class);
//        register0(ELEMENT_46, BlockElement46.class);
//        register0(ELEMENT_47, BlockElement47.class);
//        register0(ELEMENT_48, BlockElement48.class);
//        register0(ELEMENT_49, BlockElement49.class);
//        register0(ELEMENT_5, BlockElement5.class);
//        register0(ELEMENT_50, BlockElement50.class);
//        register0(ELEMENT_51, BlockElement51.class);
//        register0(ELEMENT_52, BlockElement52.class);
//        register0(ELEMENT_53, BlockElement53.class);
//        register0(ELEMENT_54, BlockElement54.class);
//        register0(ELEMENT_55, BlockElement55.class);
//        register0(ELEMENT_56, BlockElement56.class);
//        register0(ELEMENT_57, BlockElement57.class);
//        register0(ELEMENT_58, BlockElement58.class);
//        register0(ELEMENT_59, BlockElement59.class);
//        register0(ELEMENT_6, BlockElement6.class);
//        register0(ELEMENT_60, BlockElement60.class);
//        register0(ELEMENT_61, BlockElement61.class);
//        register0(ELEMENT_62, BlockElement62.class);
//        register0(ELEMENT_63, BlockElement63.class);
//        register0(ELEMENT_64, BlockElement64.class);
//        register0(ELEMENT_65, BlockElement65.class);
//        register0(ELEMENT_66, BlockElement66.class);
//        register0(ELEMENT_67, BlockElement67.class);
//        register0(ELEMENT_68, BlockElement68.class);
//        register0(ELEMENT_69, BlockElement69.class);
//        register0(ELEMENT_7, BlockElement7.class);
//        register0(ELEMENT_70, BlockElement70.class);
//        register0(ELEMENT_71, BlockElement71.class);
//        register0(ELEMENT_72, BlockElement72.class);
//        register0(ELEMENT_73, BlockElement73.class);
//        register0(ELEMENT_74, BlockElement74.class);
//        register0(ELEMENT_75, BlockElement75.class);
//        register0(ELEMENT_76, BlockElement76.class);
//        register0(ELEMENT_77, BlockElement77.class);
//        register0(ELEMENT_78, BlockElement78.class);
//        register0(ELEMENT_79, BlockElement79.class);
//        register0(ELEMENT_8, BlockElement8.class);
//        register0(ELEMENT_80, BlockElement80.class);
//        register0(ELEMENT_81, BlockElement81.class);
//        register0(ELEMENT_82, BlockElement82.class);
//        register0(ELEMENT_83, BlockElement83.class);
//        register0(ELEMENT_84, BlockElement84.class);
//        register0(ELEMENT_85, BlockElement85.class);
//        register0(ELEMENT_86, BlockElement86.class);
//        register0(ELEMENT_87, BlockElement87.class);
//        register0(ELEMENT_88, BlockElement88.class);
//        register0(ELEMENT_89, BlockElement89.class);
//        register0(ELEMENT_9, BlockElement9.class);
//        register0(ELEMENT_90, BlockElement90.class);
//        register0(ELEMENT_91, BlockElement91.class);
//        register0(ELEMENT_92, BlockElement92.class);
//        register0(ELEMENT_93, BlockElement93.class);
//        register0(ELEMENT_94, BlockElement94.class);
//        register0(ELEMENT_95, BlockElement95.class);
//        register0(ELEMENT_96, BlockElement96.class);
//        register0(ELEMENT_97, BlockElement97.class);
//        register0(ELEMENT_98, BlockElement98.class);
//        register0(ELEMENT_99, BlockElement99.class);
        register0(EMERALD_BLOCK, BlockEmeraldBlock.class);// done.
        register0(EMERALD_ORE, BlockEmeraldOre.class);// done.
        register0(ENCHANTING_TABLE, BlockEnchantingTable.class);// done.
        register0(END_BRICK_STAIRS, BlockEndBrickStairs.class);// done.
        register0(END_BRICKS, BlockEndBricks.class);// done.
        register0(END_GATEWAY, BlockEndGateway.class);// done.
        register0(END_PORTAL, BlockEndPortal.class);// done.
        register0(END_PORTAL_FRAME, BlockEndPortalFrame.class);// done.
        register0(END_ROD, BlockEndRod.class);// done.
        register0(END_STONE, BlockEndStone.class);// done.
        register0(ENDER_CHEST, BlockEnderChest.class);// done.
//        register0(EXPOSED_CHISELED_COPPER, BlockExposedChiseledCopper.class);//experiment
        register0(EXPOSED_COPPER, BlockExposedCopper.class);// done.
//        register0(EXPOSED_COPPER_BULB, BlockExposedCopperBulb.class);//experiment
//        register0(EXPOSED_COPPER_DOOR, BlockExposedCopperDoor.class);//experiment
//        register0(EXPOSED_COPPER_GRATE, BlockExposedCopperGrate.class);//experiment
//        register0(EXPOSED_COPPER_TRAPDOOR, BlockExposedCopperTrapdoor.class);//experiment
        register0(EXPOSED_CUT_COPPER, BlockExposedCutCopper.class);// done.
        register0(EXPOSED_CUT_COPPER_SLAB, BlockExposedCutCopperSlab.class);
        ;// done.
        register0(EXPOSED_CUT_COPPER_STAIRS, BlockExposedCutCopperStairs.class);// done.
        register0(EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockExposedDoubleCutCopperSlab.class);// done.
        register0(FARMLAND, BlockFarmland.class);// done.
        register0(FENCE_GATE, BlockFenceGate.class);// done.
        register0(FIRE, BlockFire.class);// done.
        register0(FIRE_CORAL, BlockFireCoral.class);// done.
        register0(FLETCHING_TABLE, BlockFletchingTable.class);// done.
        register0(FLOWER_POT, BlockFlowerPot.class);// done.
        register0(FLOWERING_AZALEA, BlockFloweringAzalea.class);// done.
        register0(FLOWING_LAVA, BlockFlowingLava.class);// done.
        register0(FLOWING_WATER, BlockFlowingWater.class);// done.
        register0(FRAME, BlockFrame.class);// done.
        register0(FROG_SPAWN, BlockFrogSpawn.class);// done.
        register0(FROSTED_ICE, BlockFrostedIce.class);// done.
        register0(FURNACE, BlockFurnace.class);// done.
        register0(GILDED_BLACKSTONE, BlockGildedBlackstone.class);// done.
        register0(GLASS, BlockGlass.class);// done.
        register0(GLASS_PANE, BlockGlassPane.class);// done.
        register0(GLOW_FRAME, BlockGlowFrame.class);// done.
        register0(GLOW_LICHEN, BlockGlowLichen.class);// done.
        register0(GLOWINGOBSIDIAN, BlockGlowingobsidian.class);// done.
        register0(GLOWSTONE, BlockGlowstone.class);// done.
        register0(GOLD_BLOCK, BlockGoldBlock.class);// done.
        register0(GOLD_ORE, BlockGoldOre.class);// done.
        register0(GOLDEN_RAIL, BlockGoldenRail.class);// done.
        register0(GRANITE, BlockGranite.class);// done.
        register0(GRANITE_STAIRS, BlockGraniteStairs.class);// done.
        register0(GRASS, BlockGrass.class);// done.
        register0(GRASS_PATH, BlockGrassPath.class);// done.
        register0(GRAVEL, BlockGravel.class);// done.
        register0(GRAY_CANDLE, BlockGrayCandle.class);// done.
        register0(GRAY_CANDLE_CAKE, BlockGrayCandleCake.class);// done.
        register0(GRAY_CARPET, BlockGrayCarpet.class);// done.
        register0(GRAY_CONCRETE, BlockGrayConcrete.class);// done.
        register0(GRAY_CONCRETE_POWDER, BlockGrayConcretePowder.class);// done.
        register0(GRAY_GLAZED_TERRACOTTA, BlockGrayGlazedTerracotta.class);// done.
        register0(GRAY_SHULKER_BOX, BlockGrayShulkerBox.class);// done.
        register0(GRAY_STAINED_GLASS, BlockGrayStainedGlass.class);// done.
        register0(GRAY_STAINED_GLASS_PANE, BlockGrayStainedGlassPane.class);// done.
        register0(GRAY_TERRACOTTA, BlockGrayTerracotta.class);// done.
        register0(GRAY_WOOL, BlockGrayWool.class);// done.
        register0(GREEN_CANDLE, BlockGreenCandle.class);// done.
        register0(GREEN_CANDLE_CAKE, BlockGreenCandleCake.class);// done.
        register0(GREEN_CARPET, BlockGreenCarpet.class);// done.
        register0(GREEN_CONCRETE, BlockGreenConcrete.class);// done.
        register0(GREEN_CONCRETE_POWDER, BlockGreenConcretePowder.class);// done.
        register0(GREEN_GLAZED_TERRACOTTA, BlockGreenGlazedTerracotta.class);// done.
        register0(GREEN_SHULKER_BOX, BlockGreenShulkerBox.class);// done.
        register0(GREEN_STAINED_GLASS, BlockGreenStainedGlass.class);// done.
        register0(GREEN_STAINED_GLASS_PANE, BlockGreenStainedGlassPane.class);// done.
        register0(GREEN_TERRACOTTA, BlockGreenTerracotta.class);// done.
        register0(GREEN_WOOL, BlockGreenWool.class);// done.
        register0(GRINDSTONE, BlockGrindstone.class);// done.
        register0(HANGING_ROOTS, BlockHangingRoots.class);// done.
//        register0(HARD_GLASS, BlockHardGlass.class);//edu
//        register0(HARD_GLASS_PANE, BlockHardGlassPane.class);//edu
//        register0(HARD_STAINED_GLASS, BlockHardStainedGlass.class);//edu
//        register0(HARD_STAINED_GLASS_PANE, BlockHardStainedGlassPane.class);//edu
        register0(HARDENED_CLAY, BlockHardenedClay.class);// done.
        register0(HAY_BLOCK, BlockHayBlock.class);// done.
        register0(HEAVY_WEIGHTED_PRESSURE_PLATE, BlockHeavyWeightedPressurePlate.class);// done.
        register0(HONEY_BLOCK, BlockHoneyBlock.class);// done.
        register0(HONEYCOMB_BLOCK, BlockHoneycombBlock.class);// done.
        register0(HOPPER, BlockHopper.class);// done.
        register0(HORN_CORAL, BlockHornCoral.class);// done.
        register0(ICE, BlockIce.class);// done.
        register0(INFESTED_DEEPSLATE, BlockInfestedDeepslate.class);// done.
        register0(INFO_UPDATE, BlockInfoUpdate.class);// done.
        register0(INFO_UPDATE2, BlockInfoUpdate2.class);// done.
        register0(INVISIBLE_BEDROCK, BlockInvisibleBedrock.class);// done.
        register0(IRON_BARS, BlockIronBars.class);// done.
        register0(IRON_BLOCK, BlockIronBlock.class);// done.
        register0(IRON_DOOR, BlockIronDoor.class);// done.
        register0(IRON_ORE, BlockIronOre.class);// done.
        register0(IRON_TRAPDOOR, BlockIronTrapdoor.class);// done.
        register0(JIGSAW, BlockJigsaw.class);// done.
        register0(JUKEBOX, BlockJukebox.class);// done.
        register0(JUNGLE_BUTTON, BlockJungleButton.class);// done.
        register0(JUNGLE_DOOR, BlockJungleDoor.class);// done.
        register0(JUNGLE_FENCE, BlockJungleFence.class);// done.
        register0(JUNGLE_FENCE_GATE, BlockJungleFenceGate.class);// done.
        register0(JUNGLE_HANGING_SIGN, BlockJungleHangingSign.class);// done.
        register0(JUNGLE_LOG, BlockJungleLog.class);// done.
        register0(JUNGLE_PLANKS, BlockJunglePlanks.class);// done.
        register0(JUNGLE_PRESSURE_PLATE, BlockJunglePressurePlate.class);// done.
        register0(JUNGLE_STAIRS, BlockJungleStairs.class);// done.
        register0(JUNGLE_STANDING_SIGN, BlockJungleStandingSign.class);// done.
        register0(JUNGLE_TRAPDOOR, BlockJungleTrapdoor.class);// done.
        register0(JUNGLE_WALL_SIGN, BlockJungleWallSign.class);// done.
        register0(KELP, BlockKelp.class);// done.
        register0(LADDER, BlockLadder.class);// done.
        register0(LANTERN, BlockLantern.class);// done.
        register0(LAPIS_BLOCK, BlockLapisBlock.class);// done.
        register0(LAPIS_ORE, BlockLapisOre.class);// done.
        register0(LARGE_AMETHYST_BUD, BlockLargeAmethystBud.class);// done.
        register0(LAVA, BlockLava.class);// done.
        register0(LEAVES, BlockLeaves.class);// done.
        register0(LEAVES2, BlockLeaves2.class);// done.
        register0(LECTERN, BlockLectern.class);// done.
        register0(LEVER, BlockLever.class);// done.
        register0(LIGHT_BLOCK, BlockLightBlock.class);// done.
        register0(LIGHT_BLUE_CANDLE, BlockLightBlueCandle.class);// done.
        register0(LIGHT_BLUE_CANDLE_CAKE, BlockLightBlueCandleCake.class);// done.
        register0(LIGHT_BLUE_CARPET, BlockLightBlueCarpet.class);// done.
        register0(LIGHT_BLUE_CONCRETE, BlockLightBlueConcrete.class);// done.
        register0(LIGHT_BLUE_CONCRETE_POWDER, BlockLightBlueConcretePowder.class);// done.
        register0(LIGHT_BLUE_GLAZED_TERRACOTTA, BlockLightBlueGlazedTerracotta.class);// done.
        register0(LIGHT_BLUE_SHULKER_BOX, BlockLightBlueShulkerBox.class);// done.
        register0(LIGHT_BLUE_STAINED_GLASS, BlockLightBlueStainedGlass.class);// done.
        register0(LIGHT_BLUE_STAINED_GLASS_PANE, BlockLightBlueStainedGlassPane.class);// done.
        register0(LIGHT_BLUE_TERRACOTTA, BlockLightBlueTerracotta.class);// done.
        register0(LIGHT_BLUE_WOOL, BlockLightBlueWool.class);// done.
        register0(LIGHT_GRAY_CANDLE, BlockLightGrayCandle.class);// done.
        register0(LIGHT_GRAY_CANDLE_CAKE, BlockLightGrayCandleCake.class);// done.
        register0(LIGHT_GRAY_CARPET, BlockLightGrayCarpet.class);// done.
        register0(LIGHT_GRAY_CONCRETE, BlockLightGrayConcrete.class);// done.
        register0(LIGHT_GRAY_CONCRETE_POWDER, BlockLightGrayConcretePowder.class);// done.
        register0(LIGHT_GRAY_SHULKER_BOX, BlockLightGrayShulkerBox.class);// done.
        register0(LIGHT_GRAY_STAINED_GLASS, BlockLightGrayStainedGlass.class);// done.
        register0(LIGHT_GRAY_STAINED_GLASS_PANE, BlockLightGrayStainedGlassPane.class);// done.
        register0(LIGHT_GRAY_TERRACOTTA, BlockLightGrayTerracotta.class);// done.
        register0(LIGHT_GRAY_WOOL, BlockLightGrayWool.class);// done.
        register0(LIGHT_WEIGHTED_PRESSURE_PLATE, BlockLightWeightedPressurePlate.class);// done.
        register0(LIGHTNING_ROD, BlockLightningRod.class);// done.
        register0(LIME_CANDLE, BlockLimeCandle.class);// done.
        register0(LIME_CANDLE_CAKE, BlockLimeCandleCake.class);// done.
        register0(LIME_CARPET, BlockLimeCarpet.class);// done.
        register0(LIME_CONCRETE, BlockLimeConcrete.class);// done.
        register0(LIME_CONCRETE_POWDER, BlockLimeConcretePowder.class);// done.
        register0(LIME_GLAZED_TERRACOTTA, BlockLimeGlazedTerracotta.class);// done.
        register0(LIME_SHULKER_BOX, BlockLimeShulkerBox.class);// done.
        register0(LIME_STAINED_GLASS, BlockLimeStainedGlass.class);// done.
        register0(LIME_STAINED_GLASS_PANE, BlockLimeStainedGlassPane.class);// done.
        register0(LIME_TERRACOTTA, BlockLimeTerracotta.class);// done.
        register0(LIME_WOOL, BlockLimeWool.class);// done.
        register0(LIT_BLAST_FURNACE, BlockLitBlastFurnace.class);// done.
        register0(LIT_DEEPSLATE_REDSTONE_ORE, BlockLitDeepslateRedstoneOre.class);// done.
        register0(LIT_FURNACE, BlockLitFurnace.class);// done.
        register0(LIT_PUMPKIN, BlockLitPumpkin.class);// done.
        register0(LIT_REDSTONE_LAMP, BlockLitRedstoneLamp.class);// done.
        register0(LIT_REDSTONE_ORE, BlockLitRedstoneOre.class);// done.
        register0(LIT_SMOKER, BlockLitSmoker.class);// done.
        register0(LODESTONE, BlockLodestone.class);// done.
        register0(LOOM, BlockLoom.class);// done.
        register0(MAGENTA_CANDLE, BlockMagentaCandle.class);// done.
        register0(MAGENTA_CANDLE_CAKE, BlockMagentaCandleCake.class);// done.
        register0(MAGENTA_CARPET, BlockMagentaCarpet.class);// done.
        register0(MAGENTA_CONCRETE, BlockMagentaConcrete.class);// done.
        register0(MAGENTA_CONCRETE_POWDER, BlockMagentaConcretePowder.class);// done.
        register0(MAGENTA_GLAZED_TERRACOTTA, BlockMagentaGlazedTerracotta.class);// done.
        register0(MAGENTA_SHULKER_BOX, BlockMagentaShulkerBox.class);// done.
        register0(MAGENTA_STAINED_GLASS, BlockMagentaStainedGlass.class);// done.
        register0(MAGENTA_STAINED_GLASS_PANE, BlockMagentaStainedGlassPane.class);// done.
        register0(MAGENTA_TERRACOTTA, BlockMagentaTerracotta.class);// done.
        register0(MAGENTA_WOOL, BlockMagentaWool.class);// done.
        register0(MAGMA, BlockMagma.class);// done.
        register0(MANGROVE_BUTTON, BlockMangroveButton.class);// done.
        register0(MANGROVE_DOOR, BlockMangroveDoor.class);// done.
        register0(MANGROVE_DOUBLE_SLAB, BlockMangroveDoubleSlab.class);// done.
        register0(MANGROVE_FENCE, BlockMangroveFence.class);// done.
        register0(MANGROVE_FENCE_GATE, BlockMangroveFenceGate.class);// done.
        register0(MANGROVE_HANGING_SIGN, BlockMangroveHangingSign.class);// done.
        register0(MANGROVE_LEAVES, BlockMangroveLeaves.class);// done.
        register0(MANGROVE_LOG, BlockMangroveLog.class);// done.
        register0(MANGROVE_PLANKS, BlockMangrovePlanks.class);// done.
        register0(MANGROVE_PRESSURE_PLATE, BlockMangrovePressurePlate.class);// done.
        register0(MANGROVE_PROPAGULE, BlockMangrovePropagule.class);// done.
        register0(MANGROVE_ROOTS, BlockMangroveRoots.class);// done.
        register0(MANGROVE_SLAB, BlockMangroveSlab.class);// done.
        register0(MANGROVE_STAIRS, BlockMangroveStairs.class);// done.
        register0(MANGROVE_STANDING_SIGN, BlockMangroveStandingSign.class);// done.
        register0(MANGROVE_TRAPDOOR, BlockMangroveTrapdoor.class);// done.
        register0(MANGROVE_WALL_SIGN, BlockMangroveWallSign.class);// done.
        register0(MANGROVE_WOOD, BlockMangroveWood.class);// done.
        register0(MEDIUM_AMETHYST_BUD, BlockMediumAmethystBud.class);// done.
        register0(MELON_BLOCK, BlockMelonBlock.class);// done.
        register0(MELON_STEM, BlockMelonStem.class);// done.
        register0(MOB_SPAWNER, BlockMobSpawner.class);// done.
        register0(MONSTER_EGG, BlockMonsterEgg.class);// done.
        register0(MOSS_BLOCK, BlockMossBlock.class);// done.
        register0(MOSS_CARPET, BlockMossCarpet.class);// done.
        register0(MOSSY_COBBLESTONE, BlockMossyCobblestone.class);// done.
        register0(MOSSY_COBBLESTONE_STAIRS, BlockMossyCobblestoneStairs.class);// done.
        register0(MOSSY_STONE_BRICK_STAIRS, BlockMossyStoneBrickStairs.class);// done.
        register0(MOVING_BLOCK, BlockMovingBlock.class);// done.
        register0(MUD, BlockMud.class);// done.
        register0(MUD_BRICK_DOUBLE_SLAB, BlockMudBrickDoubleSlab.class);// done.
        register0(MUD_BRICK_SLAB, BlockMudBrickSlab.class);// done.
        register0(MUD_BRICK_STAIRS, BlockMudBrickStairs.class);// done.
        register0(MUD_BRICK_WALL, BlockMudBrickWall.class);// done.
        register0(MUD_BRICKS, BlockMudBricks.class);// done.
        register0(MUDDY_MANGROVE_ROOTS, BlockMuddyMangroveRoots.class);// done.
        register0(MYCELIUM, BlockMycelium.class);// done.
        register0(NETHER_BRICK, BlockNetherBrick.class);// done.
        register0(NETHER_BRICK_FENCE, BlockNetherBrickFence.class);// done.
        register0(NETHER_BRICK_STAIRS, BlockNetherBrickStairs.class);// done.
        register0(NETHER_GOLD_ORE, BlockNetherGoldOre.class);// done.
        register0(NETHER_SPROUTS, BlockNetherSprouts.class);// done.
        register0(NETHER_WART, BlockNetherWart.class);// done.
        register0(NETHER_WART_BLOCK, BlockNetherWartBlock.class);// done.
        register0(NETHERITE_BLOCK, BlockNetheriteBlock.class);// done.
        register0(NETHERRACK, BlockNetherrack.class);// done.
        register0(NETHERREACTOR, BlockNetherreactor.class);// done.
        register0(NORMAL_STONE_STAIRS, BlockNormalStoneStairs.class);// done.
        register0(NOTEBLOCK, BlockNoteblock.class);// done.
        register0(OAK_FENCE, BlockOakFence.class);// done.
        register0(OAK_HANGING_SIGN, BlockOakHangingSign.class);// done.
        register0(OAK_LOG, BlockOakLog.class);// done.
        register0(OAK_PLANKS, BlockOakPlanks.class);// done.
        register0(OAK_STAIRS, BlockOakStairs.class);// done.
        register0(OBSERVER, BlockObserver.class);// done.
        register0(OBSIDIAN, BlockObsidian.class);// done.
        register0(OCHRE_FROGLIGHT, BlockOchreFroglight.class);// done.
        register0(ORANGE_CANDLE, BlockOrangeCandle.class);// done.
        register0(ORANGE_CANDLE_CAKE, BlockOrangeCandleCake.class);// done.
        register0(ORANGE_CARPET, BlockOrangeCarpet.class);// done.
        register0(ORANGE_CONCRETE, BlockOrangeConcrete.class);// done.
        register0(ORANGE_CONCRETE_POWDER, BlockOrangeConcretePowder.class);// done.
        register0(ORANGE_GLAZED_TERRACOTTA, BlockOrangeGlazedTerracotta.class);// done.
        register0(ORANGE_SHULKER_BOX, BlockOrangeShulkerBox.class);// done.
        register0(ORANGE_STAINED_GLASS, BlockOrangeStainedGlass.class);// done.
        register0(ORANGE_STAINED_GLASS_PANE, BlockOrangeStainedGlassPane.class);// done.
        register0(ORANGE_TERRACOTTA, BlockOrangeTerracotta.class);// done.
        register0(ORANGE_WOOL, BlockOrangeWool.class);// done.
//        register0(OXIDIZED_CHISELED_COPPER, BlockOxidizedChiseledCopper.class);// experimental
        register0(OXIDIZED_COPPER, BlockOxidizedCopper.class);// done.
//        register0(OXIDIZED_COPPER_BULB, BlockOxidizedCopperBulb.class);// experimental
//        register0(OXIDIZED_COPPER_DOOR, BlockOxidizedCopperDoor.class);// experimental
//        register0(OXIDIZED_COPPER_GRATE, BlockOxidizedCopperGrate.class);// experimental
//        register0(OXIDIZED_COPPER_TRAPDOOR, BlockOxidizedCopperTrapdoor.class);// experimental
        register0(OXIDIZED_CUT_COPPER, BlockOxidizedCutCopper.class);// done.
        register0(OXIDIZED_CUT_COPPER_SLAB, BlockOxidizedCutCopperSlab.class);// done.
        register0(OXIDIZED_CUT_COPPER_STAIRS, BlockOxidizedCutCopperStairs.class);// done.
        register0(OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockOxidizedDoubleCutCopperSlab.class);// done.
        register0(PACKED_ICE, BlockPackedIce.class);// done.
        register0(PACKED_MUD, BlockPackedMud.class);// done.
        register0(PEARLESCENT_FROGLIGHT, BlockPearlescentFroglight.class);// done.
        register0(PINK_CANDLE, BlockPinkCandle.class);// done.
        register0(PINK_CANDLE_CAKE, BlockPinkCandleCake.class);// done.
        register0(PINK_CARPET, BlockPinkCarpet.class);// done.
        register0(PINK_CONCRETE, BlockPinkConcrete.class);// done.
        register0(PINK_CONCRETE_POWDER, BlockPinkConcretePowder.class);// done.
        register0(PINK_GLAZED_TERRACOTTA, BlockPinkGlazedTerracotta.class);// done.
        register0(PINK_PETALS, BlockPinkPetals.class);// done.
        register0(PINK_SHULKER_BOX, BlockPinkShulkerBox.class);// done.
        register0(PINK_STAINED_GLASS, BlockPinkStainedGlass.class);// done.
        register0(PINK_STAINED_GLASS_PANE, BlockPinkStainedGlassPane.class);// done.
        register0(PINK_TERRACOTTA, BlockPinkTerracotta.class);// done.
        register0(PINK_WOOL, BlockPinkWool.class);// done.
        register0(PISTON, BlockPiston.class);// done.
        register0(PISTON_ARM_COLLISION, BlockPistonArmCollision.class);// done.
        register0(PITCHER_CROP, BlockPitcherCrop.class);// done.
        register0(PITCHER_PLANT, BlockPitcherPlant.class);// done.
        register0(PODZOL, BlockPodzol.class);// done.
        register0(POINTED_DRIPSTONE, BlockPointedDripstone.class);// done.
        register0(POLISHED_ANDESITE, BlockPolishedAndesite.class);// done.
        register0(POLISHED_ANDESITE_STAIRS, BlockPolishedAndesiteStairs.class);// done.
        register0(POLISHED_BASALT, BlockPolishedBasalt.class);// done.
        register0(POLISHED_BLACKSTONE, BlockPolishedBlackstone.class);// done.
        register0(POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, BlockPolishedBlackstoneBrickDoubleSlab.class);// done.
        register0(POLISHED_BLACKSTONE_BRICK_SLAB, BlockPolishedBlackstoneBrickSlab.class);// done.
        register0(POLISHED_BLACKSTONE_BRICK_STAIRS, BlockPolishedBlackstoneBrickStairs.class);// done.
        register0(POLISHED_BLACKSTONE_BRICK_WALL, BlockPolishedBlackstoneBrickWall.class);// done.
        register0(POLISHED_BLACKSTONE_BRICKS, BlockPolishedBlackstoneBricks.class);// done.
        register0(POLISHED_BLACKSTONE_BUTTON, BlockPolishedBlackstoneButton.class);// done.
        register0(POLISHED_BLACKSTONE_DOUBLE_SLAB, BlockPolishedBlackstoneDoubleSlab.class);// done.
        register0(POLISHED_BLACKSTONE_PRESSURE_PLATE, BlockPolishedBlackstonePressurePlate.class);// done.
        register0(POLISHED_BLACKSTONE_SLAB, BlockPolishedBlackstoneSlab.class);// done.
        register0(POLISHED_BLACKSTONE_STAIRS, BlockPolishedBlackstoneStairs.class);// done.
        register0(POLISHED_BLACKSTONE_WALL, BlockPolishedBlackstoneWall.class);// done.
        register0(POLISHED_DEEPSLATE, BlockPolishedDeepslate.class);// done.
        register0(POLISHED_DEEPSLATE_DOUBLE_SLAB, BlockPolishedDeepslateDoubleSlab.class);// done.
        register0(POLISHED_DEEPSLATE_SLAB, BlockPolishedDeepslateSlab.class);// done.
        register0(POLISHED_DEEPSLATE_STAIRS, BlockPolishedDeepslateStairs.class);// done.
        register0(POLISHED_DEEPSLATE_WALL, BlockPolishedDeepslateWall.class);// done.
        register0(POLISHED_DIORITE, BlockPolishedDiorite.class);// done.
        register0(POLISHED_DIORITE_STAIRS, BlockPolishedDioriteStairs.class);// done.
        register0(POLISHED_GRANITE, BlockPolishedGranite.class);// done.
        register0(POLISHED_GRANITE_STAIRS, BlockPolishedGraniteStairs.class);// done.
        register0(POLISHED_TUFF, BlockPolishedTuff.class);// done.
//        register0(POLISHED_TUFF_DOUBLE_SLAB, BlockPolishedTuffDoubleSlab.class);//experiment
//        register0(POLISHED_TUFF_SLAB, BlockPolishedTuffSlab.class);//experiment
//        register0(POLISHED_TUFF_STAIRS, BlockPolishedTuffStairs.class);//experiment
//        register0(POLISHED_TUFF_WALL, BlockPolishedTuffWall.class);//experiment
        register0(PORTAL, BlockPortal.class);// done.
        register0(POTATOES, BlockPotatoes.class);// done.
        register0(POWDER_SNOW, BlockPowderSnow.class);// done.
        register0(POWERED_COMPARATOR, BlockPoweredComparator.class);// done.
        register0(POWERED_REPEATER, BlockPoweredRepeater.class);// done.
        register0(PRISMARINE, BlockPrismarine.class);// done.
        register0(PRISMARINE_BRICKS_STAIRS, BlockPrismarineBricksStairs.class);// done.
        register0(PRISMARINE_STAIRS, BlockPrismarineStairs.class);// done.
        register0(PUMPKIN, BlockPumpkin.class);// done.
        register0(PUMPKIN_STEM, BlockPumpkinStem.class);// done.
        register0(PURPLE_CANDLE, BlockPurpleCandle.class);// done.
        register0(PURPLE_CANDLE_CAKE, BlockPurpleCandleCake.class);// done.
        register0(PURPLE_CARPET, BlockPurpleCarpet.class);// done.
        register0(PURPLE_CONCRETE, BlockPurpleConcrete.class);// done.
        register0(PURPLE_CONCRETE_POWDER, BlockPurpleConcretePowder.class);// done.
        register0(PURPLE_GLAZED_TERRACOTTA, BlockPurpleGlazedTerracotta.class);// done.
        register0(PURPLE_SHULKER_BOX, BlockPurpleShulkerBox.class);// done.
        register0(PURPLE_STAINED_GLASS, BlockPurpleStainedGlass.class);// done.
        register0(PURPLE_STAINED_GLASS_PANE, BlockPurpleStainedGlassPane.class);// done.
        register0(PURPLE_TERRACOTTA, BlockPurpleTerracotta.class);// done.
        register0(PURPLE_WOOL, BlockPurpleWool.class);// done.
        register0(PURPUR_BLOCK, BlockPurpurBlock.class);// done.
        register0(PURPUR_STAIRS, BlockPurpurStairs.class);// done.
        register0(QUARTZ_BLOCK, BlockQuartzBlock.class);// done.
        register0(QUARTZ_BRICKS, BlockQuartzBricks.class);// done.
        register0(QUARTZ_ORE, BlockQuartzOre.class);// done.
        register0(QUARTZ_STAIRS, BlockQuartzStairs.class);// done.
        register0(RAIL, BlockRail.class);// done.
        register0(RAW_COPPER_BLOCK, BlockRawCopperBlock.class);// done.
        register0(RAW_GOLD_BLOCK, BlockRawGoldBlock.class);// done.
        register0(RAW_IRON_BLOCK, BlockRawIronBlock.class);// done.
        register0(RED_CANDLE, BlockRedCandle.class);// done.
        register0(RED_CANDLE_CAKE, BlockRedCandleCake.class);// done.
        register0(RED_CARPET, BlockRedCarpet.class);// done.
        register0(RED_CONCRETE, BlockRedConcrete.class);// done.
        register0(RED_CONCRETE_POWDER, BlockRedConcretePowder.class);// done.
        register0(RED_FLOWER, BlockRedFlower.class);// done.
        register0(RED_GLAZED_TERRACOTTA, BlockRedGlazedTerracotta.class);// done.
        register0(RED_MUSHROOM, BlockRedMushroom.class);// done.
        register0(RED_MUSHROOM_BLOCK, BlockRedMushroomBlock.class);// done.
        register0(RED_NETHER_BRICK, BlockRedNetherBrick.class);// done.
        register0(RED_NETHER_BRICK_STAIRS, BlockRedNetherBrickStairs.class);// done.
        register0(RED_SANDSTONE, BlockRedSandstone.class);// done.
        register0(RED_SANDSTONE_STAIRS, BlockRedSandstoneStairs.class);// done.
        register0(RED_SHULKER_BOX, BlockRedShulkerBox.class);// done.
        register0(RED_STAINED_GLASS, BlockRedStainedGlass.class);// done.
        register0(RED_STAINED_GLASS_PANE, BlockRedStainedGlassPane.class);// done.
        register0(RED_TERRACOTTA, BlockRedTerracotta.class);// done.
        register0(RED_WOOL, BlockRedWool.class);// done.
        register0(REDSTONE_BLOCK, BlockRedstoneBlock.class);// done.
        register0(REDSTONE_LAMP, BlockRedstoneLamp.class);// done.
        register0(REDSTONE_ORE, BlockRedstoneOre.class);// done.
        register0(REDSTONE_TORCH, BlockRedstoneTorch.class);// done.
        register0(REDSTONE_WIRE, BlockRedstoneWire.class);// done.
        register0(REEDS, BlockReeds.class);// done.
        register0(REINFORCED_DEEPSLATE, BlockReinForcedDeepSlate.class);// done.
        register0(REPEATING_COMMAND_BLOCK, BlockRepeatingCommandBlock.class);// done.
        register0(RESERVED6, BlockReserved6.class);// done.
        register0(RESPAWN_ANCHOR, BlockRespawnAnchor.class);// done.
        register0(SAND, BlockSand.class);// done.
        register0(SANDSTONE, BlockSandstone.class);// done.
        register0(SANDSTONE_STAIRS, BlockSandstoneStairs.class);// done.
        register0(SAPLING, BlockSapling.class);// done.
        register0(SCAFFOLDING, BlockScaffolding.class);// done.
        register0(SCULK, BlockSculk.class);// done.
        register0(SCULK_CATALYST, BlockSculkCatalyst.class);// done.
        register0(SCULK_SENSOR, BlockSculkSensor.class);// done.
        register0(SCULK_SHRIEKER, BlockSculkShrieker.class);// done.
        register0(SCULK_VEIN, BlockSculkVein.class);// done.
        register0(SEA_LANTERN, BlockSeaLantern.class);// done.
        register0(SEA_PICKLE, BlockSeaPickle.class);// done.
        register0(SEAGRASS, BlockSeagrass.class);// done.
        register0(SHROOMLIGHT, BlockShroomlight.class);// done.
        register0(SILVER_GLAZED_TERRACOTTA, BlockSilverGlazedTerracotta.class);// done.
        register0(SKULL, BlockSkull.class);// done.
        register0(SLIME, BlockSlime.class);// done.
        register0(SMALL_AMETHYST_BUD, BlockSmallAmethystBud.class);// done.
        register0(SMALL_DRIPLEAF_BLOCK, BlockSmallDripleafBlock.class);// done.
        register0(SMITHING_TABLE, BlockSmithingTable.class);// done.
        register0(SMOKER, BlockSmoker.class);// done.
        register0(SMOOTH_BASALT, BlockSmoothBasalt.class);// done.
        register0(SMOOTH_QUARTZ_STAIRS, BlockSmoothQuartzStairs.class);// done.
        register0(SMOOTH_RED_SANDSTONE_STAIRS, BlockSmoothRedSandstoneStairs.class);// done.
        register0(SMOOTH_SANDSTONE_STAIRS, BlockSmoothSandstoneStairs.class);// done.
        register0(SMOOTH_STONE, BlockSmoothStone.class);// done.
        register0(SNIFFER_EGG, BlockSnifferEgg.class);// done.
        register0(SNOW, BlockSnow.class);// done.
        register0(SNOW_LAYER, BlockSnowLayer.class);// done.
        register0(SOUL_CAMPFIRE, BlockSoulCampfire.class);// done.
        register0(SOUL_FIRE, BlockSoulFire.class);// done.
        register0(SOUL_LANTERN, BlockSoulLantern.class);// done.
        register0(SOUL_SAND, BlockSoulSand.class);// done.
        register0(SOUL_SOIL, BlockSoulSoil.class);// done.
        register0(SOUL_TORCH, BlockSoulTorch.class);// done.
        register0(SPONGE, BlockSponge.class);// done.
        register0(SPORE_BLOSSOM, BlockSporeBlossom.class);// done.
        register0(SPRUCE_BUTTON, BlockSpruceButton.class);// done.
        register0(SPRUCE_DOOR, BlockSpruceDoor.class);// done.
        register0(SPRUCE_FENCE, BlockSpruceFence.class);// done.
        register0(SPRUCE_FENCE_GATE, BlockSpruceFenceGate.class);// done.
        register0(SPRUCE_HANGING_SIGN, BlockSpruceHangingSign.class);// done.
        register0(SPRUCE_LOG, BlockSpruceLog.class);// done.
        register0(SPRUCE_PLANKS, BlockSprucePlanks.class);// done.
        register0(SPRUCE_PRESSURE_PLATE, BlockSprucePressurePlate.class);// done.
        register0(SPRUCE_STAIRS, BlockSpruceStairs.class);// done.
        register0(SPRUCE_STANDING_SIGN, BlockSpruceStandingSign.class);// done.
        register0(SPRUCE_TRAPDOOR, BlockSpruceTrapdoor.class);// done.
        register0(SPRUCE_WALL_SIGN, BlockSpruceWallSign.class);// done.
        register0(STANDING_BANNER, BlockStandingBanner.class);// done.
        register0(STANDING_SIGN, BlockStandingSign.class);// done.
        register0(STICKY_PISTON, BlockStickyPiston.class);// done.
        register0(STICKY_PISTON_ARM_COLLISION, BlockStickyPistonArmCollision.class);// done.
        register0(STONE, BlockStone.class);// done.
        register0(STONE_BLOCK_SLAB, BlockStoneBlockSlab.class);// done.
        register0(STONE_BLOCK_SLAB2, BlockStoneBlockSlab2.class);// done.
        register0(STONE_BLOCK_SLAB3, BlockStoneBlockSlab3.class);// done.
        register0(STONE_BLOCK_SLAB4, BlockStoneBlockSlab4.class);// done.
        register0(STONE_BRICK_STAIRS, BlockStoneBrickStairs.class);// done.
        register0(STONE_BUTTON, BlockStoneButton.class);// done.
        register0(STONE_PRESSURE_PLATE, BlockStonePressurePlate.class);// done.
        register0(STONE_STAIRS, BlockStoneStairs.class);// done.
        register0(STONEBRICK, BlockStonebrick.class);// done.
        register0(STONECUTTER, BlockStonecutter.class);// done.
        register0(STONECUTTER_BLOCK, BlockStonecutterBlock.class);// done.
        register0(STRIPPED_ACACIA_LOG, BlockStrippedAcaciaLog.class);// done.
        register0(STRIPPED_BAMBOO_BLOCK, BlockStrippedBambooBlock.class);// done.
        register0(STRIPPED_BIRCH_LOG, BlockStrippedBirchLog.class);// done.
        register0(STRIPPED_CHERRY_LOG, BlockStrippedCherryLog.class);// done.
        register0(STRIPPED_CHERRY_WOOD, BlockStrippedCherryWood.class);// done.
        register0(STRIPPED_CRIMSON_HYPHAE, BlockStrippedCrimsonHyphae.class);// done.
        register0(STRIPPED_CRIMSON_STEM, BlockStrippedCrimsonStem.class);// done.
        register0(STRIPPED_DARK_OAK_LOG, BlockStrippedDarkOakLog.class);// done.
        register0(STRIPPED_JUNGLE_LOG, BlockStrippedJungleLog.class);// done.
        register0(STRIPPED_MANGROVE_LOG, BlockStrippedMangroveLog.class);// done.
        register0(STRIPPED_MANGROVE_WOOD, BlockStrippedMangroveWood.class);// done.
        register0(STRIPPED_OAK_LOG, BlockStrippedOakLog.class);// done.
        register0(STRIPPED_SPRUCE_LOG, BlockStrippedSpruceLog.class);// done.
        register0(STRIPPED_WARPED_HYPHAE, BlockStrippedWarpedHyphae.class);// done.
        register0(STRIPPED_WARPED_STEM, BlockStrippedWarpedStem.class);// done.
        register0(STRUCTURE_BLOCK, BlockStructureBlock.class);// done.
        register0(STRUCTURE_VOID, BlockStructureVoid.class);// done.
        register0(SUSPICIOUS_GRAVEL, BlockSuspiciousGravel.class);// done.
        register0(SUSPICIOUS_SAND, BlockSuspiciousSand.class);// done.
        register0(SWEET_BERRY_BUSH, BlockSweetBerryBush.class);// done.
        register0(TALLGRASS, BlockTallgrass.class);// done.
        register0(TARGET, BlockTarget.class);// done.
        register0(TINTED_GLASS, BlockTintedGlass.class);// done.
        register0(TNT, BlockTnt.class);// done.
        register0(TORCH, BlockTorch.class);// done.
        register0(TORCHFLOWER, BlockTorchflower.class);// done.
        register0(TORCHFLOWER_CROP, BlockTorchflowerCrop.class);// done.
        register0(TRAPPED_CHEST, BlockTrappedChest.class);// done.
        register0(TRIP_WIRE, BlockTripWire.class);// done.
        register0(TRIPWIRE_HOOK, BlockTripwireHook.class);// done.
        register0(TUBE_CORAL, BlockTubeCoral.class);// done.
        register0(TUFF, BlockTuff.class);// done.
//        register0(TUFF_BRICK_DOUBLE_SLAB, BlockTuffBrickDoubleSlab.class);// experimental
//        register0(TUFF_BRICK_SLAB, BlockTuffBrickSlab.class);// experimental
//        register0(TUFF_BRICK_STAIRS, BlockTuffBrickStairs.class);// experimental
//        register0(TUFF_BRICK_WALL, BlockTuffBrickWall.class);// experimental
//        register0(TUFF_BRICKS, BlockTuffBricks.class);// experimental
//        register0(TUFF_DOUBLE_SLAB, BlockTuffDoubleSlab.class);// experimental
//        register0(TUFF_SLAB, BlockTuffSlab.class);// experimental
//        register0(TUFF_STAIRS, BlockTuffStairs.class);// experimental
//        register0(TUFF_WALL, BlockTuffWall.class);// experimental
        register0(TURTLE_EGG, BlockTurtleEgg.class);// done.
        register0(TWISTING_VINES, BlockTwistingVines.class);// done.
//        register0(UNDERWATER_TORCH, BlockUnderwaterTorch.class);//edu
        register0(UNDYED_SHULKER_BOX, BlockUndyedShulkerBox.class);// done.
        register0(UNKNOWN, BlockUnknown.class);// done.
        register0(UNLIT_REDSTONE_TORCH, BlockUnlitRedstoneTorch.class);// done.
        register0(UNPOWERED_COMPARATOR, BlockUnpoweredComparator.class);// done.
        register0(UNPOWERED_REPEATER, BlockUnpoweredRepeater.class);// done.
        register0(VERDANT_FROGLIGHT, BlockVerdantFroglight.class);// done.
        register0(VINE, BlockVine.class);// done.
        register0(WALL_BANNER, BlockWallBanner.class);// done.
        register0(WALL_SIGN, BlockWallSign.class);// done.
        register0(WARPED_BUTTON, BlockWarpedButton.class);// done.
        register0(WARPED_DOOR, BlockWarpedDoor.class);// done.
        register0(WARPED_DOUBLE_SLAB, BlockWarpedDoubleSlab.class);// done.
        register0(WARPED_FENCE, BlockWarpedFence.class);// done.
        register0(WARPED_FENCE_GATE, BlockWarpedFenceGate.class);// done.
        register0(WARPED_FUNGUS, BlockWarpedFungus.class);// done.
        register0(WARPED_HANGING_SIGN, BlockWarpedHangingSign.class);// done.
        register0(WARPED_HYPHAE, BlockWarpedHyphae.class);// done.
        register0(WARPED_NYLIUM, BlockWarpedNylium.class);// done.
        register0(WARPED_PLANKS, BlockWarpedPlanks.class);// done.
        register0(WARPED_PRESSURE_PLATE, BlockWarpedPressurePlate.class);// done.
        register0(WARPED_ROOTS, BlockWarpedRoots.class);// done.
        register0(WARPED_SLAB, BlockWarpedSlab.class);// done.
        register0(WARPED_STAIRS, BlockWarpedStairs.class);// done.
        register0(WARPED_STANDING_SIGN, BlockWarpedStandingSign.class);// done.
        register0(WARPED_STEM, BlockWarpedStem.class);// done.
        register0(WARPED_TRAPDOOR, BlockWarpedTrapdoor.class);// done.
        register0(WARPED_WALL_SIGN, BlockWarpedWallSign.class);// done.
        register0(WARPED_WART_BLOCK, BlockWarpedWartBlock.class);// done.
        register0(WATER, BlockWater.class);// done.
        register0(WATERLILY, BlockWaterlily.class);// done.
//        register0(WAXED_CHISELED_COPPER, BlockWaxedChiseledCopper.class);// experimental
        register0(WAXED_COPPER, BlockWaxedCopper.class);// done.
//        register0(WAXED_COPPER_BULB, BlockWaxedCopperBulb.class);// experimental
//        register0(WAXED_COPPER_DOOR, BlockWaxedCopperDoor.class);// experimental
//        register0(WAXED_COPPER_GRATE, BlockWaxedCopperGrate.class);// experimental
//        register0(WAXED_COPPER_TRAPDOOR, BlockWaxedCopperTrapdoor.class);// experimental
        register0(WAXED_CUT_COPPER, BlockWaxedCutCopper.class);// done.
        register0(WAXED_CUT_COPPER_SLAB, BlockWaxedCutCopperSlab.class);// done.
        register0(WAXED_CUT_COPPER_STAIRS, BlockWaxedCutCopperStairs.class);// done.
        register0(WAXED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedDoubleCutCopperSlab.class);// done.
//        register0(WAXED_EXPOSED_CHISELED_COPPER, BlockWaxedExposedChiseledCopper.class);// experimental
        register0(WAXED_EXPOSED_COPPER, BlockWaxedExposedCopper.class);// done.
//        register0(WAXED_EXPOSED_COPPER_BULB, BlockWaxedExposedCopperBulb.class);// experimental
//        register0(WAXED_EXPOSED_COPPER_DOOR, BlockWaxedExposedCopperDoor.class);// experimental
//        register0(WAXED_EXPOSED_COPPER_GRATE, BlockWaxedExposedCopperGrate.class);// experimental
//        register0(WAXED_EXPOSED_COPPER_TRAPDOOR, BlockWaxedExposedCopperTrapdoor.class);// experimental
        register0(WAXED_EXPOSED_CUT_COPPER, BlockWaxedExposedCutCopper.class);// done.
        register0(WAXED_EXPOSED_CUT_COPPER_SLAB, BlockWaxedExposedCutCopperSlab.class);// done.
        register0(WAXED_EXPOSED_CUT_COPPER_STAIRS, BlockWaxedExposedCutCopperStairs.class);// done.
        register0(WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedExposedDoubleCutCopperSlab.class);// done.
//        register0(WAXED_OXIDIZED_CHISELED_COPPER, BlockWaxedOxidizedChiseledCopper.class);// experimental
        register0(WAXED_OXIDIZED_COPPER, BlockWaxedOxidizedCopper.class);// done.
//        register0(WAXED_OXIDIZED_COPPER_BULB, BlockWaxedOxidizedCopperBulb.class);// experimental
//        register0(WAXED_OXIDIZED_COPPER_DOOR, BlockWaxedOxidizedCopperDoor.class);// experimental
//        register0(WAXED_OXIDIZED_COPPER_GRATE, BlockWaxedOxidizedCopperGrate.class);// experimental
//        register0(WAXED_OXIDIZED_COPPER_TRAPDOOR, BlockWaxedOxidizedCopperTrapdoor.class);// experimental
        register0(WAXED_OXIDIZED_CUT_COPPER, BlockWaxedOxidizedCutCopper.class);// done.
        register0(WAXED_OXIDIZED_CUT_COPPER_SLAB, BlockWaxedOxidizedCutCopperSlab.class);// done.
        register0(WAXED_OXIDIZED_CUT_COPPER_STAIRS, BlockWaxedOxidizedCutCopperStairs.class);// done.
        register0(WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedOxidizedDoubleCutCopperSlab.class);// done.
//        register0(WAXED_WEATHERED_CHISELED_COPPER, BlockWaxedWeatheredChiseledCopper.class);// experimental
        register0(WAXED_WEATHERED_COPPER, BlockWaxedWeatheredCopper.class);// done.
//        register0(WAXED_WEATHERED_COPPER_BULB, BlockWaxedWeatheredCopperBulb.class);// experimental
//        register0(WAXED_WEATHERED_COPPER_DOOR, BlockWaxedWeatheredCopperDoor.class);// experimental
//        register0(WAXED_WEATHERED_COPPER_GRATE, BlockWaxedWeatheredCopperGrate.class);// experimental
//        register0(WAXED_WEATHERED_COPPER_TRAPDOOR, BlockWaxedWeatheredCopperTrapdoor.class);// experimental
        register0(WAXED_WEATHERED_CUT_COPPER, BlockWaxedWeatheredCutCopper.class);// done.
        register0(WAXED_WEATHERED_CUT_COPPER_SLAB, BlockWaxedWeatheredCutCopperSlab.class);// done.
        register0(WAXED_WEATHERED_CUT_COPPER_STAIRS, BlockWaxedWeatheredCutCopperStairs.class);// done.
        register0(WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedWeatheredDoubleCutCopperSlab.class);// done.
//        register0(WEATHERED_CHISELED_COPPER, BlockWeatheredChiseledCopper.class);// experimental
        register0(WEATHERED_COPPER, BlockWeatheredCopper.class);// done.
//        register0(WEATHERED_COPPER_BULB, BlockWeatheredCopperBulb.class);// experimental
//        register0(WEATHERED_COPPER_DOOR, BlockWeatheredCopperDoor.class);// experimental
//        register0(WEATHERED_COPPER_GRATE, BlockWeatheredCopperGrate.class);// experimental
//        register0(WEATHERED_COPPER_TRAPDOOR, BlockWeatheredCopperTrapdoor.class);// experimental
        register0(WEATHERED_CUT_COPPER, BlockWeatheredCutCopper.class);// done.
        register0(WEATHERED_CUT_COPPER_SLAB, BlockWeatheredCutCopperSlab.class);// done.
        register0(WEATHERED_CUT_COPPER_STAIRS, BlockWeatheredCutCopperStairs.class);// done.
        register0(WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockWeatheredDoubleCutCopperSlab.class);// done.
        register0(WEB, BlockWeb.class);// done.
        register0(WEEPING_VINES, BlockWeepingVines.class);// done.
        register0(WHEAT, BlockWheat.class);// done.
        register0(WHITE_CANDLE, BlockWhiteCandle.class);// done.
        register0(WHITE_CANDLE_CAKE, BlockWhiteCandleCake.class);// done.
        register0(WHITE_CARPET, BlockWhiteCarpet.class);// done.
        register0(WHITE_CONCRETE, BlockWhiteConcrete.class);// done.
        register0(WHITE_CONCRETE_POWDER, BlockWhiteConcretePowder.class);// done.
        register0(WHITE_GLAZED_TERRACOTTA, BlockWhiteGlazedTerracotta.class);// done.
        register0(WHITE_SHULKER_BOX, BlockWhiteShulkerBox.class);// done.
        register0(WHITE_STAINED_GLASS, BlockWhiteStainedGlass.class);// done.
        register0(WHITE_STAINED_GLASS_PANE, BlockWhiteStainedGlassPane.class);// done.
        register0(WHITE_TERRACOTTA, BlockWhiteTerracotta.class);// done.
        register0(WHITE_WOOL, BlockWhiteWool.class);// done.
        register0(WITHER_ROSE, BlockWitherRose.class);// done.
        register0(WOOD, BlockWood.class);// done.
        register0(WOODEN_BUTTON, BlockWoodenButton.class);// done.
        register0(WOODEN_DOOR, BlockWoodenDoor.class);// done.
        register0(WOODEN_PRESSURE_PLATE, BlockWoodenPressurePlate.class);// done.
        register0(WOODEN_SLAB, BlockWoodenSlab.class);// done.
        register0(YELLOW_CANDLE, BlockYellowCandle.class);// done.
        register0(YELLOW_CANDLE_CAKE, BlockYellowCandleCake.class);// done.
        register0(YELLOW_CARPET, BlockYellowCarpet.class);// done.
        register0(YELLOW_CONCRETE, BlockYellowConcrete.class);// done.
        register0(YELLOW_CONCRETE_POWDER, BlockYellowConcretePowder.class);// done.
        register0(YELLOW_FLOWER, BlockYellowFlower.class);// done.
        register0(YELLOW_GLAZED_TERRACOTTA, BlockYellowGlazedTerracotta.class);// done.
        register0(YELLOW_SHULKER_BOX, BlockYellowShulkerBox.class);// done.
        register0(YELLOW_STAINED_GLASS, BlockYellowStainedGlass.class);// done.
        register0(YELLOW_STAINED_GLASS_PANE, BlockYellowStainedGlassPane.class);// done.
        register0(YELLOW_TERRACOTTA, BlockYellowTerracotta.class);// done.
        register0(YELLOW_WOOL, BlockYellowWool.class);// done.
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
        if (Modifier.isAbstract(value.getModifiers())) {
            throw new RegisterException("you cant register a abstract block class!");
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
            throw new RegisterException("you cant register a abstract block class!");
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
                }else{
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
