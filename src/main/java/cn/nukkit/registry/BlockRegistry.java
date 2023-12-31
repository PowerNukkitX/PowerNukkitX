package cn.nukkit.registry;

import cn.nukkit.block.*;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.sunlan.fastreflection.FastConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Cool_Loong | Mcayear | KoshakMineDEV | WWMB
 */
public final class BlockRegistry extends BaseRegistry<String, Block, Class<? extends Block>> implements BlockID {
    private static final Set<String> KEYSET = new HashSet<>();
    private static final Object2ObjectOpenHashMap<String, FastConstructor<? extends Block>> CACHE_CONSTRUCTORS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, BlockProperties> PROPERTIES = new Object2ObjectOpenHashMap<>();

    @Override
    public void init() {
        register(ACACIA_BUTTON, BlockAcaciaButton.class);// done.
        register(ACACIA_DOOR, BlockAcaciaDoor.class);// done.
        register(ACACIA_FENCE, BlockAcaciaFence.class);// done.
        register(ACACIA_FENCE_GATE, BlockAcaciaFenceGate.class);// done.
        register(ACACIA_HANGING_SIGN, BlockAcaciaHangingSign.class);// done.
        register(ACACIA_LOG, BlockAcaciaLog.class);// done.
        register(ACACIA_PLANKS, BlockAcaciaPlanks.class);// done.
        register(ACACIA_PRESSURE_PLATE, BlockAcaciaPressurePlate.class);// done.
        register(ACACIA_STAIRS, BlockAcaciaStairs.class);// done.
        register(ACACIA_STANDING_SIGN, BlockAcaciaStandingSign.class);// done.
        register(ACACIA_TRAPDOOR, BlockAcaciaTrapdoor.class);// done.
        register(ACACIA_WALL_SIGN, BlockAcaciaWallSign.class);// done.
        register(ACTIVATOR_RAIL, BlockActivatorRail.class);// done.
        register(AIR, BlockAir.class);// done.
        register(ALLOW, BlockAllow.class);// done.
        register(AMETHYST_BLOCK, BlockAmethystBlock.class);// done.
        register(AMETHYST_CLUSTER, BlockAmethystCluster.class);// done.
        register(ANCIENT_DEBRIS, BlockAncientDebris.class);// done.
        register(ANDESITE, BlockAndesite.class);// done.
        register(ANDESITE_STAIRS, BlockAndesiteStairs.class);// done.
        register(ANVIL, BlockAnvil.class);// done.
        register(AZALEA, BlockAzalea.class);// done.
        register(AZALEA_LEAVES, BlockAzaleaLeaves.class);// done.
        register(AZALEA_LEAVES_FLOWERED, BlockAzaleaLeavesFlowered.class);// done.
        register(BAMBOO, BlockBamboo.class);// done.
        register(BAMBOO_BLOCK, BlockBambooBlock.class);// done.
        register(BAMBOO_BUTTON, BlockBambooButton.class);// done.
        register(BAMBOO_DOOR, BlockBambooDoor.class);// done.
        register(BAMBOO_DOUBLE_SLAB, BlockBambooDoubleSlab.class);// done.
        register(BAMBOO_FENCE, BlockBambooFence.class);// done.
        register(BAMBOO_FENCE_GATE, BlockBambooFenceGate.class);// done.
        register(BAMBOO_HANGING_SIGN, BlockBambooHangingSign.class);// done.
        register(BAMBOO_MOSAIC, BlockBambooMosaic.class);// done.
        register(BAMBOO_MOSAIC_DOUBLE_SLAB, BlockBambooMosaicDoubleSlab.class);// done.
        register(BAMBOO_MOSAIC_SLAB, BlockBambooMosaicSlab.class);// done.
        register(BAMBOO_MOSAIC_STAIRS, BlockBambooMosaicStairs.class);// done.
        register(BAMBOO_PLANKS, BlockBambooPlanks.class);// done.
        register(BAMBOO_PRESSURE_PLATE, BlockBambooPressurePlate.class);// done.
        register(BAMBOO_SAPLING, BlockBambooSapling.class);// done.
        register(BAMBOO_SLAB, BlockBambooSlab.class);// done.
        register(BAMBOO_STAIRS, BlockBambooStairs.class);// done.
        register(BAMBOO_STANDING_SIGN, BlockBambooStandingSign.class);// done.
        register(BAMBOO_TRAPDOOR, BlockBambooTrapdoor.class);// done.
        register(BAMBOO_WALL_SIGN, BlockBambooWallSign.class);// done.
        register(BARREL, BlockBarrel.class);// done.
        register(BARRIER, BlockBarrier.class);// done.
        register(BASALT, BlockBasalt.class);// done.
        register(BEACON, BlockBeacon.class);// done.
        register(BED, BlockBed.class);// done.
        register(BEDROCK, BlockBedrock.class);// done.
        register(BEE_NEST, BlockBeeNest.class);// done.
        register(BEEHIVE, BlockBeehive.class);// done.
        register(BEETROOT, BlockBeetroot.class);// done.
        register(BELL, BlockBell.class);// done.
        register(BIG_DRIPLEAF, BlockBigDripleaf.class);// done.
        register(BIRCH_BUTTON, BlockBirchButton.class);// done.
        register(BIRCH_DOOR, BlockBirchDoor.class);// done.
        register(BIRCH_FENCE, BlockBirchFence.class);// done.
        register(BIRCH_FENCE_GATE, BlockBirchFenceGate.class);// done.
        register(BIRCH_HANGING_SIGN, BlockBirchHangingSign.class);// done.
        register(BIRCH_LOG, BlockBirchLog.class);// done.
        register(BIRCH_PLANKS, BlockBirchPlanks.class);// done.
        register(BIRCH_PRESSURE_PLATE, BlockBirchPressurePlate.class);// done.
        register(BIRCH_STAIRS, BlockBirchStairs.class);// done.
        register(BIRCH_STANDING_SIGN, BlockBirchStandingSign.class);// done.
        register(BIRCH_TRAPDOOR, BlockBirchTrapdoor.class);// done.
        register(BIRCH_WALL_SIGN, BlockBirchWallSign.class);// done.
        register(BLACK_CANDLE, BlockBlackCandle.class);// done.
        register(BLACK_CANDLE_CAKE, BlockBlackCandleCake.class);// done.
        register(BLACK_CARPET, BlockBlackCarpet.class);// done.
        register(BLACK_CONCRETE, BlockBlackConcrete.class);// done.
        register(BLACK_CONCRETE_POWDER, BlockBlackConcretePowder.class);// done.
        register(BLACK_GLAZED_TERRACOTTA, BlockBlackGlazedTerracotta.class);// done.
        register(BLACK_SHULKER_BOX, BlockBlackShulkerBox.class);// done.
        register(BLACK_STAINED_GLASS, BlockBlackStainedGlass.class);// done.
        register(BLACK_STAINED_GLASS_PANE, BlockBlackStainedGlassPane.class);// done.
        register(BLACK_TERRACOTTA, BlockBlackTerracotta.class);// done.
        register(BLACK_WOOL, BlockBlackWool.class);// done.
        register(BLACKSTONE, BlockBlackstone.class);// done.
        register(BLACKSTONE_DOUBLE_SLAB, BlockBlackstoneDoubleSlab.class);// done.
        register(BLACKSTONE_SLAB, BlockBlackstoneSlab.class);// done.
        register(BLACKSTONE_STAIRS, BlockBlackstoneStairs.class);// done.
        register(BLACKSTONE_WALL, BlockBlackstoneWall.class);// done.
        register(BLAST_FURNACE, BlockBlastFurnace.class);// done.
        register(BLUE_CANDLE, BlockBlueCandle.class);// done.
        register(BLUE_CANDLE_CAKE, BlockBlueCandleCake.class);// done.
        register(BLUE_CARPET, BlockBlueCarpet.class);// done.
        register(BLUE_CONCRETE, BlockBlueConcrete.class);// done.
        register(BLUE_CONCRETE_POWDER, BlockBlueConcretePowder.class);// done.
        register(BLUE_GLAZED_TERRACOTTA, BlockBlueGlazedTerracotta.class);// done.
        register(BLUE_ICE, BlockBlueIce.class);// done.
        register(BLUE_SHULKER_BOX, BlockBlueShulkerBox.class);// done.
        register(BLUE_STAINED_GLASS, BlockBlueStainedGlass.class);// done.
        register(BLUE_STAINED_GLASS_PANE, BlockBlueStainedGlassPane.class);// done.
        register(BLUE_TERRACOTTA, BlockBlueTerracotta.class);// done.
        register(BLUE_WOOL, BlockBlueWool.class);// done.
        register(BONE_BLOCK, BlockBoneBlock.class);// done.
        register(BOOKSHELF, BlockBookshelf.class);// done.
        register(BORDER_BLOCK, BlockBorderBlock.class);// done.
        register(BRAIN_CORAL, BlockBrainCoral.class);// done.
        register(BREWING_STAND, BlockBrewingStand.class);// done.
        register(BRICK_BLOCK, BlockBrickBlock.class);// done.
        register(BRICK_STAIRS, BlockBrickStairs.class);// done.
        register(BROWN_CANDLE, BlockBrownCandle.class);// done.
        register(BROWN_CANDLE_CAKE, BlockBrownCandleCake.class);// done.
        register(BROWN_CARPET, BlockBrownCarpet.class);// done.
        register(BROWN_CONCRETE, BlockBrownConcrete.class);// done.
        register(BROWN_CONCRETE_POWDER, BlockBrownConcretePowder.class);// done.
        register(BROWN_GLAZED_TERRACOTTA, BlockBrownGlazedTerracotta.class);// done.
        register(BROWN_MUSHROOM, BlockBrownMushroom.class);// done.
        register(BROWN_MUSHROOM_BLOCK, BlockBrownMushroomBlock.class);// done.
        register(BROWN_SHULKER_BOX, BlockBrownShulkerBox.class);// done.
        register(BROWN_STAINED_GLASS, BlockBrownStainedGlass.class);// done.
        register(BROWN_STAINED_GLASS_PANE, BlockBrownStainedGlassPane.class);// done.
        register(BROWN_TERRACOTTA, BlockBrownTerracotta.class);// done.
        register(BROWN_WOOL, BlockBrownWool.class);// done.
        register(BUBBLE_COLUMN, BlockBubbleColumn.class);// done.
        register(BUBBLE_CORAL, BlockBubbleCoral.class);// done.
        register(BUDDING_AMETHYST, BlockBuddingAmethyst.class);// done.
        register(CACTUS, BlockCactus.class);// done.
        register(CAKE, BlockCake.class);// done.
        register(CALCITE, BlockCalcite.class);// done.
        register(CALIBRATED_SCULK_SENSOR, BlockCalibratedSculkSensor.class);// done.
//        register(CAMERA, BlockCamera.class);//edu
        register(CAMPFIRE, BlockCampfire.class);// done.
        register(CANDLE, BlockCandle.class);// done.
        register(CANDLE_CAKE, BlockCandleCake.class);// done.
        register(CARROTS, BlockCarrots.class);// done.
        register(CARTOGRAPHY_TABLE, BlockCartographyTable.class);// done.
        register(CARVED_PUMPKIN, BlockCarvedPumpkin.class);// done.
        register(CAULDRON, BlockCauldron.class);// done.
        register(CAVE_VINES, BlockCaveVines.class);// done.
        register(CAVE_VINES_BODY_WITH_BERRIES, BlockCaveVinesBodyWithBerries.class);// done.
        register(CAVE_VINES_HEAD_WITH_BERRIES, BlockCaveVinesHeadWithBerries.class);// done.
        register(CHAIN, BlockChain.class);// done.
        register(CHAIN_COMMAND_BLOCK, BlockChainCommandBlock.class);// done.
//        register(CHEMICAL_HEAT, BlockChemicalHeat.class);//edu
//        register(CHEMISTRY_TABLE, BlockChemistryTable.class);//edu
        register(CHERRY_BUTTON, BlockCherryButton.class);// done.
        register(CHERRY_DOOR, BlockCherryDoor.class);// done.
        register(CHERRY_DOUBLE_SLAB, BlockCherryDoubleSlab.class);
        register(CHERRY_FENCE, BlockCherryFence.class);// done.
        register(CHERRY_FENCE_GATE, BlockCherryFenceGate.class);// done.
        register(CHERRY_HANGING_SIGN, BlockCherryHangingSign.class);// done.
        register(CHERRY_LEAVES, BlockCherryLeaves.class);// done.
        register(CHERRY_LOG, BlockCherryLog.class);// done.
        register(CHERRY_PLANKS, BlockCherryPlanks.class);// done.
        register(CHERRY_PRESSURE_PLATE, BlockCherryPressurePlate.class);
        register(CHERRY_SAPLING, BlockCherrySapling.class);// done.
        register(CHERRY_SLAB, BlockCherrySlab.class);
        register(CHERRY_STAIRS, BlockCherryStairs.class);
        register(CHERRY_STANDING_SIGN, BlockCherryStandingSign.class);// done.
        register(CHERRY_TRAPDOOR, BlockCherryTrapdoor.class);// done.
        register(CHERRY_WALL_SIGN, BlockCherryWallSign.class);// done.
        register(CHERRY_WOOD, BlockCherryWood.class);
        register(CHEST, BlockChest.class);// done.
        register(CHISELED_BOOKSHELF, BlockChiseledBookshelf.class);// done.
//        register(CHISELED_COPPER, BlockChiseledCopper.class);// experimental
        register(CHISELED_DEEPSLATE, BlockChiseledDeepslate.class);// done.
        register(CHISELED_NETHER_BRICKS, BlockChiseledNetherBricks.class);
        register(CHISELED_POLISHED_BLACKSTONE, BlockChiseledPolishedBlackstone.class);
//        register(CHISELED_TUFF, BlockChiseledTuff.class);// experimental
        register(CHISELED_TUFF_BRICKS, BlockChiseledTuffBricks.class);
        register(CHORUS_FLOWER, BlockChorusFlower.class);// done.
        register(CHORUS_PLANT, BlockChorusPlant.class);// done.
        register(CLAY, BlockClay.class);// done.
        register(CLIENT_REQUEST_PLACEHOLDER_BLOCK, BlockClientRequestPlaceholderBlock.class);// done.
        register(COAL_BLOCK, BlockCoalBlock.class);// done.
        register(COAL_ORE, BlockCoalOre.class);// done.
        register(COBBLED_DEEPSLATE, BlockCobbledDeepslate.class);// done.
        register(COBBLED_DEEPSLATE_DOUBLE_SLAB, BlockCobbledDeepslateDoubleSlab.class);
        register(COBBLED_DEEPSLATE_SLAB, BlockCobbledDeepslateSlab.class);
        register(COBBLED_DEEPSLATE_STAIRS, BlockCobbledDeepslateStairs.class);
        register(COBBLED_DEEPSLATE_WALL, BlockCobbledDeepslateWall.class);
        register(COBBLESTONE, BlockCobblestone.class);
        register(COBBLESTONE_WALL, BlockCobblestoneWall.class);
        register(COCOA, BlockCocoa.class);// done.
//        register(COLORED_TORCH_BP, BlockColoredTorchBp.class);//edu
//        register(COLORED_TORCH_RG, BlockColoredTorchRg.class);//edu
        register(COMMAND_BLOCK, BlockCommandBlock.class);// done.
        register(COMPOSTER, BlockComposter.class);// done.
        register(CONDUIT, BlockConduit.class);// done.
        register(COPPER_BLOCK, BlockCopperBlock.class);// done.
//        register(COPPER_BULB, BlockCopperBulb.class);// experiment
        register(COPPER_DOOR, BlockCopperDoor.class);// done.
//        register(COPPER_GRATE, BlockCopperGrate.class);//experimental
        register(COPPER_ORE, BlockCopperOre.class);// done.
        register(COPPER_TRAPDOOR, BlockCopperTrapdoor.class);
        register(CORAL_BLOCK, BlockCoralBlock.class);// done.
        register(CORAL_FAN, BlockCoralFan.class);// done.
        register(CORAL_FAN_DEAD, BlockCoralFanDead.class);// done.
        register(CORAL_FAN_HANG, BlockCoralFanHang.class);// done.
        register(CORAL_FAN_HANG2, BlockCoralFanHang2.class);// done.
        register(CORAL_FAN_HANG3, BlockCoralFanHang3.class);// done.
        register(CRACKED_DEEPSLATE_BRICKS, BlockCrackedDeepslateBricks.class);
        register(CRACKED_DEEPSLATE_TILES, BlockCrackedDeepslateTiles.class);
        register(CRACKED_NETHER_BRICKS, BlockCrackedNetherBricks.class);
        register(CRACKED_POLISHED_BLACKSTONE_BRICKS, BlockCrackedPolishedBlackstoneBricks.class);
//        register(CRAFTER, BlockCrafter.class);//experimental
        register(CRAFTING_TABLE, BlockCraftingTable.class);// done.
        register(CRIMSON_BUTTON, BlockCrimsonButton.class);// done.
        register(CRIMSON_DOOR, BlockCrimsonDoor.class);// done.
        register(CRIMSON_DOUBLE_SLAB, BlockCrimsonDoubleSlab.class);
        register(CRIMSON_FENCE, BlockCrimsonFence.class);// done.
        register(CRIMSON_FENCE_GATE, BlockCrimsonFenceGate.class);// done.
        register(CRIMSON_FUNGUS, BlockCrimsonFungus.class);// done.
        register(CRIMSON_HANGING_SIGN, BlockCrimsonHangingSign.class);// done.
        register(CRIMSON_HYPHAE, BlockCrimsonHyphae.class);// done.
        register(CRIMSON_NYLIUM, BlockCrimsonNylium.class);// done.
        register(CRIMSON_PLANKS, BlockCrimsonPlanks.class);// done.
        register(CRIMSON_PRESSURE_PLATE, BlockCrimsonPressurePlate.class);
        register(CRIMSON_ROOTS, BlockCrimsonRoots.class);// done.
        register(CRIMSON_SLAB, BlockCrimsonSlab.class);
        register(CRIMSON_STAIRS, BlockCrimsonStairs.class);
        register(CRIMSON_STANDING_SIGN, BlockCrimsonStandingSign.class);// done.
        register(CRIMSON_STEM, BlockCrimsonStem.class);// done.
        register(CRIMSON_TRAPDOOR, BlockCrimsonTrapdoor.class);// done.
        register(CRIMSON_WALL_SIGN, BlockCrimsonWallSign.class);// done.
        register(CRYING_OBSIDIAN, BlockCryingObsidian.class);// done.
        register(CUT_COPPER, BlockCutCopper.class);// done.
        register(CUT_COPPER_SLAB, BlockCutCopperSlab.class);
        register(CUT_COPPER_STAIRS, BlockCutCopperStairs.class);
        register(CYAN_CANDLE, BlockCyanCandle.class);// done.
        register(CYAN_CANDLE_CAKE, BlockCyanCandleCake.class);// done.
        register(CYAN_CARPET, BlockCyanCarpet.class);// done.
        register(CYAN_CONCRETE, BlockCyanConcrete.class);// done.
        register(CYAN_CONCRETE_POWDER, BlockCyanConcretePowder.class);// done.
        register(CYAN_GLAZED_TERRACOTTA, BlockCyanGlazedTerracotta.class);// done.
        register(CYAN_SHULKER_BOX, BlockCyanShulkerBox.class);// done.
        register(CYAN_STAINED_GLASS, BlockCyanStainedGlass.class);// done.
        register(CYAN_STAINED_GLASS_PANE, BlockCyanStainedGlassPane.class);// done.
        register(CYAN_TERRACOTTA, BlockCyanTerracotta.class);// done.
        register(CYAN_WOOL, BlockCyanWool.class);// done.
        register(DARK_OAK_BUTTON, BlockDarkOakButton.class);// done.
        register(DARK_OAK_DOOR, BlockDarkOakDoor.class);// done.
        register(DARK_OAK_FENCE, BlockDarkOakFence.class);// done.
        register(DARK_OAK_FENCE_GATE, BlockDarkOakFenceGate.class);// done.
        register(DARK_OAK_HANGING_SIGN, BlockDarkOakHangingSign.class);// done.
        register(DARK_OAK_LOG, BlockDarkOakLog.class);// done.
        register(DARK_OAK_PLANKS, BlockDarkOakPlanks.class);// done.
        register(DARK_OAK_PRESSURE_PLATE, BlockDarkOakPressurePlate.class);
        register(DARK_OAK_STAIRS, BlockDarkOakStairs.class);
        register(DARK_OAK_TRAPDOOR, BlockDarkOakTrapdoor.class);// done.
        register(DARK_PRISMARINE_STAIRS, BlockDarkPrismarineStairs.class);
        register(DARKOAK_STANDING_SIGN, BlockDarkoakStandingSign.class);// done.
        register(DARKOAK_WALL_SIGN, BlockDarkoakWallSign.class);// done.
        register(DAYLIGHT_DETECTOR, BlockDaylightDetector.class);// done.
        register(DAYLIGHT_DETECTOR_INVERTED, BlockDaylightDetectorInverted.class);// done.
        register(DEAD_BRAIN_CORAL, BlockDeadBrainCoral.class);// done.
        register(DEAD_BUBBLE_CORAL, BlockDeadBubbleCoral.class);// done.
        register(DEAD_FIRE_CORAL, BlockDeadFireCoral.class);// done.
        register(DEAD_HORN_CORAL, BlockDeadHornCoral.class);// done.
        register(DEAD_TUBE_CORAL, BlockDeadTubeCoral.class);// done.
        register(DEADBUSH, BlockDeadbush.class);// done.
        register(DECORATED_POT, BlockDecoratedPot.class);// done.
        register(DEEPSLATE, BlockDeepslate.class);// done.
        register(DEEPSLATE_BRICK_DOUBLE_SLAB, BlockDeepslateBrickDoubleSlab.class);
        register(DEEPSLATE_BRICK_SLAB, BlockDeepslateBrickSlab.class);
        register(DEEPSLATE_BRICK_STAIRS, BlockDeepslateBrickStairs.class);
        register(DEEPSLATE_BRICK_WALL, BlockDeepslateBrickWall.class);
        register(DEEPSLATE_BRICKS, BlockDeepslateBricks.class);
        register(DEEPSLATE_COAL_ORE, BlockDeepslateCoalOre.class);// done.
        register(DEEPSLATE_COPPER_ORE, BlockDeepslateCopperOre.class);// done.
        register(DEEPSLATE_DIAMOND_ORE, BlockDeepslateDiamondOre.class);// done.
        register(DEEPSLATE_EMERALD_ORE, BlockDeepslateEmeraldOre.class);// done.
        register(DEEPSLATE_GOLD_ORE, BlockDeepslateGoldOre.class);// done.
        register(DEEPSLATE_IRON_ORE, BlockDeepslateIronOre.class);// done.
        register(DEEPSLATE_LAPIS_ORE, BlockDeepslateLapisOre.class);// done.
        register(DEEPSLATE_REDSTONE_ORE, BlockDeepslateRedstoneOre.class);// done.
        register(DEEPSLATE_TILE_DOUBLE_SLAB, BlockDeepslateTileDoubleSlab.class);
        register(DEEPSLATE_TILE_SLAB, BlockDeepslateTileSlab.class);
        register(DEEPSLATE_TILE_STAIRS, BlockDeepslateTileStairs.class);
        register(DEEPSLATE_TILE_WALL, BlockDeepslateTileWall.class);
        register(DEEPSLATE_TILES, BlockDeepslateTiles.class);// done.
        register(DENY, BlockDeny.class);// done.
        register(DETECTOR_RAIL, BlockDetectorRail.class);// done.
        register(DIAMOND_BLOCK, BlockDiamondBlock.class);// done.
        register(DIAMOND_ORE, BlockDiamondOre.class);// done.
        register(DIORITE, BlockDiorite.class);// done.
        register(DIORITE_STAIRS, BlockDioriteStairs.class);
        register(DIRT, BlockDirt.class);// done.
        register(DIRT_WITH_ROOTS, BlockDirtWithRoots.class);// done.
        register(DISPENSER, BlockDispenser.class);
        register(DOUBLE_CUT_COPPER_SLAB, BlockDoubleCutCopperSlab.class);
        register(DOUBLE_PLANT, BlockDoublePlant.class);// done.
        register(DOUBLE_STONE_BLOCK_SLAB, BlockDoubleStoneBlockSlab.class);
        register(DOUBLE_STONE_BLOCK_SLAB2, BlockDoubleStoneBlockSlab2.class);
        register(DOUBLE_STONE_BLOCK_SLAB3, BlockDoubleStoneBlockSlab3.class);
        register(DOUBLE_STONE_BLOCK_SLAB4, BlockDoubleStoneBlockSlab4.class);
        register(DOUBLE_WOODEN_SLAB, BlockDoubleWoodenSlab.class);
        register(DRAGON_EGG, BlockDragonEgg.class);// done.
        register(DRIED_KELP_BLOCK, BlockDriedKelpBlock.class);// done.
        register(DRIPSTONE_BLOCK, BlockDripstoneBlock.class);
        register(DROPPER, BlockDropper.class);// done.
//        register(ELEMENT_0, BlockElement0.class);
//        register(ELEMENT_1, BlockElement1.class);
//        register(ELEMENT_10, BlockElement10.class);
//        register(ELEMENT_100, BlockElement100.class);
//        register(ELEMENT_101, BlockElement101.class);
//        register(ELEMENT_102, BlockElement102.class);
//        register(ELEMENT_103, BlockElement103.class);
//        register(ELEMENT_104, BlockElement104.class);
//        register(ELEMENT_105, BlockElement105.class);
//        register(ELEMENT_106, BlockElement106.class);
//        register(ELEMENT_107, BlockElement107.class);
//        register(ELEMENT_108, BlockElement108.class);
//        register(ELEMENT_109, BlockElement109.class);
//        register(ELEMENT_11, BlockElement11.class);
//        register(ELEMENT_110, BlockElement110.class);
//        register(ELEMENT_111, BlockElement111.class);
//        register(ELEMENT_112, BlockElement112.class);
//        register(ELEMENT_113, BlockElement113.class);
//        register(ELEMENT_114, BlockElement114.class);
//        register(ELEMENT_115, BlockElement115.class);
//        register(ELEMENT_116, BlockElement116.class);
//        register(ELEMENT_117, BlockElement117.class);
//        register(ELEMENT_118, BlockElement118.class);
//        register(ELEMENT_12, BlockElement12.class);
//        register(ELEMENT_13, BlockElement13.class);
//        register(ELEMENT_14, BlockElement14.class);
//        register(ELEMENT_15, BlockElement15.class);
//        register(ELEMENT_16, BlockElement16.class);
//        register(ELEMENT_17, BlockElement17.class);
//        register(ELEMENT_18, BlockElement18.class);
//        register(ELEMENT_19, BlockElement19.class);
//        register(ELEMENT_2, BlockElement2.class);
//        register(ELEMENT_20, BlockElement20.class);
//        register(ELEMENT_21, BlockElement21.class);
//        register(ELEMENT_22, BlockElement22.class);
//        register(ELEMENT_23, BlockElement23.class);
//        register(ELEMENT_24, BlockElement24.class);
//        register(ELEMENT_25, BlockElement25.class);
//        register(ELEMENT_26, BlockElement26.class);
//        register(ELEMENT_27, BlockElement27.class);
//        register(ELEMENT_28, BlockElement28.class);
//        register(ELEMENT_29, BlockElement29.class);
//        register(ELEMENT_3, BlockElement3.class);
//        register(ELEMENT_30, BlockElement30.class);
//        register(ELEMENT_31, BlockElement31.class);
//        register(ELEMENT_32, BlockElement32.class);
//        register(ELEMENT_33, BlockElement33.class);
//        register(ELEMENT_34, BlockElement34.class);
//        register(ELEMENT_35, BlockElement35.class);
//        register(ELEMENT_36, BlockElement36.class);
//        register(ELEMENT_37, BlockElement37.class);
//        register(ELEMENT_38, BlockElement38.class);
//        register(ELEMENT_39, BlockElement39.class);
//        register(ELEMENT_4, BlockElement4.class);
//        register(ELEMENT_40, BlockElement40.class);
//        register(ELEMENT_41, BlockElement41.class);
//        register(ELEMENT_42, BlockElement42.class);
//        register(ELEMENT_43, BlockElement43.class);
//        register(ELEMENT_44, BlockElement44.class);
//        register(ELEMENT_45, BlockElement45.class);
//        register(ELEMENT_46, BlockElement46.class);
//        register(ELEMENT_47, BlockElement47.class);
//        register(ELEMENT_48, BlockElement48.class);
//        register(ELEMENT_49, BlockElement49.class);
//        register(ELEMENT_5, BlockElement5.class);
//        register(ELEMENT_50, BlockElement50.class);
//        register(ELEMENT_51, BlockElement51.class);
//        register(ELEMENT_52, BlockElement52.class);
//        register(ELEMENT_53, BlockElement53.class);
//        register(ELEMENT_54, BlockElement54.class);
//        register(ELEMENT_55, BlockElement55.class);
//        register(ELEMENT_56, BlockElement56.class);
//        register(ELEMENT_57, BlockElement57.class);
//        register(ELEMENT_58, BlockElement58.class);
//        register(ELEMENT_59, BlockElement59.class);
//        register(ELEMENT_6, BlockElement6.class);
//        register(ELEMENT_60, BlockElement60.class);
//        register(ELEMENT_61, BlockElement61.class);
//        register(ELEMENT_62, BlockElement62.class);
//        register(ELEMENT_63, BlockElement63.class);
//        register(ELEMENT_64, BlockElement64.class);
//        register(ELEMENT_65, BlockElement65.class);
//        register(ELEMENT_66, BlockElement66.class);
//        register(ELEMENT_67, BlockElement67.class);
//        register(ELEMENT_68, BlockElement68.class);
//        register(ELEMENT_69, BlockElement69.class);
//        register(ELEMENT_7, BlockElement7.class);
//        register(ELEMENT_70, BlockElement70.class);
//        register(ELEMENT_71, BlockElement71.class);
//        register(ELEMENT_72, BlockElement72.class);
//        register(ELEMENT_73, BlockElement73.class);
//        register(ELEMENT_74, BlockElement74.class);
//        register(ELEMENT_75, BlockElement75.class);
//        register(ELEMENT_76, BlockElement76.class);
//        register(ELEMENT_77, BlockElement77.class);
//        register(ELEMENT_78, BlockElement78.class);
//        register(ELEMENT_79, BlockElement79.class);
//        register(ELEMENT_8, BlockElement8.class);
//        register(ELEMENT_80, BlockElement80.class);
//        register(ELEMENT_81, BlockElement81.class);
//        register(ELEMENT_82, BlockElement82.class);
//        register(ELEMENT_83, BlockElement83.class);
//        register(ELEMENT_84, BlockElement84.class);
//        register(ELEMENT_85, BlockElement85.class);
//        register(ELEMENT_86, BlockElement86.class);
//        register(ELEMENT_87, BlockElement87.class);
//        register(ELEMENT_88, BlockElement88.class);
//        register(ELEMENT_89, BlockElement89.class);
//        register(ELEMENT_9, BlockElement9.class);
//        register(ELEMENT_90, BlockElement90.class);
//        register(ELEMENT_91, BlockElement91.class);
//        register(ELEMENT_92, BlockElement92.class);
//        register(ELEMENT_93, BlockElement93.class);
//        register(ELEMENT_94, BlockElement94.class);
//        register(ELEMENT_95, BlockElement95.class);
//        register(ELEMENT_96, BlockElement96.class);
//        register(ELEMENT_97, BlockElement97.class);
//        register(ELEMENT_98, BlockElement98.class);
//        register(ELEMENT_99, BlockElement99.class);
        register(EMERALD_BLOCK, BlockEmeraldBlock.class);// done.
        register(EMERALD_ORE, BlockEmeraldOre.class);// done.
        register(ENCHANTING_TABLE, BlockEnchantingTable.class);// done.
        register(END_BRICK_STAIRS, BlockEndBrickStairs.class);
        register(END_BRICKS, BlockEndBricks.class);
        register(END_GATEWAY, BlockEndGateway.class);// done.
        register(END_PORTAL, BlockEndPortal.class);// done.
        register(END_PORTAL_FRAME, BlockEndPortalFrame.class);
        register(END_ROD, BlockEndRod.class);// done.
        register(END_STONE, BlockEndStone.class);// done.
        register(ENDER_CHEST, BlockEnderChest.class);// done.
//        register(EXPOSED_CHISELED_COPPER, BlockExposedChiseledCopper.class);//experiment
        register(EXPOSED_COPPER, BlockExposedCopper.class);// done.
        register(EXPOSED_COPPER_BULB, BlockExposedCopperBulb.class);
        register(EXPOSED_COPPER_DOOR, BlockExposedCopperDoor.class);
        register(EXPOSED_COPPER_GRATE, BlockExposedCopperGrate.class);
        register(EXPOSED_COPPER_TRAPDOOR, BlockExposedCopperTrapdoor.class);
        register(EXPOSED_CUT_COPPER, BlockExposedCutCopper.class);
        register(EXPOSED_CUT_COPPER_SLAB, BlockExposedCutCopperSlab.class);
        register(EXPOSED_CUT_COPPER_STAIRS, BlockExposedCutCopperStairs.class);
        register(EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockExposedDoubleCutCopperSlab.class);
        register(FARMLAND, BlockFarmland.class);// done.
        register(FENCE_GATE, BlockFenceGate.class);// done.
        register(FIRE, BlockFire.class);
        register(FIRE_CORAL, BlockFireCoral.class);// done.
        register(FLETCHING_TABLE, BlockFletchingTable.class);// done.
        register(FLOWER_POT, BlockFlowerPot.class);
        register(FLOWERING_AZALEA, BlockFloweringAzalea.class);// done.
        register(FLOWING_LAVA, BlockFlowingLava.class);// done.
        register(FLOWING_WATER, BlockFlowingWater.class);// done.
        register(FRAME, BlockFrame.class);
        register(FROG_SPAWN, BlockFrogSpawn.class);// done.
        register(FROSTED_ICE, BlockFrostedIce.class);// done.
        register(FURNACE, BlockFurnace.class);// done.
        register(GILDED_BLACKSTONE, BlockGildedBlackstone.class);
        register(GLASS, BlockGlass.class);// done.
        register(GLASS_PANE, BlockGlassPane.class);// done.
        register(GLOW_FRAME, BlockGlowFrame.class);
        register(GLOW_LICHEN, BlockGlowLichen.class);// done.
        register(GLOWINGOBSIDIAN, BlockGlowingobsidian.class);
        register(GLOWSTONE, BlockGlowstone.class);// done.
        register(GOLD_BLOCK, BlockGoldBlock.class);// done.
        register(GOLD_ORE, BlockGoldOre.class);// done.
        register(GOLDEN_RAIL, BlockGoldenRail.class);// done.
        register(GRANITE, BlockGranite.class);// done.
        register(GRANITE_STAIRS, BlockGraniteStairs.class);
        register(GRASS, BlockGrass.class);// done.
        register(GRASS_PATH, BlockGrassPath.class);// done.
        register(GRAVEL, BlockGravel.class);// done.
        register(GRAY_CANDLE, BlockGrayCandle.class);// done.
        register(GRAY_CANDLE_CAKE, BlockGrayCandleCake.class);// done.
        register(GRAY_CARPET, BlockGrayCarpet.class);// done.
        register(GRAY_CONCRETE, BlockGrayConcrete.class);// done.
        register(GRAY_CONCRETE_POWDER, BlockGrayConcretePowder.class);// done.
        register(GRAY_GLAZED_TERRACOTTA, BlockGrayGlazedTerracotta.class);// done.
        register(GRAY_SHULKER_BOX, BlockGrayShulkerBox.class);// done.
        register(GRAY_STAINED_GLASS, BlockGrayStainedGlass.class);// done.
        register(GRAY_STAINED_GLASS_PANE, BlockGrayStainedGlassPane.class);// done.
        register(GRAY_TERRACOTTA, BlockGrayTerracotta.class);// done.
        register(GRAY_WOOL, BlockGrayWool.class);// done.
        register(GREEN_CANDLE, BlockGreenCandle.class);// done.
        register(GREEN_CANDLE_CAKE, BlockGreenCandleCake.class);// done.
        register(GREEN_CARPET, BlockGreenCarpet.class);// done.
        register(GREEN_CONCRETE, BlockGreenConcrete.class);// done.
        register(GREEN_CONCRETE_POWDER, BlockGreenConcretePowder.class);// done.
        register(GREEN_GLAZED_TERRACOTTA, BlockGreenGlazedTerracotta.class);// done.
        register(GREEN_SHULKER_BOX, BlockGreenShulkerBox.class);// done.
        register(GREEN_STAINED_GLASS, BlockGreenStainedGlass.class);// done.
        register(GREEN_STAINED_GLASS_PANE, BlockGreenStainedGlassPane.class);// done.
        register(GREEN_TERRACOTTA, BlockGreenTerracotta.class);// done.
        register(GREEN_WOOL, BlockGreenWool.class);// done.
        register(GRINDSTONE, BlockGrindstone.class);// done.
        register(HANGING_ROOTS, BlockHangingRoots.class);// done.
//        register(HARD_GLASS, BlockHardGlass.class);//edu
//        register(HARD_GLASS_PANE, BlockHardGlassPane.class);//edu
//        register(HARD_STAINED_GLASS, BlockHardStainedGlass.class);//edu
//        register(HARD_STAINED_GLASS_PANE, BlockHardStainedGlassPane.class);//edu
        register(HARDENED_CLAY, BlockHardenedClay.class);// done.
        register(HAY_BLOCK, BlockHayBlock.class);// done.
        register(HEAVY_WEIGHTED_PRESSURE_PLATE, BlockHeavyWeightedPressurePlate.class);
        register(HONEY_BLOCK, BlockHoneyBlock.class);// done.
        register(HONEYCOMB_BLOCK, BlockHoneycombBlock.class);// done.
        register(HOPPER, BlockHopper.class);// done.
        register(HORN_CORAL, BlockHornCoral.class);// done.
        register(ICE, BlockIce.class);// done.
        register(INFESTED_DEEPSLATE, BlockInfestedDeepslate.class);// done.
        register(INFO_UPDATE, BlockInfoUpdate.class);// done.
        register(INFO_UPDATE2, BlockInfoUpdate2.class);// done.
        register(INVISIBLE_BEDROCK, BlockInvisibleBedrock.class);// done.
        register(IRON_BARS, BlockIronBars.class);// done.
        register(IRON_BLOCK, BlockIronBlock.class);// done.
        register(IRON_DOOR, BlockIronDoor.class);// done.
        register(IRON_ORE, BlockIronOre.class);// done.
        register(IRON_TRAPDOOR, BlockIronTrapdoor.class);
        register(JIGSAW, BlockJigsaw.class);// done.
        register(JUKEBOX, BlockJukebox.class);// done.
        register(JUNGLE_BUTTON, BlockJungleButton.class);// done.
        register(JUNGLE_DOOR, BlockJungleDoor.class);// done.
        register(JUNGLE_FENCE, BlockJungleFence.class);// done.
        register(JUNGLE_FENCE_GATE, BlockJungleFenceGate.class);// done.
        register(JUNGLE_HANGING_SIGN, BlockJungleHangingSign.class);// done.
        register(JUNGLE_LOG, BlockJungleLog.class);// done.
        register(JUNGLE_PLANKS, BlockJunglePlanks.class);// done.
        register(JUNGLE_PRESSURE_PLATE, BlockJunglePressurePlate.class);
        register(JUNGLE_STAIRS, BlockJungleStairs.class);
        register(JUNGLE_STANDING_SIGN, BlockJungleStandingSign.class);// done.
        register(JUNGLE_TRAPDOOR, BlockJungleTrapdoor.class);// done.
        register(JUNGLE_WALL_SIGN, BlockJungleWallSign.class);// done.
        register(KELP, BlockKelp.class);// done.
        register(LADDER, BlockLadder.class);// done.
        register(LANTERN, BlockLantern.class);// done.
        register(LAPIS_BLOCK, BlockLapisBlock.class);// done.
        register(LAPIS_ORE, BlockLapisOre.class);// done.
        register(LARGE_AMETHYST_BUD, BlockLargeAmethystBud.class);
        register(LAVA, BlockLava.class);// done.
        register(LEAVES, BlockLeaves.class);// done.
        register(LEAVES2, BlockLeaves2.class);// done.
        register(LECTERN, BlockLectern.class);// done.
        register(LEVER, BlockLever.class);
        register(LIGHT_BLOCK, BlockLightBlock.class);// done.
        register(LIGHT_BLUE_CANDLE, BlockLightBlueCandle.class);// done.
        register(LIGHT_BLUE_CANDLE_CAKE, BlockLightBlueCandleCake.class);// done.
        register(LIGHT_BLUE_CARPET, BlockLightBlueCarpet.class);// done.
        register(LIGHT_BLUE_CONCRETE, BlockLightBlueConcrete.class);// done.
        register(LIGHT_BLUE_CONCRETE_POWDER, BlockLightBlueConcretePowder.class);// done.
        register(LIGHT_BLUE_GLAZED_TERRACOTTA, BlockLightBlueGlazedTerracotta.class);// done.
        register(LIGHT_BLUE_SHULKER_BOX, BlockLightBlueShulkerBox.class);// done.
        register(LIGHT_BLUE_STAINED_GLASS, BlockLightBlueStainedGlass.class);// done.
        register(LIGHT_BLUE_STAINED_GLASS_PANE, BlockLightBlueStainedGlassPane.class);// done.
        register(LIGHT_BLUE_TERRACOTTA, BlockLightBlueTerracotta.class);// done.
        register(LIGHT_BLUE_WOOL, BlockLightBlueWool.class);// done.
        register(LIGHT_GRAY_CANDLE, BlockLightGrayCandle.class);// done.
        register(LIGHT_GRAY_CANDLE_CAKE, BlockLightGrayCandleCake.class);// done.
        register(LIGHT_GRAY_CARPET, BlockLightGrayCarpet.class);// done.
        register(LIGHT_GRAY_CONCRETE, BlockLightGrayConcrete.class);// done.
        register(LIGHT_GRAY_CONCRETE_POWDER, BlockLightGrayConcretePowder.class);// done.
        register(LIGHT_GRAY_SHULKER_BOX, BlockLightGrayShulkerBox.class);// done.
        register(LIGHT_GRAY_STAINED_GLASS, BlockLightGrayStainedGlass.class);// done.
        register(LIGHT_GRAY_STAINED_GLASS_PANE, BlockLightGrayStainedGlassPane.class);// done.
        register(LIGHT_GRAY_TERRACOTTA, BlockLightGrayTerracotta.class);// done.
        register(LIGHT_GRAY_WOOL, BlockLightGrayWool.class);// done.
        register(LIGHT_WEIGHTED_PRESSURE_PLATE, BlockLightWeightedPressurePlate.class);
        register(LIGHTNING_ROD, BlockLightningRod.class);// done.
        register(LIME_CANDLE, BlockLimeCandle.class);// done.
        register(LIME_CANDLE_CAKE, BlockLimeCandleCake.class);// done.
        register(LIME_CARPET, BlockLimeCarpet.class);// done.
        register(LIME_CONCRETE, BlockLimeConcrete.class);// done.
        register(LIME_CONCRETE_POWDER, BlockLimeConcretePowder.class);// done.
        register(LIME_GLAZED_TERRACOTTA, BlockLimeGlazedTerracotta.class);// done.
        register(LIME_SHULKER_BOX, BlockLimeShulkerBox.class);// done.
        register(LIME_STAINED_GLASS, BlockLimeStainedGlass.class);// done.
        register(LIME_STAINED_GLASS_PANE, BlockLimeStainedGlassPane.class);// done.
        register(LIME_TERRACOTTA, BlockLimeTerracotta.class);// done.
        register(LIME_WOOL, BlockLimeWool.class);// done.
        register(LIT_BLAST_FURNACE, BlockLitBlastFurnace.class);
        register(LIT_DEEPSLATE_REDSTONE_ORE, BlockLitDeepslateRedstoneOre.class);
        register(LIT_FURNACE, BlockLitFurnace.class);
        register(LIT_PUMPKIN, BlockLitPumpkin.class);
        register(LIT_REDSTONE_LAMP, BlockLitRedstoneLamp.class);
        register(LIT_REDSTONE_ORE, BlockLitRedstoneOre.class);
        register(LIT_SMOKER, BlockLitSmoker.class);
        register(LODESTONE, BlockLodestone.class);
        register(LOOM, BlockLoom.class);// done.
        register(MAGENTA_CANDLE, BlockMagentaCandle.class);// done.
        register(MAGENTA_CANDLE_CAKE, BlockMagentaCandleCake.class);// done.
        register(MAGENTA_CARPET, BlockMagentaCarpet.class);// done.
        register(MAGENTA_CONCRETE, BlockMagentaConcrete.class);// done.
        register(MAGENTA_CONCRETE_POWDER, BlockMagentaConcretePowder.class);// done.
        register(MAGENTA_GLAZED_TERRACOTTA, BlockMagentaGlazedTerracotta.class);// done.
        register(MAGENTA_SHULKER_BOX, BlockMagentaShulkerBox.class);// done.
        register(MAGENTA_STAINED_GLASS, BlockMagentaStainedGlass.class);// done.
        register(MAGENTA_STAINED_GLASS_PANE, BlockMagentaStainedGlassPane.class);// done.
        register(MAGENTA_TERRACOTTA, BlockMagentaTerracotta.class);// done.
        register(MAGENTA_WOOL, BlockMagentaWool.class);// done.
        register(MAGMA, BlockMagma.class);// done.
        register(MANGROVE_BUTTON, BlockMangroveButton.class);// done.
        register(MANGROVE_DOOR, BlockMangroveDoor.class);// done.
        register(MANGROVE_DOUBLE_SLAB, BlockMangroveDoubleSlab.class);
        register(MANGROVE_FENCE, BlockMangroveFence.class);// done.
        register(MANGROVE_FENCE_GATE, BlockMangroveFenceGate.class);// done.
        register(MANGROVE_HANGING_SIGN, BlockMangroveHangingSign.class);// done.
        register(MANGROVE_LEAVES, BlockMangroveLeaves.class);
        register(MANGROVE_LOG, BlockMangroveLog.class);// done.
        register(MANGROVE_PLANKS, BlockMangrovePlanks.class);// done.
        register(MANGROVE_PRESSURE_PLATE, BlockMangrovePressurePlate.class);
        register(MANGROVE_PROPAGULE, BlockMangrovePropagule.class);
        register(MANGROVE_ROOTS, BlockMangroveRoots.class);// done.
        register(MANGROVE_SLAB, BlockMangroveSlab.class);
        register(MANGROVE_STAIRS, BlockMangroveStairs.class);
        register(MANGROVE_STANDING_SIGN, BlockMangroveStandingSign.class);// done.
        register(MANGROVE_TRAPDOOR, BlockMangroveTrapdoor.class);// done.
        register(MANGROVE_WALL_SIGN, BlockMangroveWallSign.class);// done.
        register(MANGROVE_WOOD, BlockMangroveWood.class);
        register(MEDIUM_AMETHYST_BUD, BlockMediumAmethystBud.class);
        register(MELON_BLOCK, BlockMelonBlock.class);
        register(MELON_STEM, BlockMelonStem.class);// done.
        register(MOB_SPAWNER, BlockMobSpawner.class);// done.
        register(MONSTER_EGG, BlockMonsterEgg.class);// done.
        register(MOSS_BLOCK, BlockMossBlock.class);
        register(MOSS_CARPET, BlockMossCarpet.class);// done.
        register(MOSSY_COBBLESTONE, BlockMossyCobblestone.class);
        register(MOSSY_COBBLESTONE_STAIRS, BlockMossyCobblestoneStairs.class);
        register(MOSSY_STONE_BRICK_STAIRS, BlockMossyStoneBrickStairs.class);
        register(MOVING_BLOCK, BlockMovingBlock.class);
        register(MUD, BlockMud.class);// done.
        register(MUD_BRICK_DOUBLE_SLAB, BlockMudBrickDoubleSlab.class);
        register(MUD_BRICK_SLAB, BlockMudBrickSlab.class);
        register(MUD_BRICK_STAIRS, BlockMudBrickStairs.class);
        register(MUD_BRICK_WALL, BlockMudBrickWall.class);
        register(MUD_BRICKS, BlockMudBricks.class);
        register(MUDDY_MANGROVE_ROOTS, BlockMuddyMangroveRoots.class);// done.
        register(MYCELIUM, BlockMycelium.class);// done.
        register(NETHER_BRICK, BlockNetherBrick.class);// done.
        register(NETHER_BRICK_FENCE, BlockNetherBrickFence.class);// done.
        register(NETHER_BRICK_STAIRS, BlockNetherBrickStairs.class);
        register(NETHER_GOLD_ORE, BlockNetherGoldOre.class);
        register(NETHER_SPROUTS, BlockNetherSprouts.class);
        register(NETHER_WART, BlockNetherWart.class);
        register(NETHER_WART_BLOCK, BlockNetherWartBlock.class);
        register(NETHERITE_BLOCK, BlockNetheriteBlock.class);
        register(NETHERRACK, BlockNetherrack.class);
        register(NETHERREACTOR, BlockNetherreactor.class);// done.
        register(NORMAL_STONE_STAIRS, BlockNormalStoneStairs.class);
        register(NOTEBLOCK, BlockNoteblock.class);
        register(OAK_FENCE, BlockOakFence.class);// done.
        register(OAK_HANGING_SIGN, BlockOakHangingSign.class);// done.
        register(OAK_LOG, BlockOakLog.class);// done.
        register(OAK_PLANKS, BlockOakPlanks.class);// done.
        register(OAK_STAIRS, BlockOakStairs.class);
        register(OBSERVER, BlockObserver.class);// done.
        register(OBSIDIAN, BlockObsidian.class);
        register(OCHRE_FROGLIGHT, BlockOchreFroglight.class);
        register(ORANGE_CANDLE, BlockOrangeCandle.class);// done.
        register(ORANGE_CANDLE_CAKE, BlockOrangeCandleCake.class);// done.
        register(ORANGE_CARPET, BlockOrangeCarpet.class);// done.
        register(ORANGE_CONCRETE, BlockOrangeConcrete.class);// done.
        register(ORANGE_CONCRETE_POWDER, BlockOrangeConcretePowder.class);// done.
        register(ORANGE_GLAZED_TERRACOTTA, BlockOrangeGlazedTerracotta.class);// done.
        register(ORANGE_SHULKER_BOX, BlockOrangeShulkerBox.class);// done.
        register(ORANGE_STAINED_GLASS, BlockOrangeStainedGlass.class);// done.
        register(ORANGE_STAINED_GLASS_PANE, BlockOrangeStainedGlassPane.class);// done.
        register(ORANGE_TERRACOTTA, BlockOrangeTerracotta.class);// done.
        register(ORANGE_WOOL, BlockOrangeWool.class);// done.
        register(OXIDIZED_CHISELED_COPPER, BlockOxidizedChiseledCopper.class);
        register(OXIDIZED_COPPER, BlockOxidizedCopper.class);// done.
        register(OXIDIZED_COPPER_BULB, BlockOxidizedCopperBulb.class);
        register(OXIDIZED_COPPER_DOOR, BlockOxidizedCopperDoor.class);
        register(OXIDIZED_COPPER_GRATE, BlockOxidizedCopperGrate.class);
        register(OXIDIZED_COPPER_TRAPDOOR, BlockOxidizedCopperTrapdoor.class);
        register(OXIDIZED_CUT_COPPER, BlockOxidizedCutCopper.class);
        register(OXIDIZED_CUT_COPPER_SLAB, BlockOxidizedCutCopperSlab.class);
        register(OXIDIZED_CUT_COPPER_STAIRS, BlockOxidizedCutCopperStairs.class);
        register(OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockOxidizedDoubleCutCopperSlab.class);
        register(PACKED_ICE, BlockPackedIce.class);// done.
        register(PACKED_MUD, BlockPackedMud.class);// done.
        register(PEARLESCENT_FROGLIGHT, BlockPearlescentFroglight.class);
        register(PINK_CANDLE, BlockPinkCandle.class);// done.
        register(PINK_CANDLE_CAKE, BlockPinkCandleCake.class);// done.
        register(PINK_CARPET, BlockPinkCarpet.class);// done.
        register(PINK_CONCRETE, BlockPinkConcrete.class);// done.
        register(PINK_CONCRETE_POWDER, BlockPinkConcretePowder.class);// done.
        register(PINK_GLAZED_TERRACOTTA, BlockPinkGlazedTerracotta.class);// done.
        register(PINK_PETALS, BlockPinkPetals.class);
        register(PINK_SHULKER_BOX, BlockPinkShulkerBox.class);// done.
        register(PINK_STAINED_GLASS, BlockPinkStainedGlass.class);// done.
        register(PINK_STAINED_GLASS_PANE, BlockPinkStainedGlassPane.class);// done.
        register(PINK_TERRACOTTA, BlockPinkTerracotta.class);// done.
        register(PINK_WOOL, BlockPinkWool.class);// done.
        register(PISTON, BlockPiston.class);// done.
        register(PISTON_ARM_COLLISION, BlockPistonArmCollision.class);// done.
        register(PITCHER_CROP, BlockPitcherCrop.class);
        register(PITCHER_PLANT, BlockPitcherPlant.class);
        register(PODZOL, BlockPodzol.class);// done.
        register(POINTED_DRIPSTONE, BlockPointedDripstone.class);// done.
        register(POLISHED_ANDESITE, BlockPolishedAndesite.class);// done.
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
        register(POLISHED_DIORITE, BlockPolishedDiorite.class);// done.
        register(POLISHED_DIORITE_STAIRS, BlockPolishedDioriteStairs.class);
        register(POLISHED_GRANITE, BlockPolishedGranite.class);// done.
        register(POLISHED_GRANITE_STAIRS, BlockPolishedGraniteStairs.class);
        register(POLISHED_TUFF, BlockPolishedTuff.class);
        register(POLISHED_TUFF_DOUBLE_SLAB, BlockPolishedTuffDoubleSlab.class);
        register(POLISHED_TUFF_SLAB, BlockPolishedTuffSlab.class);
        register(POLISHED_TUFF_STAIRS, BlockPolishedTuffStairs.class);
        register(POLISHED_TUFF_WALL, BlockPolishedTuffWall.class);
        register(PORTAL, BlockPortal.class);
        register(POTATOES, BlockPotatoes.class);
        register(POWDER_SNOW, BlockPowderSnow.class);
        register(POWERED_COMPARATOR, BlockPoweredComparator.class);
        register(POWERED_REPEATER, BlockPoweredRepeater.class);
        register(PRISMARINE, BlockPrismarine.class);// done.
        register(PRISMARINE_BRICKS_STAIRS, BlockPrismarineBricksStairs.class);
        register(PRISMARINE_STAIRS, BlockPrismarineStairs.class);
        register(PUMPKIN, BlockPumpkin.class);// done.
        register(PUMPKIN_STEM, BlockPumpkinStem.class);// done.
        register(PURPLE_CANDLE, BlockPurpleCandle.class);// done.
        register(PURPLE_CANDLE_CAKE, BlockPurpleCandleCake.class);// done.
        register(PURPLE_CARPET, BlockPurpleCarpet.class);// done.
        register(PURPLE_CONCRETE, BlockPurpleConcrete.class);// done.
        register(PURPLE_CONCRETE_POWDER, BlockPurpleConcretePowder.class);// done.
        register(PURPLE_GLAZED_TERRACOTTA, BlockPurpleGlazedTerracotta.class);// done.
        register(PURPLE_SHULKER_BOX, BlockPurpleShulkerBox.class);// done.
        register(PURPLE_STAINED_GLASS, BlockPurpleStainedGlass.class);// done.
        register(PURPLE_STAINED_GLASS_PANE, BlockPurpleStainedGlassPane.class);// done.
        register(PURPLE_TERRACOTTA, BlockPurpleTerracotta.class);// done.
        register(PURPLE_WOOL, BlockPurpleWool.class);// done.
        register(PURPUR_BLOCK, BlockPurpurBlock.class);
        register(PURPUR_STAIRS, BlockPurpurStairs.class);
        register(QUARTZ_BLOCK, BlockQuartzBlock.class);
        register(QUARTZ_BRICKS, BlockQuartzBricks.class);
        register(QUARTZ_ORE, BlockQuartzOre.class);
        register(QUARTZ_STAIRS, BlockQuartzStairs.class);
        register(RAIL, BlockRail.class);// done.
        register(RAW_COPPER_BLOCK, BlockRawCopperBlock.class);// done.
        register(RAW_GOLD_BLOCK, BlockRawGoldBlock.class);// done.
        register(RAW_IRON_BLOCK, BlockRawIronBlock.class);// done.
        register(RED_CANDLE, BlockRedCandle.class);// done.
        register(RED_CANDLE_CAKE, BlockRedCandleCake.class);// done.
        register(RED_CARPET, BlockRedCarpet.class);// done.
        register(RED_CONCRETE, BlockRedConcrete.class);// done.
        register(RED_CONCRETE_POWDER, BlockRedConcretePowder.class);// done.
        register(RED_FLOWER, BlockRedFlower.class);// done.
        register(RED_GLAZED_TERRACOTTA, BlockRedGlazedTerracotta.class);// done.
        register(RED_MUSHROOM, BlockRedMushroom.class);// done.
        register(RED_MUSHROOM_BLOCK, BlockRedMushroomBlock.class);// done.
        register(RED_NETHER_BRICK, BlockRedNetherBrick.class);
        register(RED_NETHER_BRICK_STAIRS, BlockRedNetherBrickStairs.class);
        register(RED_SANDSTONE, BlockRedSandstone.class);
        register(RED_SANDSTONE_STAIRS, BlockRedSandstoneStairs.class);
        register(RED_SHULKER_BOX, BlockRedShulkerBox.class);// done.
        register(RED_STAINED_GLASS, BlockRedStainedGlass.class);// done.
        register(RED_STAINED_GLASS_PANE, BlockRedStainedGlassPane.class);// done.
        register(RED_TERRACOTTA, BlockRedTerracotta.class);// done.
        register(RED_WOOL, BlockRedWool.class);// done.
        register(REDSTONE_BLOCK, BlockRedstoneBlock.class);// done.
        register(REDSTONE_LAMP, BlockRedstoneLamp.class);
        register(REDSTONE_ORE, BlockRedstoneOre.class);// done.
        register(REDSTONE_TORCH, BlockRedstoneTorch.class);
        register(REDSTONE_WIRE, BlockRedstoneWire.class);
        register(REEDS, BlockReeds.class);
        register(REINFORCED_DEEPSLATE, BlockReinForcedDeepSlate.class);// done.
        register(REPEATING_COMMAND_BLOCK, BlockRepeatingCommandBlock.class);
        register(RESERVED6, BlockReserved6.class);
        register(RESPAWN_ANCHOR, BlockRespawnAnchor.class);// done.
        register(SAND, BlockSand.class);// done.
        register(SANDSTONE, BlockSandstone.class);// done.
        register(SANDSTONE_STAIRS, BlockSandstoneStairs.class);
        register(SAPLING, BlockSapling.class);// done.
        register(SCAFFOLDING, BlockScaffolding.class);// done.
        register(SCULK, BlockSculk.class);
        register(SCULK_CATALYST, BlockSculkCatalyst.class);
        register(SCULK_SENSOR, BlockSculkSensor.class);
        register(SCULK_SHRIEKER, BlockSculkShrieker.class);
        register(SCULK_VEIN, BlockSculkVein.class);
        register(SEA_LANTERN, BlockSeaLantern.class);
        register(SEA_PICKLE, BlockSeaPickle.class);
        register(SEAGRASS, BlockSeagrass.class);
        register(SHROOMLIGHT, BlockShroomlight.class);
        register(SILVER_GLAZED_TERRACOTTA, BlockSilverGlazedTerracotta.class);// done.
        register(SKULL, BlockSkull.class);// done.
        register(SLIME, BlockSlime.class);// done.
        register(SMALL_AMETHYST_BUD, BlockSmallAmethystBud.class);
        register(SMALL_DRIPLEAF_BLOCK, BlockSmallDripleafBlock.class);
        register(SMITHING_TABLE, BlockSmithingTable.class);
        register(SMOKER, BlockSmoker.class);
        register(SMOOTH_BASALT, BlockSmoothBasalt.class);
        register(SMOOTH_QUARTZ_STAIRS, BlockSmoothQuartzStairs.class);
        register(SMOOTH_RED_SANDSTONE_STAIRS, BlockSmoothRedSandstoneStairs.class);
        register(SMOOTH_SANDSTONE_STAIRS, BlockSmoothSandstoneStairs.class);
        register(SMOOTH_STONE, BlockSmoothStone.class);
        register(SNIFFER_EGG, BlockSnifferEgg.class);// done.
        register(SNOW, BlockSnow.class);// done.
        register(SNOW_LAYER, BlockSnowLayer.class);// done.
        register(SOUL_CAMPFIRE, BlockSoulCampfire.class);
        register(SOUL_FIRE, BlockSoulFire.class);
        register(SOUL_LANTERN, BlockSoulLantern.class);
        register(SOUL_SAND, BlockSoulSand.class);
        register(SOUL_SOIL, BlockSoulSoil.class);
        register(SOUL_TORCH, BlockSoulTorch.class);
        register(SPONGE, BlockSponge.class);
        register(SPORE_BLOSSOM, BlockSporeBlossom.class);
        register(SPRUCE_BUTTON, BlockSpruceButton.class);// done.
        register(SPRUCE_DOOR, BlockSpruceDoor.class);// done.
        register(SPRUCE_FENCE, BlockSpruceFence.class);// done.
        register(SPRUCE_FENCE_GATE, BlockSpruceFenceGate.class);// done.
        register(SPRUCE_HANGING_SIGN, BlockSpruceHangingSign.class);// done.
        register(SPRUCE_LOG, BlockSpruceLog.class);// done.
        register(SPRUCE_PLANKS, BlockSprucePlanks.class);// done.
        register(SPRUCE_PRESSURE_PLATE, BlockSprucePressurePlate.class);
        register(SPRUCE_STAIRS, BlockSpruceStairs.class);
        register(SPRUCE_STANDING_SIGN, BlockSpruceStandingSign.class);// done.
        register(SPRUCE_TRAPDOOR, BlockSpruceTrapdoor.class);// done.
        register(SPRUCE_WALL_SIGN, BlockSpruceWallSign.class);// done.
        register(STANDING_BANNER, BlockStandingBanner.class);// done.
        register(STANDING_SIGN, BlockStandingSign.class);
        register(STICKY_PISTON, BlockStickyPiston.class);// done.
        register(STICKY_PISTON_ARM_COLLISION, BlockStickyPistonArmCollision.class);// done.
        register(STONE, BlockStone.class);
        register(STONE_BLOCK_SLAB, BlockStoneBlockSlab.class);
        register(STONE_BLOCK_SLAB2, BlockStoneBlockSlab2.class);
        register(STONE_BLOCK_SLAB3, BlockStoneBlockSlab3.class);
        register(STONE_BLOCK_SLAB4, BlockStoneBlockSlab4.class);
        register(STONE_BRICK_STAIRS, BlockStoneBrickStairs.class);
        register(STONE_BUTTON, BlockStoneButton.class);
        register(STONE_PRESSURE_PLATE, BlockStonePressurePlate.class);
        register(STONE_STAIRS, BlockStoneStairs.class);
        register(STONEBRICK, BlockStonebrick.class);
        register(STONECUTTER, BlockStonecutter.class);// done.
        register(STONECUTTER_BLOCK, BlockStonecutterBlock.class);// done.
        register(STRIPPED_ACACIA_LOG, BlockStrippedAcaciaLog.class);// done.
        register(STRIPPED_BAMBOO_BLOCK, BlockStrippedBambooBlock.class);
        register(STRIPPED_BIRCH_LOG, BlockStrippedBirchLog.class);// done.
        register(STRIPPED_CHERRY_LOG, BlockStrippedCherryLog.class);// done.
        register(STRIPPED_CHERRY_WOOD, BlockStrippedCherryWood.class);
        register(STRIPPED_CRIMSON_HYPHAE, BlockStrippedCrimsonHyphae.class);// done.
        register(STRIPPED_CRIMSON_STEM, BlockStrippedCrimsonStem.class);
        register(STRIPPED_DARK_OAK_LOG, BlockStrippedDarkOakLog.class);// done.
        register(STRIPPED_JUNGLE_LOG, BlockStrippedJungleLog.class);// done.
        register(STRIPPED_MANGROVE_LOG, BlockStrippedMangroveLog.class);// done.
        register(STRIPPED_MANGROVE_WOOD, BlockStrippedMangroveWood.class);
        register(STRIPPED_OAK_LOG, BlockStrippedOakLog.class);// done.
        register(STRIPPED_SPRUCE_LOG, BlockStrippedSpruceLog.class);// done.
        register(STRIPPED_WARPED_HYPHAE, BlockStrippedWarpedHyphae.class);// done.
        register(STRIPPED_WARPED_STEM, BlockStrippedWarpedStem.class);
        register(STRUCTURE_BLOCK, BlockStructureBlock.class);
        register(STRUCTURE_VOID, BlockStructureVoid.class);// done.
        register(SUSPICIOUS_GRAVEL, BlockSuspiciousGravel.class);// done.
        register(SUSPICIOUS_SAND, BlockSuspiciousSand.class);// done.
        register(SWEET_BERRY_BUSH, BlockSweetBerryBush.class);
        register(TALLGRASS, BlockTallgrass.class);// done.
        register(TARGET, BlockTarget.class);
        register(TINTED_GLASS, BlockTintedGlass.class);
        register(TNT, BlockTnt.class);
        register(TORCH, BlockTorch.class);
        register(TORCHFLOWER, BlockTorchflower.class);
        register(TORCHFLOWER_CROP, BlockTorchflowerCrop.class);
        register(TRAPDOOR, BlockTrapdoor.class);// done.
        register(TRAPPED_CHEST, BlockTrappedChest.class);
        register(TRIP_WIRE, BlockTripWire.class);
        register(TRIPWIRE_HOOK, BlockTripwireHook.class);
        register(TUBE_CORAL, BlockTubeCoral.class);// done.
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
        register(UNDERWATER_TORCH, BlockUnderwaterTorch.class);
        register(UNDYED_SHULKER_BOX, BlockUndyedShulkerBox.class);// done.
        register(UNKNOWN, BlockUnknown.class);// done.
        register(UNLIT_REDSTONE_TORCH, BlockUnlitRedstoneTorch.class);
        register(UNPOWERED_COMPARATOR, BlockUnpoweredComparator.class);
        register(UNPOWERED_REPEATER, BlockUnpoweredRepeater.class);
        register(VERDANT_FROGLIGHT, BlockVerdantFroglight.class);
        register(VINE, BlockVine.class);// done.
        register(WALL_BANNER, BlockWallBanner.class);// done.
        register(WALL_SIGN, BlockWallSign.class);
        register(WARPED_BUTTON, BlockWarpedButton.class);// done.
        register(WARPED_DOOR, BlockWarpedDoor.class);// done.
        register(WARPED_DOUBLE_SLAB, BlockWarpedDoubleSlab.class);
        register(WARPED_FENCE, BlockWarpedFence.class);// done.
        register(WARPED_FENCE_GATE, BlockWarpedFenceGate.class);// done.
        register(WARPED_FUNGUS, BlockWarpedFungus.class);
        register(WARPED_HANGING_SIGN, BlockWarpedHangingSign.class);// done.
        register(WARPED_HYPHAE, BlockWarpedHyphae.class);// done.
        register(WARPED_NYLIUM, BlockWarpedNylium.class);
        register(WARPED_PLANKS, BlockWarpedPlanks.class);// done.
        register(WARPED_PRESSURE_PLATE, BlockWarpedPressurePlate.class);
        register(WARPED_ROOTS, BlockWarpedRoots.class);
        register(WARPED_SLAB, BlockWarpedSlab.class);
        register(WARPED_STAIRS, BlockWarpedStairs.class);
        register(WARPED_STANDING_SIGN, BlockWarpedStandingSign.class);// done.
        register(WARPED_STEM, BlockWarpedStem.class);// done.
        register(WARPED_TRAPDOOR, BlockWarpedTrapdoor.class);// done.
        register(WARPED_WALL_SIGN, BlockWarpedWallSign.class);// done.
        register(WARPED_WART_BLOCK, BlockWarpedWartBlock.class);
        register(WATER, BlockWater.class);
        register(WATERLILY, BlockWaterlily.class);// done.
        register(WAXED_CHISELED_COPPER, BlockWaxedChiseledCopper.class);
        register(WAXED_COPPER, BlockWaxedCopper.class);// done.
        register(WAXED_COPPER_BULB, BlockWaxedCopperBulb.class);
        register(WAXED_COPPER_DOOR, BlockWaxedCopperDoor.class);
        register(WAXED_COPPER_GRATE, BlockWaxedCopperGrate.class);
        register(WAXED_COPPER_TRAPDOOR, BlockWaxedCopperTrapdoor.class);
        register(WAXED_CUT_COPPER, BlockWaxedCutCopper.class);
        register(WAXED_CUT_COPPER_SLAB, BlockWaxedCutCopperSlab.class);
        register(WAXED_CUT_COPPER_STAIRS, BlockWaxedCutCopperStairs.class);
        register(WAXED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedDoubleCutCopperSlab.class);
        register(WAXED_EXPOSED_CHISELED_COPPER, BlockWaxedExposedChiseledCopper.class);
        register(WAXED_EXPOSED_COPPER, BlockWaxedExposedCopper.class);// done.
        register(WAXED_EXPOSED_COPPER_BULB, BlockWaxedExposedCopperBulb.class);
        register(WAXED_EXPOSED_COPPER_DOOR, BlockWaxedExposedCopperDoor.class);
        register(WAXED_EXPOSED_COPPER_GRATE, BlockWaxedExposedCopperGrate.class);
        register(WAXED_EXPOSED_COPPER_TRAPDOOR, BlockWaxedExposedCopperTrapdoor.class);
        register(WAXED_EXPOSED_CUT_COPPER, BlockWaxedExposedCutCopper.class);
        register(WAXED_EXPOSED_CUT_COPPER_SLAB, BlockWaxedExposedCutCopperSlab.class);
        register(WAXED_EXPOSED_CUT_COPPER_STAIRS, BlockWaxedExposedCutCopperStairs.class);
        register(WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedExposedDoubleCutCopperSlab.class);
        register(WAXED_OXIDIZED_CHISELED_COPPER, BlockWaxedOxidizedChiseledCopper.class);
        register(WAXED_OXIDIZED_COPPER, BlockWaxedOxidizedCopper.class);// done.
        register(WAXED_OXIDIZED_COPPER_BULB, BlockWaxedOxidizedCopperBulb.class);
        register(WAXED_OXIDIZED_COPPER_DOOR, BlockWaxedOxidizedCopperDoor.class);
        register(WAXED_OXIDIZED_COPPER_GRATE, BlockWaxedOxidizedCopperGrate.class);
        register(WAXED_OXIDIZED_COPPER_TRAPDOOR, BlockWaxedOxidizedCopperTrapdoor.class);
        register(WAXED_OXIDIZED_CUT_COPPER, BlockWaxedOxidizedCutCopper.class);
        register(WAXED_OXIDIZED_CUT_COPPER_SLAB, BlockWaxedOxidizedCutCopperSlab.class);
        register(WAXED_OXIDIZED_CUT_COPPER_STAIRS, BlockWaxedOxidizedCutCopperStairs.class);
        register(WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedOxidizedDoubleCutCopperSlab.class);
        register(WAXED_WEATHERED_CHISELED_COPPER, BlockWaxedWeatheredChiseledCopper.class);
        register(WAXED_WEATHERED_COPPER, BlockWaxedWeatheredCopper.class);// done.
        register(WAXED_WEATHERED_COPPER_BULB, BlockWaxedWeatheredCopperBulb.class);
        register(WAXED_WEATHERED_COPPER_DOOR, BlockWaxedWeatheredCopperDoor.class);
        register(WAXED_WEATHERED_COPPER_GRATE, BlockWaxedWeatheredCopperGrate.class);
        register(WAXED_WEATHERED_COPPER_TRAPDOOR, BlockWaxedWeatheredCopperTrapdoor.class);
        register(WAXED_WEATHERED_CUT_COPPER, BlockWaxedWeatheredCutCopper.class);
        register(WAXED_WEATHERED_CUT_COPPER_SLAB, BlockWaxedWeatheredCutCopperSlab.class);
        register(WAXED_WEATHERED_CUT_COPPER_STAIRS, BlockWaxedWeatheredCutCopperStairs.class);
        register(WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, BlockWaxedWeatheredDoubleCutCopperSlab.class);
        register(WEATHERED_CHISELED_COPPER, BlockWeatheredChiseledCopper.class);
        register(WEATHERED_COPPER, BlockWeatheredCopper.class);// done.
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
        register(WHITE_CANDLE, BlockWhiteCandle.class);// done.
        register(WHITE_CANDLE_CAKE, BlockWhiteCandleCake.class);// done.
        register(WHITE_CARPET, BlockWhiteCarpet.class);// done.
        register(WHITE_CONCRETE, BlockWhiteConcrete.class);// done.
        register(WHITE_CONCRETE_POWDER, BlockWhiteConcretePowder.class);// done.
        register(WHITE_GLAZED_TERRACOTTA, BlockWhiteGlazedTerracotta.class);// done.
        register(WHITE_SHULKER_BOX, BlockWhiteShulkerBox.class);// done.
        register(WHITE_STAINED_GLASS, BlockWhiteStainedGlass.class);// done.
        register(WHITE_STAINED_GLASS_PANE, BlockWhiteStainedGlassPane.class);// done.
        register(WHITE_TERRACOTTA, BlockWhiteTerracotta.class);// done.
        register(WHITE_WOOL, BlockWhiteWool.class);// done.
        register(WITHER_ROSE, BlockWitherRose.class);// done.
        register(WOOD, BlockWood.class);// done.
        register(WOODEN_BUTTON, BlockWoodenButton.class);// done.
        register(WOODEN_DOOR, BlockWoodenDoor.class);// done.
        register(WOODEN_PRESSURE_PLATE, BlockWoodenPressurePlate.class);
        register(WOODEN_SLAB, BlockWoodenSlab.class);
        register(YELLOW_CANDLE, BlockYellowCandle.class);// done.
        register(YELLOW_CANDLE_CAKE, BlockYellowCandleCake.class);// done.
        register(YELLOW_CARPET, BlockYellowCarpet.class);// done.
        register(YELLOW_CONCRETE, BlockYellowConcrete.class);// done.
        register(YELLOW_CONCRETE_POWDER, BlockYellowConcretePowder.class);// done.
        register(YELLOW_FLOWER, BlockYellowFlower.class);
        register(YELLOW_GLAZED_TERRACOTTA, BlockYellowGlazedTerracotta.class);// done.
        register(YELLOW_SHULKER_BOX, BlockYellowShulkerBox.class);// done.
        register(YELLOW_STAINED_GLASS, BlockYellowStainedGlass.class);// done.
        register(YELLOW_STAINED_GLASS_PANE, BlockYellowStainedGlassPane.class);// done.
        register(YELLOW_TERRACOTTA, BlockYellowTerracotta.class);// done.
        register(YELLOW_WOOL, BlockYellowWool.class);// done.
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
    public OK<?> register(String key, Class<? extends Block> value) {
        if (Modifier.isAbstract(value.getModifiers())) {
            return new OK<>(false, new IllegalArgumentException("you cant register a abstract block class!"));
        }
        try {
            Field properties = value.getDeclaredField("PROPERTIES");
            properties.setAccessible(true);
            int modifiers = properties.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && properties.getType() == BlockProperties.class) {
                BlockProperties blockProperties = (BlockProperties) properties.get(value);
                FastConstructor<? extends Block> c = FastConstructor.create(value.getConstructor(BlockState.class));
                if (CACHE_CONSTRUCTORS.putIfAbsent(blockProperties.getIdentifier(), c) == null) {
                    KEYSET.add(blockProperties.getIdentifier());
                    PROPERTIES.put(blockProperties.getIdentifier(), blockProperties);
                    return OK.TRUE;
                }
                return new OK<>(false, new IllegalArgumentException("This block has already been registered with the identifier: " + blockProperties.getIdentifier()));
            } else {
                return new OK<>(false, new IllegalArgumentException("There must define a field `public static final BlockProperties PROPERTIES` in this class!"));
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            return new OK<>(false, e);
        }
    }

    public BlockProperties getBlockProperties(String identifier) {
        BlockProperties properties = PROPERTIES.get(identifier);
        if (properties == null) {
            throw new IllegalArgumentException("Get the Block State from a unknown id: " + identifier);
        } else return properties;
    }

    @NotNull
    @Override
    public Block get(String identifier) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return new BlockAir();
        try {
            return (Block) constructor.invoke((Object) null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Block get(String identifier, int x, int y, int z) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return new BlockAir();
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

    @NotNull
    public Block get(String identifier, int x, int y, int z, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return new BlockAir();
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

    @NotNull
    public Block get(String identifier, int x, int y, int z, int layer, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(identifier);
        if (constructor == null) return new BlockAir();
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

    @NotNull
    public Block get(BlockState blockState) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return new BlockAir();
        try {
            return (Block) constructor.invoke(blockState);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Block get(BlockState blockState, int x, int y, int z) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return new BlockAir();
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

    @NotNull
    public Block get(BlockState blockState, int x, int y, int z, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return new BlockAir();
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

    @NotNull
    public Block get(BlockState blockState, int x, int y, int z, int layer, Level level) {
        FastConstructor<? extends Block> constructor = CACHE_CONSTRUCTORS.get(blockState.getIdentifier());
        if (constructor == null) return new BlockAir();
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
