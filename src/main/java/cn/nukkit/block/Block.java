package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.*;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockstate.*;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.customitem.ItemCustomTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.*;
import com.google.common.base.Preconditions;
import com.google.gson.JsonParser;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(info = "Implements IMutableBlockState only on PowerNukkit", since = "1.4.0.0-PN")
@SuppressWarnings({"java:S2160", "java:S3400"})
@Log4j2
public abstract class Block extends Position implements Metadatable, Cloneable, AxisAlignedBB, BlockID, IMutableBlockState {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Block[] EMPTY_ARRAY = new Block[0];

    //<editor-fold desc="static fields" defaultstate="collapsed">
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "It is being replaced by an other solution that don't require a fixed size")
    @PowerNukkitOnly
    public static final int MAX_BLOCK_ID = dynamic(868);

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "It's not a constant value, it may be changed on major updates and" +
            " plugins will have to be recompiled in order to update this value in the binary files, " +
            "it's also being replaced by the BlockState system")
    @PowerNukkitOnly
    public static final int DATA_BITS = dynamic(4);

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "It's not a constant value, it may be changed on major updates and" +
            " plugins will have to be recompiled in order to update this value in the binary files, " +
            "it's also being replaced by the BlockState system")
    @PowerNukkitOnly
    public static final int DATA_SIZE = dynamic(1 << DATA_BITS);

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "It's not a constant value, it may be changed on major updates and" +
            " plugins will have to be recompiled in order to update this value in the binary files, " +
            "it's also being replaced by the BlockState system")
    @PowerNukkitOnly
    public static final int DATA_MASK = dynamic(DATA_SIZE - 1);

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Not encapsulated, easy to break",
            replaceWith = "Block.get(int).getClass(), to register new blocks use registerBlockImplementation()")
    @SuppressWarnings({"java:S1444", "java:S2386"})
    @SuppressFBWarnings(value = "MS_PKGPROTECT", justification = "Changing it would break compatibility with some regular Nukkit plugins")
    public static Class<? extends Block>[] list = null;

    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN",
            replaceWith = "To register/override implementations use registerBlockImplementation(), " +
                    "to get the block with a given state use BlockState.of and than BlockState.getBlock()")
    @Deprecated
    @SuppressWarnings({"java:S1444", "java:S2386", "java:S1123", "java:S1133", "DeprecatedIsStillUsed"})
    @SuppressFBWarnings(value = "MS_PKGPROTECT", justification = "Changing it would break compatibility with some regular Nukkit plugins")
    public static Block[] fullList = null;

    @Deprecated
    @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN",
            replaceWith = "Block.getLightLevel() or Block.getLightLevel(int)")
    @SuppressWarnings({"java:S1444", "java:S2386"})
    @SuppressFBWarnings(value = "MS_PKGPROTECT", justification = "Changing it would break compatibility with some regular Nukkit plugins")
    public static int[] light = null;

    @Deprecated
    @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN",
            replaceWith = "Block.getLightFilter() or Block.getLightFilter(int)")
    @SuppressWarnings({"java:S1444", "java:S2386", "DeprecatedIsStillUsed"})
    @SuppressFBWarnings(value = "MS_PKGPROTECT", justification = "Changing it would break compatibility with some regular Nukkit plugins")
    public static int[] lightFilter = null;

    @Deprecated
    @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN",
            replaceWith = "Block.isSolid() or Block.isSolid(int)")
    @SuppressWarnings({"java:S1444", "java:S2386"})
    @SuppressFBWarnings(value = "MS_PKGPROTECT", justification = "Changing it would break compatibility with some regular Nukkit plugins")
    public static boolean[] solid = null;

    @Deprecated
    @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN",
            replaceWith = "Block.getHardness() or Block.getHardness(int)")
    @SuppressWarnings({"java:S1444", "java:S2386", "DeprecatedIsStillUsed"})
    @SuppressFBWarnings(value = "MS_PKGPROTECT", justification = "Changing it would break compatibility with some regular Nukkit plugins")
    public static double[] hardness = null;

    @Deprecated
    @DeprecationDetails(reason = "Not encapsulated, easy to break", since = "1.4.0.0-PN",
            replaceWith = "Block.isTransparent() or Block.isTransparent(int)")
    @SuppressWarnings({"java:S1444", "java:S2386", "DeprecatedIsStillUsed"})
    @SuppressFBWarnings(value = "MS_PKGPROTECT", justification = "Changing it would break compatibility with some regular Nukkit plugins")
    public static boolean[] transparent = null;

    private static boolean[] diffusesSkyLight = null;

    @PowerNukkitXOnly
    private static final List<CustomBlockDefinition> CUSTOM_BLOCK_DEFINITIONS = new ArrayList<>();

    @PowerNukkitXOnly
    public static final Int2ObjectMap<CustomBlock> ID_TO_CUSTOM_BLOCK = new Int2ObjectOpenHashMap<>();

    @PowerNukkitXOnly
    public static final ConcurrentHashMap<String, Integer> CUSTOM_BLOCK_ID_MAP = new ConcurrentHashMap<>();

    /**
     * if a block has can have variants
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "It's being replaced by the BlockState system")
    @SuppressWarnings({"java:S1444", "java:S2386"})
    public static boolean[] hasMeta = null;

    private static boolean initializing;

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public static boolean isInitializing() {
        return initializing;
    }
    //</editor-fold>

    //<editor-fold desc="initialization" defaultstate="collapsed">
    @SuppressWarnings("unchecked")
    public static void init() {
        if (list == null) {
            list = new Class[MAX_BLOCK_ID];
            fullList = new Block[MAX_BLOCK_ID * (1 << DATA_BITS)];
            light = new int[MAX_BLOCK_ID];
            lightFilter = new int[MAX_BLOCK_ID];
            solid = new boolean[MAX_BLOCK_ID];
            hardness = new double[MAX_BLOCK_ID];
            transparent = new boolean[MAX_BLOCK_ID];
            diffusesSkyLight = new boolean[MAX_BLOCK_ID];
            hasMeta = new boolean[MAX_BLOCK_ID];

            list[AIR] = BlockAir.class; //0
            list[STONE] = BlockStone.class; //1
            list[GRASS] = BlockGrass.class; //2
            list[DIRT] = BlockDirt.class; //3
            list[COBBLESTONE] = BlockCobblestone.class; //4
            list[PLANKS] = BlockPlanks.class; //5
            list[SAPLING] = BlockSapling.class; //6
            list[BEDROCK] = BlockBedrock.class; //7
            list[FLOWING_WATER] = BlockWater.class; //8
            list[STILL_WATER] = BlockWaterStill.class; //9
            list[FLOWING_LAVA] = BlockLava.class; //10
            list[STILL_LAVA] = BlockLavaStill.class; //11
            list[SAND] = BlockSand.class; //12
            list[GRAVEL] = BlockGravel.class; //13
            list[GOLD_ORE] = BlockOreGold.class; //14
            list[IRON_ORE] = BlockOreIron.class; //15
            list[COAL_ORE] = BlockOreCoal.class; //16
            list[LOG] = BlockWood.class; //17
            list[LEAVES] = BlockLeaves.class; //18
            list[SPONGE] = BlockSponge.class; //19
            list[GLASS] = BlockGlass.class; //20
            list[LAPIS_ORE] = BlockOreLapis.class; //21
            list[LAPIS_BLOCK] = BlockLapis.class; //22
            list[DISPENSER] = BlockDispenser.class; //23
            list[SANDSTONE] = BlockSandstone.class; //24
            list[NOTEBLOCK] = BlockNoteblock.class; //25
            list[BED_BLOCK] = BlockBed.class; //26
            list[POWERED_RAIL] = BlockRailPowered.class; //27
            list[DETECTOR_RAIL] = BlockRailDetector.class; //28
            list[STICKY_PISTON] = BlockPistonSticky.class; //29
            list[COBWEB] = BlockCobweb.class; //30
            list[TALL_GRASS] = BlockTallGrass.class; //31
            list[DEAD_BUSH] = BlockDeadBush.class; //32
            list[PISTON] = BlockPiston.class; //33
            list[PISTON_ARM_COLLISION] = BlockPistonHead.class; //34
            list[WOOL] = BlockWool.class; //35
            list[DANDELION] = BlockDandelion.class; //37
            list[RED_FLOWER] = BlockFlower.class; //38
            list[BROWN_MUSHROOM] = BlockMushroomBrown.class; //39
            list[RED_MUSHROOM] = BlockMushroomRed.class; //40
            list[GOLD_BLOCK] = BlockGold.class; //41
            list[IRON_BLOCK] = BlockIron.class; //42
            list[DOUBLE_STONE_SLAB] = BlockDoubleSlabStone.class; //43
            list[STONE_SLAB] = BlockSlabStone.class; //44
            list[BRICKS_BLOCK] = BlockBricks.class; //45
            list[TNT] = BlockTNT.class; //46
            list[BOOKSHELF] = BlockBookshelf.class; //47
            list[MOSSY_COBBLESTONE] = BlockMossStone.class; //48
            list[OBSIDIAN] = BlockObsidian.class; //49
            list[TORCH] = BlockTorch.class; //50
            list[FIRE] = BlockFire.class; //51
            list[MOB_SPAWNER] = BlockMobSpawner.class; //52
            list[OAK_STAIRS] = BlockStairsWood.class; //53
            list[CHEST] = BlockChest.class; //54
            list[REDSTONE_WIRE] = BlockRedstoneWire.class; //55
            list[DIAMOND_ORE] = BlockOreDiamond.class; //56
            list[DIAMOND_BLOCK] = BlockDiamond.class; //57
            list[CRAFTING_TABLE] = BlockCraftingTable.class; //58
            list[WHEAT_BLOCK] = BlockWheat.class; //59
            list[FARMLAND] = BlockFarmland.class; //60
            list[FURNACE] = BlockFurnace.class; //61
            list[LIT_FURNACE] = BlockFurnaceBurning.class; //62
            list[SIGN_POST] = BlockSignPost.class; //63
            list[OAK_DOOR_BLOCK] = BlockDoorWood.class; //64
            list[LADDER] = BlockLadder.class; //65
            list[RAIL] = BlockRail.class; //66
            list[STONE_STAIRS] = BlockStairsCobblestone.class; //67
            list[WALL_SIGN] = BlockWallSign.class; //68
            list[LEVER] = BlockLever.class; //69
            list[STONE_PRESSURE_PLATE] = BlockPressurePlateStone.class; //70
            list[IRON_DOOR_BLOCK] = BlockDoorIron.class; //71
            list[WOODEN_PRESSURE_PLATE] = BlockPressurePlateWood.class; //72
            list[REDSTONE_ORE] = BlockOreRedstone.class; //73
            list[LIT_REDSTONE_ORE] = BlockOreRedstoneGlowing.class; //74
            list[UNLIT_REDSTONE_TORCH] = BlockRedstoneTorchUnlit.class;
            list[REDSTONE_TORCH] = BlockRedstoneTorch.class; //76
            list[STONE_BUTTON] = BlockButtonStone.class; //77
            list[SNOW_LAYER] = BlockSnowLayer.class; //78
            list[ICE] = BlockIce.class; //79
            list[SNOW_BLOCK] = BlockSnow.class; //80
            list[CACTUS] = BlockCactus.class; //81
            list[CLAY_BLOCK] = BlockClay.class; //82
            list[REEDS] = BlockSugarcane.class; //83
            list[JUKEBOX] = BlockJukebox.class; //84
            list[FENCE] = BlockFence.class; //85
            list[PUMPKIN] = BlockPumpkin.class; //86
            list[NETHERRACK] = BlockNetherrack.class; //87
            list[SOUL_SAND] = BlockSoulSand.class; //88
            list[GLOWSTONE] = BlockGlowstone.class; //89
            list[NETHER_PORTAL] = BlockNetherPortal.class; //90
            list[LIT_PUMPKIN] = BlockPumpkinLit.class; //91
            list[CAKE_BLOCK] = BlockCake.class; //92
            list[UNPOWERED_REPEATER] = BlockRedstoneRepeaterUnpowered.class; //93
            list[POWERED_REPEATER] = BlockRedstoneRepeaterPowered.class; //94
            list[INVISIBLE_BEDROCK] = BlockBedrockInvisible.class; //95
            list[TRAPDOOR] = BlockTrapdoor.class; //96
            list[MONSTER_EGG] = BlockMonsterEgg.class; //97
            list[STONEBRICK] = BlockBricksStone.class; //98
            list[BROWN_MUSHROOM_BLOCK] = BlockHugeMushroomBrown.class; //99
            list[RED_MUSHROOM_BLOCK] = BlockHugeMushroomRed.class; //100
            list[IRON_BARS] = BlockIronBars.class; //101
            list[GLASS_PANE] = BlockGlassPane.class; //102
            list[MELON_BLOCK] = BlockMelon.class; //103
            list[PUMPKIN_STEM] = BlockStemPumpkin.class; //104
            list[MELON_STEM] = BlockStemMelon.class; //105
            list[VINE] = BlockVine.class; //106
            list[FENCE_GATE] = BlockFenceGate.class; //107
            list[BRICK_STAIRS] = BlockStairsBrick.class; //108
            list[STONE_BRICK_STAIRS] = BlockStairsStoneBrick.class; //109
            list[MYCELIUM] = BlockMycelium.class; //110
            list[WATERLILY] = BlockWaterLily.class; //111
            list[NETHER_BRICK_BLOCK] = BlockBricksNether.class; //112
            list[NETHER_BRICK_FENCE] = BlockFenceNetherBrick.class; //113
            list[NETHER_BRICKS_STAIRS] = BlockStairsNetherBrick.class; //114
            list[NETHER_WART_BLOCK] = BlockNetherWart.class; //115
            list[ENCHANTING_TABLE] = BlockEnchantingTable.class; //116
            list[BREWING_STAND_BLOCK] = BlockBrewingStand.class; //117
            list[CAULDRON_BLOCK] = BlockCauldron.class; //118
            list[END_PORTAL] = BlockEndPortal.class; //119
            list[END_PORTAL_FRAME] = BlockEndPortalFrame.class; //120
            list[END_STONE] = BlockEndStone.class; //121
            list[DRAGON_EGG] = BlockDragonEgg.class; //122
            list[REDSTONE_LAMP] = BlockRedstoneLamp.class; //123
            list[LIT_REDSTONE_LAMP] = BlockRedstoneLampLit.class; //124
            list[DROPPER] = BlockDropper.class; //125
            list[ACTIVATOR_RAIL] = BlockRailActivator.class; //126
            list[COCOA] = BlockCocoa.class; //127
            list[SANDSTONE_STAIRS] = BlockStairsSandstone.class; //128
            list[EMERALD_ORE] = BlockOreEmerald.class; //129
            list[ENDER_CHEST] = BlockEnderChest.class; //130
            list[TRIPWIRE_HOOK] = BlockTripWireHook.class;
            list[TRIP_WIRE] = BlockTripWire.class; //132
            list[EMERALD_BLOCK] = BlockEmerald.class; //133
            list[SPRUCE_STAIRS] = BlockStairsSpruce.class; //134
            list[BIRCH_STAIRS] = BlockStairsBirch.class; //135
            list[JUNGLE_STAIRS] = BlockStairsJungle.class; //136
            list[COMMAND_BLOCK] = BlockCommandBlock.class; //137

            list[BEACON] = BlockBeacon.class; //138
            list[STONE_WALL] = BlockWall.class; //139
            list[FLOWER_POT_BLOCK] = BlockFlowerPot.class; //140
            list[CARROT_BLOCK] = BlockCarrot.class; //141
            list[POTATO_BLOCK] = BlockPotato.class; //142
            list[WOODEN_BUTTON] = BlockButtonWooden.class; //143
            list[SKULL_BLOCK] = BlockSkull.class; //144
            list[ANVIL] = BlockAnvil.class; //145
            list[TRAPPED_CHEST] = BlockTrappedChest.class; //146
            list[LIGHT_WEIGHTED_PRESSURE_PLATE] = BlockWeightedPressurePlateLight.class; //147
            list[HEAVY_WEIGHTED_PRESSURE_PLATE] = BlockWeightedPressurePlateHeavy.class; //148
            list[UNPOWERED_COMPARATOR] = BlockRedstoneComparatorUnpowered.class; //149
            list[POWERED_COMPARATOR] = BlockRedstoneComparatorPowered.class; //149
            list[DAYLIGHT_DETECTOR] = BlockDaylightDetector.class; //151
            list[REDSTONE_BLOCK] = BlockRedstone.class; //152
            list[QUARTZ_ORE] = BlockOreQuartz.class; //153
            list[HOPPER_BLOCK] = BlockHopper.class; //154
            list[QUARTZ_BLOCK] = BlockQuartz.class; //155
            list[QUARTZ_STAIRS] = BlockStairsQuartz.class; //156
            list[DOUBLE_WOOD_SLAB] = BlockDoubleSlabWood.class; //157
            list[WOOD_SLAB] = BlockSlabWood.class; //158
            list[STAINED_TERRACOTTA] = BlockTerracottaStained.class; //159
            list[STAINED_GLASS_PANE] = BlockGlassPaneStained.class; //160

            list[LEAVES2] = BlockLeaves2.class; //161
            list[WOOD2] = BlockWood2.class; //162
            list[ACACIA_WOOD_STAIRS] = BlockStairsAcacia.class; //163
            list[DARK_OAK_WOOD_STAIRS] = BlockStairsDarkOak.class; //164
            list[SLIME_BLOCK] = BlockSlime.class; //165

            list[IRON_TRAPDOOR] = BlockTrapdoorIron.class; //167
            list[PRISMARINE] = BlockPrismarine.class; //168
            list[SEA_LANTERN] = BlockSeaLantern.class; //169
            list[HAY_BALE] = BlockHayBale.class; //170
            list[CARPET] = BlockCarpet.class; //171
            list[TERRACOTTA] = BlockTerracotta.class; //172
            list[COAL_BLOCK] = BlockCoal.class; //173
            list[PACKED_ICE] = BlockIcePacked.class; //174
            list[DOUBLE_PLANT] = BlockDoublePlant.class; //175
            list[STANDING_BANNER] = BlockBanner.class; //176
            list[WALL_BANNER] = BlockWallBanner.class; //177
            list[DAYLIGHT_DETECTOR_INVERTED] = BlockDaylightDetectorInverted.class; //178
            list[RED_SANDSTONE] = BlockRedSandstone.class; //179
            list[RED_SANDSTONE_STAIRS] = BlockStairsRedSandstone.class; //180
            list[DOUBLE_RED_SANDSTONE_SLAB] = BlockDoubleSlabRedSandstone.class; //181
            list[RED_SANDSTONE_SLAB] = BlockSlabRedSandstone.class; //182
            list[FENCE_GATE_SPRUCE] = BlockFenceGateSpruce.class; //183
            list[FENCE_GATE_BIRCH] = BlockFenceGateBirch.class; //184
            list[FENCE_GATE_JUNGLE] = BlockFenceGateJungle.class; //185
            list[FENCE_GATE_DARK_OAK] = BlockFenceGateDarkOak.class; //186
            list[FENCE_GATE_ACACIA] = BlockFenceGateAcacia.class; //187

            list[CHAIN_COMMAND_BLOCK] = BlockCommandBlockChain.class; //189
            list[REPEATING_COMMAND_BLOCK] = BlockCommandBlockRepeating.class; //190

            list[SPRUCE_DOOR_BLOCK] = BlockDoorSpruce.class; //193
            list[BIRCH_DOOR_BLOCK] = BlockDoorBirch.class; //194
            list[JUNGLE_DOOR_BLOCK] = BlockDoorJungle.class; //195
            list[ACACIA_DOOR_BLOCK] = BlockDoorAcacia.class; //196
            list[DARK_OAK_DOOR_BLOCK] = BlockDoorDarkOak.class; //197
            list[GRASS_PATH] = BlockGrassPath.class; //198
            list[ITEM_FRAME_BLOCK] = BlockItemFrame.class; //199
            list[CHORUS_FLOWER] = BlockChorusFlower.class; //200
            list[PURPUR_BLOCK] = BlockPurpur.class; //201

            list[PURPUR_STAIRS] = BlockStairsPurpur.class; //203

            list[UNDYED_SHULKER_BOX] = BlockUndyedShulkerBox.class; //205
            list[END_BRICKS] = BlockBricksEndStone.class; //206
            list[ICE_FROSTED] = BlockIceFrosted.class; //207
            list[END_ROD] = BlockEndRod.class; //208
            list[END_GATEWAY] = BlockEndGateway.class; //209
            list[ALLOW] = BlockAllow.class; //210
            list[DENY] = BlockDeny.class; //211
            list[BORDER_BLOCK] = BlockBorder.class; //212
            list[MAGMA] = BlockMagma.class; //213
            list[BLOCK_NETHER_WART_BLOCK] = BlockNetherWartBlock.class; //214
            list[RED_NETHER_BRICK] = BlockBricksRedNether.class; //215
            list[BONE_BLOCK] = BlockBone.class; //216
            list[STRUCTURE_VOID] = BlockStructureVoid.class; //217
            list[SHULKER_BOX] = BlockShulkerBox.class; //218
            list[PURPLE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedPurple.class; //219
            list[WHITE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedWhite.class; //220
            list[ORANGE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedOrange.class; //221
            list[MAGENTA_GLAZED_TERRACOTTA] = BlockTerracottaGlazedMagenta.class; //222
            list[LIGHT_BLUE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedLightBlue.class; //223
            list[YELLOW_GLAZED_TERRACOTTA] = BlockTerracottaGlazedYellow.class; //224
            list[LIME_GLAZED_TERRACOTTA] = BlockTerracottaGlazedLime.class; //225
            list[PINK_GLAZED_TERRACOTTA] = BlockTerracottaGlazedPink.class; //226
            list[GRAY_GLAZED_TERRACOTTA] = BlockTerracottaGlazedGray.class; //227
            list[SILVER_GLAZED_TERRACOTTA] = BlockTerracottaGlazedSilver.class; //228
            list[CYAN_GLAZED_TERRACOTTA] = BlockTerracottaGlazedCyan.class; //229

            list[BLUE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedBlue.class; //231
            list[BROWN_GLAZED_TERRACOTTA] = BlockTerracottaGlazedBrown.class; //232
            list[GREEN_GLAZED_TERRACOTTA] = BlockTerracottaGlazedGreen.class; //233
            list[RED_GLAZED_TERRACOTTA] = BlockTerracottaGlazedRed.class; //234
            list[BLACK_GLAZED_TERRACOTTA] = BlockTerracottaGlazedBlack.class; //235
            list[CONCRETE] = BlockConcrete.class; //236
            list[CONCRETE_POWDER] = BlockConcretePowder.class; //237

            list[CHORUS_PLANT] = BlockChorusPlant.class; //240
            list[STAINED_GLASS] = BlockGlassStained.class; //241
            list[PODZOL] = BlockPodzol.class; //243
            list[BEETROOT_BLOCK] = BlockBeetroot.class; //244
            list[STONECUTTER] = BlockStonecutter.class; //245
            list[GLOWING_OBSIDIAN] = BlockObsidianGlowing.class; //246
            list[NETHER_REACTOR] = BlockNetherReactor.class; //247 Should not be removed
            list[INFO_UPDATE] = BlockInfoUpdate.class;//248 Don't use this Block.
            list[MOVING_BLOCK] = BlockMoving.class; //250
            list[OBSERVER] = BlockObserver.class; //251
            list[STRUCTURE_BLOCK] = BlockStructure.class; //252

            list[PRISMARINE_STAIRS] = BlockStairsPrismarine.class; //257
            list[DARK_PRISMARINE_STAIRS] = BlockStairsDarkPrismarine.class; //258
            list[PRISMARINE_BRICKS_STAIRS] = BlockStairsPrismarineBrick.class; //259
            list[STRIPPED_SPRUCE_LOG] = BlockWoodStrippedSpruce.class; //260
            list[STRIPPED_BIRCH_LOG] = BlockWoodStrippedBirch.class; //261
            list[STRIPPED_JUNGLE_LOG] = BlockWoodStrippedJungle.class; //262
            list[STRIPPED_ACACIA_LOG] = BlockWoodStrippedAcacia.class; //263
            list[STRIPPED_DARK_OAK_LOG] = BlockWoodStrippedDarkOak.class; //264
            list[STRIPPED_OAK_LOG] = BlockWoodStrippedOak.class; //265
            list[BLUE_ICE] = BlockBlueIce.class; //266

            list[SEAGRASS] = BlockSeagrass.class; //385
            list[CORAL] = BlockCoral.class; //386
            list[CORAL_BLOCK] = BlockCoralBlock.class; //387
            list[CORAL_FAN] = BlockCoralFan.class; //388
            list[CORAL_FAN_DEAD] = BlockCoralFanDead.class; //389
            list[CORAL_FAN_HANG] = BlockCoralFanHang.class; //390
            list[CORAL_FAN_HANG2] = BlockCoralFanHang2.class; //391
            list[CORAL_FAN_HANG3] = BlockCoralFanHang3.class; //392
            list[BLOCK_KELP] = BlockKelp.class; //393
            list[DRIED_KELP_BLOCK] = BlockDriedKelpBlock.class; //394
            list[ACACIA_BUTTON] = BlockButtonAcacia.class; //395
            list[BIRCH_BUTTON] = BlockButtonBirch.class; //396
            list[DARK_OAK_BUTTON] = BlockButtonDarkOak.class; //397
            list[JUNGLE_BUTTON] = BlockButtonJungle.class; //398
            list[SPRUCE_BUTTON] = BlockButtonSpruce.class; //399
            list[ACACIA_TRAPDOOR] = BlockTrapdoorAcacia.class; //400
            list[BIRCH_TRAPDOOR] = BlockTrapdoorBirch.class; //401
            list[DARK_OAK_TRAPDOOR] = BlockTrapdoorDarkOak.class; //402
            list[JUNGLE_TRAPDOOR] = BlockTrapdoorJungle.class; //403
            list[SPRUCE_TRAPDOOR] = BlockTrapdoorSpruce.class; //404
            list[ACACIA_PRESSURE_PLATE] = BlockPressurePlateAcacia.class; //405
            list[BIRCH_PRESSURE_PLATE] = BlockPressurePlateBirch.class; //406
            list[DARK_OAK_PRESSURE_PLATE] = BlockPressurePlateDarkOak.class; //407
            list[JUNGLE_PRESSURE_PLATE] = BlockPressurePlateJungle.class; //408
            list[SPRUCE_PRESSURE_PLATE] = BlockPressurePlateSpruce.class; //409
            list[CARVED_PUMPKIN] = BlockCarvedPumpkin.class; //410
            list[SEA_PICKLE] = BlockSeaPickle.class; //411
            list[CONDUIT] = BlockConduit.class; //412

            list[TURTLE_EGG] = BlockTurtleEgg.class; //414
            list[BUBBLE_COLUMN] = BlockBubbleColumn.class; //415
            list[BARRIER] = BlockBarrier.class; //416
            list[STONE_SLAB3] = BlockSlabStone3.class; //417
            list[BAMBOO] = BlockBamboo.class; //418
            list[BAMBOO_SAPLING] = BlockBambooSapling.class; //419
            list[SCAFFOLDING] = BlockScaffolding.class; //420
            list[STONE_SLAB4] = BlockSlabStone4.class; //421
            list[DOUBLE_STONE_SLAB3] = BlockDoubleSlabStone3.class; //422
            list[DOUBLE_STONE_SLAB4] = BlockDoubleSlabStone4.class; //422

            list[GRANITE_STAIRS] = BlockStairsGranite.class; //424
            list[DIORITE_STAIRS] = BlockStairsDiorite.class; //425
            list[ANDESITE_STAIRS] = BlockStairsAndesite.class; //426
            list[POLISHED_GRANITE_STAIRS] = BlockStairsGranitePolished.class; //427
            list[POLISHED_DIORITE_STAIRS] = BlockStairsDioritePolished.class; //428
            list[POLISHED_ANDESITE_STAIRS] = BlockStairsAndesitePolished.class; //429
            list[MOSSY_STONE_BRICK_STAIRS] = BlockStairsMossyStoneBrick.class; //430
            list[SMOOTH_RED_SANDSTONE_STAIRS] = BlockStairsSmoothRedSandstone.class; //431
            list[SMOOTH_SANDSTONE_STAIRS] = BlockStairsSmoothSandstone.class; //432
            list[END_BRICK_STAIRS] = BlockStairsEndBrick.class; //433
            list[MOSSY_COBBLESTONE_STAIRS] = BlockStairsMossyCobblestone.class; //434
            list[NORMAL_STONE_STAIRS] = BlockStairsStone.class; //435

            list[SMOOTH_STONE] = BlockSmoothStone.class; //438
            list[RED_NETHER_BRICK_STAIRS] = BlockStairsRedNetherBrick.class; //439
            list[SMOOTH_QUARTZ_STAIRS] = BlockStairsSmoothQuartz.class; //440

            list[SPRUCE_STANDING_SIGN] = BlockSpruceSignPost.class; //436
            list[SPRUCE_WALL_SIGN] = BlockSpruceWallSign.class; //437

            list[BIRCH_STANDING_SIGN] = BlockBirchSignPost.class; //441
            list[BIRCH_WALL_SIGN] = BlockBirchWallSign.class; //442
            list[JUNGLE_STANDING_SIGN] = BlockJungleSignPost.class; //443
            list[JUNGLE_WALL_SIGN] = BlockJungleWallSign.class; //444
            list[ACACIA_STANDING_SIGN] = BlockAcaciaSignPost.class; //445
            list[ACACIA_WALL_SIGN] = BlockAcaciaWallSign.class; //446
            list[DARKOAK_STANDING_SIGN] = BlockDarkOakSignPost.class; //447
            list[DARKOAK_WALL_SIGN] = BlockDarkOakWallSign.class; //448

            list[LECTERN] = BlockLectern.class; //449
            list[GRINDSTONE] = BlockGrindstone.class; //450
            list[BLAST_FURNACE] = BlockBlastFurnace.class; //451
            list[STONECUTTER_BLOCK] = BlockStonecutterBlock.class; //452
            list[SMOKER] = BlockSmoker.class; //453
            list[LIT_SMOKER] = BlockSmokerBurning.class; //454

            list[CARTOGRAPHY_TABLE] = BlockCartographyTable.class; //455
            list[FLETCHING_TABLE] = BlockFletchingTable.class; //456
            list[SMITHING_TABLE] = BlockSmithingTable.class; //457
            list[BARREL] = BlockBarrel.class; //458
            list[LOOM] = BlockLoom.class; //459

            list[BELL] = BlockBell.class; //462
            list[SWEET_BERRY_BUSH] = BlockSweetBerryBush.class; //462
            list[LANTERN] = BlockLantern.class; //463

            list[CAMPFIRE_BLOCK] = BlockCampfire.class; //464
            list[LAVA_CAULDRON] = BlockCauldronLava.class; //465
            list[JIGSAW] = BlockJigsaw.class; //466
            list[WOOD_BARK] = BlockWoodBark.class; //467
            list[COMPOSTER] = BlockComposter.class; //468
            list[LIT_BLAST_FURNACE] = BlockBlastFurnaceBurning.class; //469
            list[LIGHT_BLOCK] = BlockLight.class; //470
            list[WITHER_ROSE] = BlockWitherRose.class; //471

            list[STICKY_PISTON_ARM_COLLISION] = BlockPistonHeadSticky.class; //472
            list[BEE_NEST] = BlockBeeNest.class; //473
            list[BEEHIVE] = BlockBeehive.class; //474
            list[HONEY_BLOCK] = BlockHoney.class; //475
            list[HONEYCOMB_BLOCK] = BlockHoneycombBlock.class; //476
            list[LODESTONE] = BlockLodestone.class; //477
            list[CRIMSON_ROOTS] = BlockRootsCrimson.class; //478
            list[WARPED_ROOTS] = BlockRootsWarped.class; //479
            list[CRIMSON_STEM] = BlockStemCrimson.class; //480
            list[WARPED_STEM] = BlockStemWarped.class; //481
            list[WARPED_WART_BLOCK] = BlockWarpedWartBlock.class; //482 
            list[CRIMSON_FUNGUS] = BlockFungusCrimson.class; //483
            list[WARPED_FUNGUS] = BlockFungusWarped.class; //484
            list[SHROOMLIGHT] = BlockShroomlight.class; //485
            list[WEEPING_VINES] = BlockVinesWeeping.class; //486
            list[CRIMSON_NYLIUM] = BlockNyliumCrimson.class; //487
            list[WARPED_NYLIUM] = BlockNyliumWarped.class; //488
            list[BASALT] = BlockBasalt.class; //489
            list[POLISHED_BASALT] = BlockPolishedBasalt.class; //490
            list[SOUL_SOIL] = BlockSoulSoil.class; //491
            list[SOUL_FIRE] = BlockFireSoul.class; //492
            list[NETHER_SPROUTS_BLOCK] = BlockNetherSprout.class; //493 
            list[TARGET] = BlockTarget.class; //494
            list[STRIPPED_CRIMSON_STEM] = BlockStemStrippedCrimson.class; //495
            list[STRIPPED_WARPED_STEM] = BlockStemStrippedWarped.class; //496
            list[CRIMSON_PLANKS] = BlockPlanksCrimson.class; //497
            list[WARPED_PLANKS] = BlockPlanksWarped.class; //498
            list[CRIMSON_DOOR_BLOCK] = BlockDoorCrimson.class; //499
            list[WARPED_DOOR_BLOCK] = BlockDoorWarped.class; //500
            list[CRIMSON_TRAPDOOR] = BlockTrapdoorCrimson.class; //501
            list[WARPED_TRAPDOOR] = BlockTrapdoorWarped.class; //502
            // 503
            // 504
            list[CRIMSON_STANDING_SIGN] = BlockCrimsonSignPost.class; //505
            list[CRIMSON_WALL_SIGN] = BlockCrimsonWallSign.class; //506
            list[WARPED_STANDING_SIGN] = BlockWarpedSignPost.class; //507
            list[WARPED_WALL_SIGN] = BlockWarpedWallSign.class; //508
            list[CRIMSON_STAIRS] = BlockStairsCrimson.class; //509
            list[WARPED_STAIRS] = BlockStairsWarped.class; //510
            list[CRIMSON_FENCE] = BlockFenceCrimson.class; //511
            list[WARPED_FENCE] = BlockFenceWarped.class; //512
            list[CRIMSON_FENCE_GATE] = BlockFenceGateCrimson.class; //513
            list[WARPED_FENCE_GATE] = BlockFenceGateWarped.class; //514
            list[CRIMSON_BUTTON] = BlockButtonCrimson.class; //515
            list[WARPED_BUTTON] = BlockButtonWarped.class; //516
            list[CRIMSON_PRESSURE_PLATE] = BlockPressurePlateCrimson.class; //517
            list[WARPED_PRESSURE_PLATE] = BlockPressurePlateWarped.class; //518
            list[CRIMSON_SLAB] = BlockSlabCrimson.class; //519
            list[WARPED_SLAB] = BlockSlabWarped.class; //520
            list[CRIMSON_DOUBLE_SLAB] = BlockDoubleSlabCrimson.class; //521
            list[WARPED_DOUBLE_SLAB] = BlockDoubleSlabWarped.class; //522
            list[SOUL_TORCH] = BlockSoulTorch.class; //523
            list[SOUL_LANTERN] = BlockSoulLantern.class; //524
            list[NETHERITE_BLOCK] = BlockNetheriteBlock.class; //525
            list[ANCIENT_DERBRIS] = BlockAncientDebris.class; //526
            list[RESPAWN_ANCHOR] = BlockRespawnAnchor.class; //527
            list[BLACKSTONE] = BlockBlackstone.class; //528
            list[POLISHED_BLACKSTONE_BRICKS] = BlockBricksBlackstonePolished.class; //529
            list[POLISHED_BLACKSTONE_BRICK_STAIRS] = BlockStairsBrickBlackstonePolished.class; //530
            list[BLACKSTONE_STAIRS] = BlockStairsBlackstone.class; //531
            list[BLACKSTONE_WALL] = BlockWallBlackstone.class; //532
            list[POLISHED_BLACKSTONE_BRICK_WALL] = BlockWallBrickBlackstonePolished.class; //533
            list[CHISELED_POLISHED_BLACKSTONE] = BlockBlackstonePolishedChiseled.class; //534
            list[CRACKED_POLISHED_BLACKSTONE_BRICKS] = BlockBricksBlackstonePolishedCracked.class; //535
            list[GILDED_BLACKSTONE] = BlockBlackstoneGilded.class; //536
            list[BLACKSTONE_SLAB] = BlockSlabBlackstone.class; //537
            list[BLACKSTONE_DOUBLE_SLAB] = BlockDoubleSlabBlackstone.class; //538
            list[POLISHED_BLACKSTONE_BRICK_SLAB] = BlockSlabBrickBlackstonePolished.class; //539
            list[POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB] = BlockDoubleSlabBrickBlackstonePolished.class; //540
            list[CHAIN_BLOCK] = BlockChain.class; //541
            list[TWISTING_VINES] = BlockVinesTwisting.class; //542
            list[NETHER_GOLD_ORE] = BlockOreGoldNether.class; //543
            list[CRYING_OBSIDIAN] = BlockObsidianCrying.class; //544
            list[SOUL_CAMPFIRE_BLOCK] = BlockCampfireSoul.class; //545
            list[POLISHED_BLACKSTONE] = BlockBlackstonePolished.class; //546
            list[POLISHED_BLACKSTONE_STAIRS] = BlockStairsBlackstonePolished.class; //547
            list[POLISHED_BLACKSTONE_SLAB] = BlockSlabBlackstonePolished.class; //548
            list[POLISHED_BLACKSTONE_DOUBLE_SLAB] = BlockDoubleSlabBlackstonePolished.class; //549
            list[POLISHED_BLACKSTONE_PRESSURE_PLATE] = BlockPressurePlateBlackstonePolished.class; //550
            list[POLISHED_BLACKSTONE_BUTTON] = BlockButtonBlackstonePolished.class; //551
            list[POLISHED_BLACKSTONE_WALL] = BlockWallBlackstonePolished.class; //552
            list[WARPED_HYPHAE] = BlockHyphaeWarped.class; //553
            list[CRIMSON_HYPHAE] = BlockHyphaeCrimson.class; //554
            list[STRIPPED_CRIMSON_HYPHAE] = BlockHyphaeStrippedCrimson.class; //555
            list[STRIPPED_WARPED_HYPHAE] = BlockHyphaeStrippedWarped.class; //556
            list[CHISELED_NETHER_BRICKS] = BlockBricksNetherChiseled.class; //557
            list[CRACKED_NETHER_BRICKS] = BlockBricksNetherCracked.class; //558
            list[QUARTZ_BRICKS] = BlockBricksQuartz.class; //559
            // 560 Special block: minecraft:unknown
            list[POWDER_SNOW] = BlockPowderSnow.class; //561
            list[SCULK_SENSOR] = BlockSculkSensor.class; //562
            list[POINTED_DRIPSTONE] = BlockPointedDripstone.class; //563
            // 564 (unused)
            // 565 (unused)
            list[COPPER_ORE] = BlockOreCopper.class; //566
            list[LIGHTNING_ROD] = BlockLightningRod.class; //567
            // 568 (unused)
            // 569 (unused)
            // 570 (unused)
            // 571 (unused)
            list[DRIPSTONE_BLOCK] = BlockDripstone.class; //572
            list[DIRT_WITH_ROOTS] = BlockDirtWithRoots.class; //573
            list[HANGING_ROOTS] = BlockRootsHanging.class; //574
            list[MOSS_BLOCK] = BlockMoss.class; //575
            list[SPORE_BLOSSOM] = BlockSporeBlossom.class; //576
            list[CAVE_VINES] = BlockCaveVines.class; //577
            list[BIG_DRIPLEAF] = BlockBigDripleaf.class; //578
            list[AZALEA_LEAVES] = BlockAzaleaLeaves.class; //579
            list[AZALEA_LEAVES_FLOWERED] = BlockAzaleaLeavesFlowered.class; //580
            list[CALCITE] = BlockCalcite.class; //581
            list[AMETHYST_BLOCK] = BlockAmethyst.class; //582
            list[BUDDING_AMETHYST] = BlockBuddingAmethyst.class; //583
            list[AMETHYST_CLUSTER] = BlockAmethystCluster.class; //584
            list[LARGE_AMETHYST_BUD] = BlockLargeAmethystBud.class; //585
            list[MEDIUM_AMETHYST_BUD] = BlockMediumAmethystBud.class; //586
            list[SMALL_AMETHYST_BUD] = BlockSmallAmethystBud.class; //587
            list[TUFF] = BlockTuff.class; //588
            list[TINTED_GLASS] = BlockGlassTinted.class; //589
            list[MOSS_CARPET] = BlockMossCarpet.class; //590
            list[SMALL_DRIPLEAF_BLOCK] = BlockSmallDripleaf.class; //591
            list[AZALEA] = BlockAzalea.class; //592
            list[FLOWERING_AZALEA] = BlockAzaleaFlowering.class; //593
            list[GLOW_FRAME] = BlockItemFrameGlow.class; //594
            list[COPPER_BLOCK] = BlockCopper.class; //595
            list[EXPOSED_COPPER] = BlockCopperExposed.class; //596
            list[WEATHERED_COPPER] = BlockCopperWeathered.class; //597
            list[OXIDIZED_COPPER] = BlockCopperOxidized.class; //598
            list[WAXED_COPPER] = BlockCopperWaxed.class; //599
            list[WAXED_EXPOSED_COPPER] = BlockCopperExposedWaxed.class; //600
            list[WAXED_WEATHERED_COPPER] = BlockCopperWeatheredWaxed.class; //601
            list[CUT_COPPER] = BlockCopperCut.class; //602
            list[EXPOSED_CUT_COPPER] = BlockCopperCutExposed.class; //603
            list[WEATHERED_CUT_COPPER] = BlockCopperCutWeathered.class; //604
            list[OXIDIZED_CUT_COPPER] = BlockCopperCutOxidized.class; //605
            list[WAXED_CUT_COPPER] = BlockCopperCutWaxed.class; //606
            list[WAXED_EXPOSED_CUT_COPPER] = BlockCopperCutExposedWaxed.class; //607
            list[WAXED_WEATHERED_CUT_COPPER] = BlockCopperCutWeatheredWaxed.class; //608
            list[CUT_COPPER_STAIRS] = BlockStairsCopperCut.class; //609
            list[EXPOSED_CUT_COPPER_STAIRS] = BlockStairsCopperCutExposed.class; //610
            list[WEATHERED_CUT_COPPER_STAIRS] = BlockStairsCopperCutWeathered.class; //611
            list[OXIDIZED_CUT_COPPER_STAIRS] = BlockStairsCopperCutOxidized.class; //612
            list[WAXED_CUT_COPPER_STAIRS] = BlockStairsCopperCutWaxed.class; //613
            list[WAXED_EXPOSED_CUT_COPPER_STAIRS] = BlockStairsCopperCutExposedWaxed.class; //614
            list[WAXED_WEATHERED_CUT_COPPER_STAIRS] = BlockStairsCopperCutWeatheredWaxed.class; //615
            list[CUT_COPPER_SLAB] = BlockSlabCopperCut.class; //616
            list[EXPOSED_CUT_COPPER_SLAB] = BlockSlabCopperCutExposed.class; //617
            list[WEATHERED_CUT_COPPER_SLAB] = BlockSlabCopperCutWeathered.class; //618
            list[OXIDIZED_CUT_COPPER_SLAB] = BlockSlabCopperCutOxidized.class; //619
            list[WAXED_CUT_COPPER_SLAB] = BlockSlabCopperCutWaxed.class; //620
            list[WAXED_EXPOSED_CUT_COPPER_SLAB] = BlockSlabCopperCutExposedWaxed.class; //621
            list[WAXED_WEATHERED_CUT_COPPER_SLAB] = BlockSlabCopperCutWeatheredWaxed.class; //622
            list[DOUBLE_CUT_COPPER_SLAB] = BlockDoubleSlabCopperCut.class; //623
            list[EXPOSED_DOUBLE_CUT_COPPER_SLAB] = BlockDoubleSlabCopperCutExposed.class; //624
            list[WEATHERED_DOUBLE_CUT_COPPER_SLAB] = BlockDoubleSlabCopperCutWeathered.class; //625
            list[OXIDIZED_DOUBLE_CUT_COPPER_SLAB] = BlockDoubleSlabCopperCutOxidized.class; //626
            list[WAXED_DOUBLE_CUT_COPPER_SLAB] = BlockDoubleSlabCopperCutWaxed.class; //627
            list[WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB] = BlockDoubleSlabCopperCutExposedWaxed.class; //628
            list[WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB] = BlockDoubleSlabCopperCutWeatheredWaxed.class; //629
            list[CAVE_VINES_BODY_WITH_BERRIES] = BlockCaveVinesBodyWithBerries.class; //630
            list[CAVE_VINES_HEAD_WITH_BERRIES] = BlockCaveVinesHeadWithBerries.class; //631
            list[SMOOTH_BASALT] = BlockSmoothBasalt.class; //632
            list[DEEPSLATE] = BlockDeepslate.class; //633
            list[COBBLED_DEEPSLATE] = BlockDeepslateCobbled.class; //634
            list[COBBLED_DEEPSLATE_SLAB] = BlockSlabDeepslateCobbled.class; //635
            list[COBBLED_DEEPSLATE_STAIRS] = BlockStairsDeepslateCobbled.class; //636
            list[COBBLED_DEEPSLATE_WALL] = BlockWallDeepslateCobbled.class; //637
            list[POLISHED_DEEPSLATE] = BlockDeepslatePolished.class; //638
            list[POLISHED_DEEPSLATE_SLAB] = BlockSlabDeepslatePolished.class; //639
            list[POLISHED_DEEPSLATE_STAIRS] = BlockStairsDeepslatePolished.class; //640
            list[POLISHED_DEEPSLATE_WALL] = BlockWallDeepslatePolished.class; //641
            list[DEEPSLATE_TILES] = BlockTilesDeepslate.class; //642
            list[DEEPSLATE_TILE_SLAB] = BlockSlabTileDeepslate.class; //643
            list[DEEPSLATE_TILE_STAIRS] = BlockStairsTileDeepslate.class; //644
            list[DEEPSLATE_TILE_WALL] = BlockWallTileDeepslate.class; //645
            list[DEEPSLATE_BRICKS] = BlockBricksDeepslate.class; //646
            list[DEEPSLATE_BRICK_SLAB] = BlockSlabBrickDeepslate.class; //647
            list[DEEPSLATE_BRICK_STAIRS] = BlockStairsBrickDeepslate.class; //648
            list[DEEPSLATE_BRICK_WALL] = BlockWallBrickDeepslate.class; //649
            list[CHISELED_DEEPSLATE] = BlockDeepslateChiseled.class; //650
            list[COBBLED_DEEPSLATE_DOUBLE_SLAB] = BlockDoubleSlabDeepslateCobbled.class; //651
            list[POLISHED_DEEPSLATE_DOUBLE_SLAB] = BlockDoubleSlabDeepslatePolished.class; //652
            list[DEEPSLATE_TILE_DOUBLE_SLAB] = BlockDoubleSlabTileDeepslate.class; //653
            list[DEEPSLATE_BRICK_DOUBLE_SLAB] = BlockDoubleSlabBrickDeepslate.class; //654
            list[DEEPSLATE_LAPIS_ORE] = BlockOreLapisDeepslate.class; //655
            list[DEEPSLATE_IRON_ORE] = BlockOreIronDeepslate.class; //656
            list[DEEPSLATE_GOLD_ORE] = BlockOreGoldDeepslate.class; //657
            list[DEEPSLATE_REDSTONE_ORE] = BlockOreRedstoneDeepslate.class; //658
            list[LIT_DEEPSLATE_REDSTONE_ORE] = BlockOreRedstoneDeepslateGlowing.class; //659
            list[DEEPSLATE_DIAMOND_ORE] = BlockOreDiamondDeepslate.class; //660
            list[DEEPSLATE_COAL_ORE] = BlockOreCoalDeepslate.class; //661
            list[DEEPSLATE_EMERALD_ORE] = BlockOreEmeraldDeepslate.class; //662
            list[DEEPSLATE_COPPER_ORE] = BlockOreCopperDeepslate.class; //663
            list[CRACKED_DEEPSLATE_TILES] = BlockTilesDeepslateCracked.class; //664
            list[CRACKED_DEEPSLATE_BRICKS] = BlockBricksDeepslateCracked.class; //665
            list[GLOW_LICHEN] = BlockGlowLichen.class; //666
            list[CANDLE] = BlockCandle.class; //667
            list[WHITE_CANDLE] = BlockCandleWhite.class; //668
            list[ORANGE_CANDLE] = BlockCandleOrange.class; //669
            list[MAGENTA_CANDLE] = BlockCandleMagenta.class; //670
            list[LIGHT_BLUE_CANDLE] = BlockCandleLightBlue.class; //671
            list[YELLOW_CANDLE] = BlockCandleYellow.class; //672
            list[LIME_CANDLE] = BlockCandleLime.class; //673
            list[PINK_CANDLE] = BlockCandlePink.class; //674
            list[GRAY_CANDLE] = BlockCandleGray.class; //675
            list[LIGHT_GRAY_CANDLE] = BlockCandleLightGray.class; //676
            list[CYAN_CANDLE] = BlockCandleCyan.class; //677
            list[PURPLE_CANDLE] = BlockCandlePurple.class; //678
            list[BLUE_CANDLE] = BlockCandleBlue.class; //679
            list[BROWN_CANDLE] = BlockCandleBrown.class; //680
            list[GREEN_CANDLE] = BlockCandleGreen.class; //681
            list[RED_CANDLE] = BlockCandleRed.class; //682
            list[BLACK_CANDLE] = BlockCandleBlack.class; //683
            list[CANDLE_CAKE] = BlockCandleCake.class; //684
            list[WHITE_CANDLE_CAKE] = BlockCandleCakeWhite.class; //685
            list[ORANGE_CANDLE_CAKE] = BlockCandleCakeOrange.class; //686
            list[MAGENTA_CANDLE_CAKE] = BlockCandleCakeMagenta.class; //687
            list[LIGHT_BLUE_CANDLE_CAKE] = BlockCandleCakeLightBlue.class; //688
            list[YELLOW_CANDLE_CAKE] = BlockCandleCakeYellow.class; //689
            list[LIME_CANDLE_CAKE] = BlockCandleCakeLime.class; //690
            list[PINK_CANDLE_CAKE] = BlockCandleCakePink.class; //691
            list[GRAY_CANDLE_CAKE] = BlockCandleCakeGray.class; //692
            list[LIGHT_GRAY_CANDLE_CAKE] = BlockCandleCakeLightGray.class; //693
            list[CYAN_CANDLE_CAKE] = BlockCandleCakeCyan.class; //694
            list[PURPLE_CANDLE_CAKE] = BlockCandleCakePurple.class; //695
            list[BLUE_CANDLE_CAKE] = BlockCandleCakeBlue.class; //696
            list[BROWN_CANDLE_CAKE] = BlockCandleCakeBrown.class; //697
            list[GREEN_CANDLE_CAKE] = BlockCandleCakeGreen.class; //698
            list[RED_CANDLE_CAKE] = BlockCandleCakeRed.class; //699
            list[BLACK_CANDLE_CAKE] = BlockCandleCakeBlack.class; //700
            list[WAXED_OXIDIZED_COPPER] = BlockCopperOxidizedWaxed.class; //701
            list[WAXED_OXIDIZED_CUT_COPPER] = BlockCopperCutOxidizedWaxed.class; //702
            list[WAXED_OXIDIZED_CUT_COPPER_STAIRS] = BlockStairsCopperCutOxidizedWaxed.class; //703
            list[WAXED_OXIDIZED_CUT_COPPER_SLAB] = BlockSlabCopperCutOxidizedWaxed.class; //704
            list[WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB] = BlockDoubleSlabCopperCutOxidizedWaxed.class; //705
            list[RAW_IRON_BLOCK] = BlockRawIron.class; //706
            list[RAW_COPPER_BLOCK] = BlockRawCopper.class; //707
            list[RAW_GOLD_BLOCK] = BlockRawGold.class; //708
            list[INFESTED_DEEPSLATE] = BlockInfestedDeepslate.class; //709

            list[SCULK] = BlockSculk.class; //713
            list[SCULK_VEIN] = BlockSculkVein.class; //714
            list[SCULK_CATALYST] = BlockSculkCatalyst.class; //715
            list[SCULK_SHRIEKER] = BlockSculkShrieker.class; //716

            list[REINFORCED_DEEPSLATE] = BlockReinForcedDeepSlate.class; //721

            list[FROG_SPAWN] = BlockFrogSpawn.class; //723
            list[PEARLESCENT_FROGLIGHT] = BlockPearlescentFrogLight.class; //724
            list[VERDANT_FROGLIGHT] = BlockVerdantFrogLight.class; //725
            list[OCHRE_FROGLIGHT] = BlockOchreFrogLight.class; //726
            list[MANGROVE_LEAVES] = BlockMangroveLeaves.class; // 727

            list[MUD] = BlockMud.class; //728
            list[MANGROVE_PROPAGULE] = BlockMangrovePropagule.class; //729
            list[MUD_BRICKS] = BlockMudBrick.class; //730
            list[MANGROVE_PROPAGULE_HANGING] = BlockMangrovePropaguleHanging.class; //731
            list[PACKED_MUD] = BlockPackedMud.class; //732
            list[MUD_BRICK_SLAB] = BlockMudBrickSlab.class; //733
            list[MUD_BRICK_DOUBLE_SLAB] = BlockDoubleMudBrickSlab.class; //734
            list[MUD_BRICK_STAIRS] = BlockMudBrickStairs.class; //735
            list[MUD_BRICK_WALL] = BlockMudBrickWall.class; //736

            list[MANGROVE_ROOTS] = BlockMangroveRoots.class;//737
            list[MUDDY_MANGROVE_ROOTS] = BlockMuddyMangroveRoots.class;//738
            list[MANGROVE_LOG] = BlockMangroveLog.class;//739

            list[STRIPPED_MANGROVE_LOG] = BlockLogStrippedMangrove.class;//740
            list[MANGROVE_PLANKS] = BlockPlanksMangrove.class;//741
            list[MANGROVE_BUTTON] = BlockButtonMangrove.class;//742
            list[MANGROVE_STAIRS] = BlockStairMangrove.class;//743
            list[MANGROVE_SLAB] = BlockSlabMangrove.class;//744
            list[MANGROVE_PRESSURE_PLATE] = BlockPressurePlateMangrove.class;//745

            list[MANGROVE_FENCE] = BlockFenceMangrove.class;//746
            list[MANGROVE_FENCE_GATE] = BlockFenceGateMangrove.class;//747
            list[MANGROVE_DOOR] = BlockDoorMangrove.class;//748
            list[MANGROVE_STANDING_SIGN] = BlockMangroveSignPost.class;//749
            list[MANGROVE_WALL_SIGN] = BlockMangroveWallSign.class;//750
            list[MANGROVE_TRAPDOOR] = BlockTrapdoorMangrove.class;//751

            list[MANGROVE_WOOD] = BlockWoodMangrove.class;//752
            list[STRIPPED_MANGROVE_WOOD] = BlockWoodStrippedMangrove.class;//753
            list[DOUBLE_MANGROVE_SLAB] = BlockDoubleSlabMangrove.class;//754
            list[OAK_HANGING_SIGN] = BlockOakHangingSign.class;//755
            list[SPRUCE_HANGING_SIGN] = BlockSpruceHangingSign.class;//756
            list[BIRCH_HANGING_SIGN] = BlockBirchHangingSign.class;//757
            list[JUNGLE_HANGING_SIGN] = BlockJungleHangingSign.class;//758
            list[ACACIA_HANGING_SIGN] = BlockAcaciaHangingSign.class;//759
            list[DARK_OAK_HANGING_SIGN] = BlockDarkOakHangingSign.class;//760
            list[CRIMSON_HANGING_SIGN] = BlockCrimsonHangingSign.class;//761
            list[WARPED_HANGING_SIGN] = BlockWarpedHangingSign.class;//762
            list[MANGROVE_HANGING_SIGN] = BlockMangroveHangingSign.class;//763
            list[BAMBOO_MOSAIC] = BlockBambooMosaic.class;//764
            list[BAMBOO_PLANKS] = BlockBambooPlanks.class;//765
            list[BAMBOO_BUTTON] = BlockBambooButton.class;//766
            list[BAMBOO_STAIRS] = BlockBambooStairs.class;//767
            list[BAMBOO_SLAB] = BlockBambooSlab.class;//768
            list[BAMBOO_PRESSURE_PLATE] = BlockBambooPressurePlate.class;//769
            list[BAMBOO_FENCE] = BlockBambooFence.class;//770
            list[BAMBOO_FENCE_GATE] = BlockBambooFenceGate.class;//771
            list[BAMBOO_DOOR] = BlockBambooDoor.class;//772
            list[BAMBOO_STANDING_SIGN] = BlockBambooStandingSign.class;//773
            list[BAMBOO_WALL_SIGN] = BlockBambooWallSign.class;//774
            list[BAMBOO_TRAPDOOR] = BlockBambooTrapdoor.class;//775
            list[BAMBOO_DOUBLE_SLAB] = BlockBambooDoubleSlab.class;//776
            list[BAMBOO_HANGING_SIGN] = BlockBambooHangingSign.class;//777
            list[BAMBOO_MOSAIC_STAIRS] = BlockBambooMosaicStairs.class;//778
            list[BAMBOO_MOSAIC_SLAB] = BlockBambooMosaicSlab.class;//779
            list[BAMBOO_MOSAIC_DOUBLE_SLAB] = BlockBambooMosaicDoubleSlab.class;//780
            list[CHISELED_BOOKSHELF] = BlockChiseledBookshelf.class;//781
            list[BAMBOO_BLOCK] = BlockBambooBlock.class;//782
            list[STRIPPED_BAMBOO_BLOCK] = BlockStrippedBambooBlock.class;//783
            list[SUSPICIOUS_SAND] = BlockSuspiciousSand.class;//784

            list[CHERRY_BUTTON] = BlockButtonCherry.class;//785
            list[CHERRY_DOOR] = BlockDoorCherry.class;//786
            list[CHERRY_FENCE] = BlockFenceCherry.class;//787
            list[CHERRY_FENCE_GATE] = BlockFenceGateCherry.class;//788

            list[STRIPPED_CHERRY_LOG] = BlockLogStrippedCherry.class;//790
            list[CHERRY_LOG] = BlockCherryLog.class;//791
            list[CHERRY_PLANKS] = BlockPlanksCherry.class;//792
            list[CHERRY_PRESSURE_PLATE] = BlockPressurePlateCherry.class;//793
            list[CHERRY_SLAB] = BlockSlabCherry.class;//794
            list[DOUBLE_CHERRY_SLAB] = BlockDoubleSlabCherry.class;//795
            list[CHERRY_STAIRS] = BlockStairsCherry.class;//796
            list[CHERRY_STANDING_SIGN] = BlockCherrySignPost.class;//797
            list[CHERRY_TRAPDOOR] = BlockTrapdoorCherry.class;//798
            list[CHERRY_WALL_SIGN] = BlockCherryWallSign.class;//799
            list[STRIPPED_CHERRY_WOOD] = BlockWoodStrippedCherry.class;//800
            list[CHERRY_WOOD] = BlockWoodCherry.class;//801
            list[CHERRY_SAPLING] = BlockCherrySapling.class;//802
            list[CHERRY_LEAVES] = BlockCherryLeaves.class;//803
            list[PINK_PETALS] = BlockPinkPetals.class;//804
            initializing = true;

            for (int id = 0; id < MAX_BLOCK_ID; id++) {
                Class<? extends Block> c = list[id];
                if (c != null) {
                    Block block;
                    try {
                        block = c.getDeclaredConstructor().newInstance();
                        String persistenceName = block.getPersistenceName();
                        BlockStateRegistry.registerPersistenceName(id, persistenceName);
                        try {
                            Constructor<? extends Block> constructor = c.getDeclaredConstructor(int.class);
                            constructor.setAccessible(true);
                            for (int data = 0; data < (1 << DATA_BITS); ++data) {
                                int fullId = (id << DATA_BITS) | data;
                                Block b;
                                try {
                                    b = constructor.newInstance(data);
                                    if (b.getDamage() != data) {
                                        b = new BlockUnknown(id, data);
                                    }
                                } catch (InvocationTargetException wrapper) {
                                    Throwable uncaught = wrapper.getTargetException();
                                    if (!(uncaught instanceof InvalidBlockDamageException)) {
                                        log.error("Error while registering {} with meta {}", c.getName(), data, uncaught);
                                    }
                                    b = new BlockUnknown(id, data);
                                }
                                fullList[fullId] = b;
                            }
                            hasMeta[id] = true;
                        } catch (NoSuchMethodException ignore) {
                            for (int data = 0; data < DATA_SIZE; ++data) {
                                int fullId = (id << DATA_BITS) | data;
                                fullList[fullId] = block;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error while registering {}", c.getName(), e);
                        for (int data = 0; data < DATA_SIZE; ++data) {
                            fullList[(id << DATA_BITS) | data] = new BlockUnknown(id, data);
                        }
                        block = fullList[id << DATA_BITS];
                    }

                    solid[id] = block.isSolid();
                    transparent[id] = block.isTransparent();
                    diffusesSkyLight[id] = block.diffusesSkyLight();
                    hardness[id] = block.getHardness();
                    light[id] = block.getLightLevel();
                    lightFilter[id] = block.getLightFilter();
                } else {
                    lightFilter[id] = 1;
                    for (int data = 0; data < DATA_SIZE; ++data) {
                        fullList[(id << DATA_BITS) | data] = new BlockUnknown(id, data);
                    }
                }
            }
            initializing = false;
        }
    }
    //</editor-fold>

    //<editor-fold desc="static getters" defaultstate="collapsed">
    public static Block get(int id) {
        if (id < 0) {
            id = 255 - id;
        } else if (id > MAX_BLOCK_ID) {
            return ID_TO_CUSTOM_BLOCK.get(id).toCustomBlock();
        }
        return fullList[id << DATA_BITS].clone();
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
    public static Block get(int id, Integer meta) {
        if (id > MAX_BLOCK_ID) {
            return ID_TO_CUSTOM_BLOCK.get(id).toCustomBlock(meta);
        }
        if (id < 0) {
            id = 255 - id;
        }
        if (meta != null) {
            int iMeta = meta;
            if (iMeta <= DATA_SIZE) {
                return fullList[(id << DATA_BITS) | meta].clone();
            } else {
                Block block = fullList[id << DATA_BITS].clone();
                block.setDamage(iMeta);
                return block;
            }
        } else {
            return fullList[id << DATA_BITS].clone();
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
    public static Block get(int id, Integer meta, Position pos) {
        return get(id, meta, pos, 0);
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
    public static Block get(int id, Integer meta, Position pos, int layer) {
        Block block;

        if (id > MAX_BLOCK_ID) {
            block = ID_TO_CUSTOM_BLOCK.get(id).toCustomBlock(meta);
            block.x = pos.x;
            block.y = pos.y;
            block.z = pos.z;
            block.level = pos.level;
            block.layer = layer;
            return block;
        }

        if (id < 0) {
            id = 255 - id;
        }

        if (meta != null && meta > DATA_SIZE) {
            block = fullList[id << DATA_BITS].clone();
            block.setDamage(meta);
        } else {
            block = fullList[(id << DATA_BITS) | (meta == null ? 0 : meta)].clone();
        }

        if (pos != null) {
            block.x = pos.x;
            block.y = pos.y;
            block.z = pos.z;
            block.level = pos.level;
            block.layer = layer;
        }
        return block;
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
    public static Block get(int id, int data) {
        if (id > MAX_BLOCK_ID) {
            return ID_TO_CUSTOM_BLOCK.get(id).toCustomBlock(data);
        }

        if (id < 0) {
            id = 255 - id;
        }
        if (data < DATA_SIZE) {
            return fullList[(id << DATA_BITS) | data].clone();
        } else {
            Block block = fullList[(id << DATA_BITS)].clone();
            block.setDamage(data);
            return block;
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    public static Block get(int fullId, Level level, int x, int y, int z) {
        return get(fullId, level, x, y, z, 0);
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    public static Block get(int fullId, Level level, int x, int y, int z, int layer) {
        var id = fullId >> DATA_BITS;
        if (id > MAX_BLOCK_ID) {
            var block = ID_TO_CUSTOM_BLOCK.get(id).toCustomBlock((~(id << DATA_BITS)) & fullId);
            block.x = x;
            block.y = y;
            block.z = z;
            block.level = level;
            block.layer = layer;
            return block;
        }
        var block = fullList[fullId].clone();
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = level;
        block.layer = layer;
        return block;
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public static Block get(int id, int meta, Level level, int x, int y, int z) {
        return get(id, meta, level, x, y, z, 0);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockState.getBlock()", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public static Block get(int id, int meta, Level level, int x, int y, int z, int layer) {
        Block block;

        if (id > MAX_BLOCK_ID) {
            block = ID_TO_CUSTOM_BLOCK.get(id).toCustomBlock();
            block.x = x;
            block.y = y;
            block.z = z;
            block.level = level;
            block.layer = layer;
            return block;
        }

        if (meta <= DATA_SIZE) {
            block = fullList[id << DATA_BITS | meta].clone();
        } else {
            block = fullList[id << DATA_BITS].clone();
            block.setDamage(meta);
        }
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = level;
        block.layer = layer;
        return block;
    }
    //</editor-fold>

    @PowerNukkitOnly
    @Since("FUTURE")
    @SuppressWarnings("java:S1874")
    public static boolean isSolid(int blockId) {
        if (blockId < 0 || blockId >= solid.length) {
            return true;
        }
        return solid[blockId];
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public static boolean diffusesSkyLight(int blockId) {
        if (blockId < 0 || blockId >= diffusesSkyLight.length) {
            return false;
        }
        return diffusesSkyLight[blockId];
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @SuppressWarnings("java:S1874")
    public static double getHardness(int blockId) {
        if (blockId < 0 || blockId >= hardness.length) {
            return Double.MAX_VALUE;
        }
        return hardness[blockId];
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @SuppressWarnings("java:S1874")
    public static int getLightLevel(int blockId) {
        if (blockId < 0 || blockId >= light.length) {
            return 0;
        }
        return light[blockId];
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @SuppressWarnings("java:S1874")
    public static int getLightFilter(int blockId) {
        if (blockId < 0 || blockId >= lightFilter.length) {
            return 15;
        }
        return lightFilter[blockId];
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @SuppressWarnings("java:S1874")
    public static boolean isTransparent(int blockId) {
        if (blockId < 0 || blockId >= transparent.length) {
            return false;
        }
        return transparent[blockId];
    }

    /**
     * Register a new block implementation overriding the existing one.
     *
     * @param blockId            The block ID that will be registered. Can't be negative.
     * @param blockClass         The class that overrides {@link Block} and implements this block,
     *                           it must have a constructor without params and optionally one that accepts {@code Number} or {@code int}
     * @param persistenceName    The block persistence name, must use the format namespace:block_name
     * @param receivesRandomTick If the block should receive random ticks from the level
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static void registerBlockImplementation(int blockId, @NotNull Class<? extends Block> blockClass, @NotNull String persistenceName, boolean receivesRandomTick) {
        Preconditions.checkArgument(blockId >= 0, "Negative block id %s", blockId);
        Preconditions.checkNotNull(blockClass, "blockClass was null");
        Preconditions.checkNotNull(persistenceName, "persistenceName was null");
        Preconditions.checkArgument(blockId < MAX_BLOCK_ID, "blockId %s must be less than %s", blockId, MAX_BLOCK_ID);
        Block mainBlock;
        BlockProperties properties;
        try {
            mainBlock = blockClass.getConstructor().newInstance();
            mainBlock.clone(); // Make sure clone works
            properties = mainBlock.getProperties();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create the main block of " + blockClass, e);
        }

        list[blockId] = blockClass;
        solid[blockId] = mainBlock.isSolid();
        transparent[blockId] = mainBlock.isTransparent();
        diffusesSkyLight[blockId] = mainBlock.diffusesSkyLight();
        hardness[blockId] = mainBlock.getHardness();
        light[blockId] = mainBlock.getLightLevel();
        lightFilter[blockId] = mainBlock.getLightFilter();
        fullList[blockId << DATA_BITS] = mainBlock;

        boolean metaAdded = false;
        if (properties.getBitSize() > 0) {
            for (int data = 0; data < (1 << DATA_BITS); ++data) {
                int fullId = (blockId << DATA_BITS) | data;
                Constructor<? extends Block> constructor = null;
                Exception exception = null;
                try {
                    Constructor<? extends Block> testing = blockClass.getDeclaredConstructor(Number.class);
                    testing.newInstance(0).clone();
                    constructor = testing;
                } catch (ReflectiveOperationException e) {
                    exception = e;
                    try {
                        Constructor<? extends Block> testing = blockClass.getDeclaredConstructor(int.class);
                        testing.newInstance(0).clone();
                        constructor = testing;
                        exception = null;
                    } catch (ReflectiveOperationException e2) {
                        e.addSuppressed(e2);
                        try {
                            Constructor<? extends Block> testing = blockClass.getDeclaredConstructor(Integer.class);
                            testing.newInstance(0).clone();
                            constructor = testing;
                            exception = null;
                        } catch (ReflectiveOperationException e3) {
                            e.addSuppressed(e3);
                        }
                    }
                }

                Block b = null;
                if (constructor != null) {
                    try {
                        b = constructor.newInstance(data);
                        if (b.getDamage() != data) {
                            b = new BlockUnknown(blockId, data);
                        }
                    } catch (InvocationTargetException wrapper) {
                        Throwable uncaught = wrapper.getTargetException();
                        if (uncaught instanceof InvalidBlockStateException) {
                            b = new BlockUnknown(blockId, data);
                        }
                    } catch (ReflectiveOperationException e) {
                        exception = e;
                    }
                }

                if (b == null) {
                    try {
                        b = BlockState.of(blockId, data).getBlock();
                    } catch (InvalidBlockStateException e) {
                        b = new BlockUnknown(blockId, data);
                    } catch (Exception e) {
                        b = new BlockUnknown(blockId, data);
                        if (exception != null) {
                            exception.addSuppressed(e);
                        } else {
                            log.error("Error while registering {} with meta {}", blockClass.getName(), data, exception);
                        }
                    }
                }

                if (!metaAdded && !(b instanceof BlockUnknown)) {
                    metaAdded = true;
                }

                fullList[fullId] = b;
            }
            hasMeta[blockId] = metaAdded;
        } else {
            hasMeta[blockId] = false;
        }

        Level.setCanRandomTick(blockId, receivesRandomTick);
    }

    //自定义方块id从1000开始,按照FNV1 64bit计算hash值升序排序
    @PowerNukkitXOnly
    private static int nextBlockId = 1000;

    @PowerNukkitXOnly
    @Since("1.19.62-r1")
    private final static SortedMap<String, CustomBlock> SORTED_CUSTOM_BLOCK = new TreeMap<>(MinecraftNamespaceComparator::compareFNV);

    /**
     * 注册自定义方块
     *
     * @param blockClassList 传入自定义方块class List
     */
    @PowerNukkitXOnly
    public static OK<?> registerCustomBlock(@NotNull List<Class<? extends CustomBlock>> blockClassList) {
        if (!Server.getInstance().isEnableExperimentMode() || Server.getInstance().getConfig("settings.waterdogpe", false)) {
            return new OK<>(false, "The server does not have the experiment mode feature enabled.Unable to register custom block!");
        }
        for (var clazz : blockClassList) {
            CustomBlock block;
            try {
                var method = clazz.getDeclaredConstructor();
                method.setAccessible(true);
                block = method.newInstance();
                if (!SORTED_CUSTOM_BLOCK.containsKey(block.getNamespaceId())) {
                    SORTED_CUSTOM_BLOCK.put(block.getNamespaceId(), block);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                return new OK<>(false, e);
            } catch (NoSuchMethodException e) {
                return new OK<>(false, "Cannot find the parameterless constructor for this custom block:" + clazz.getCanonicalName());
            }
        }
        return new OK<Void>(true);
    }

    /**
     * 注册自定义方块
     *
     * @param blockNamespaceClassMap 传入自定义方块classMap { key: NamespaceID, value: Class }
     */
    @PowerNukkitXOnly
    public static OK<?> registerCustomBlock(@NotNull Map<String, Class<? extends CustomBlock>> blockNamespaceClassMap) {
        if (!Server.getInstance().isEnableExperimentMode() || Server.getInstance().getConfig("settings.waterdogpe", false)) {
            return new OK<>(false, "The server does not have the experiment mode feature enabled.Unable to register custom block!");
        }
        for (var entry : blockNamespaceClassMap.entrySet()) {
            if (!SORTED_CUSTOM_BLOCK.containsKey(entry.getKey())) {
                try {
                    var method = entry.getValue().getDeclaredConstructor();
                    method.setAccessible(true);
                    var block = method.newInstance();
                    SORTED_CUSTOM_BLOCK.put(entry.getKey(), block);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    return new OK<>(false, e);
                } catch (NoSuchMethodException e) {
                    return new OK<>(false, "Cannot find the parameterless constructor for this custom block:" + entry.getValue().getCanonicalName());
                }
            }
        }
        return new OK<Void>(true);
    }

    public static void initCustomBlock() {
        if (!SORTED_CUSTOM_BLOCK.isEmpty()) {
            for (var entry : SORTED_CUSTOM_BLOCK.entrySet()) {
                CUSTOM_BLOCK_ID_MAP.put(entry.getKey(), nextBlockId);//自定义方块标识符->自定义方块id
                ID_TO_CUSTOM_BLOCK.put(nextBlockId, entry.getValue());//自定义方块id->自定义方块
                CUSTOM_BLOCK_DEFINITIONS.add(entry.getValue().getDefinition());//行为包数据
                ++nextBlockId;
            }
            var blocks = ID_TO_CUSTOM_BLOCK.values().stream().toList();
            var result = BlockStateRegistry.registerCustomBlockState(blocks);//注册方块state
            if (!result.ok()) {
                throw new CustomBlockStateRegisterException("Register CustomBlock state error, please check all your CustomBlock plugins,contact the plugin author! Error:", result.getError());
            }
            RuntimeItems.getRuntimeMapping().registerCustomBlock(blocks);//注册物品
            blocks.forEach(b -> Item.addCreativeItem(b.toItem()));//注册创造栏物品
        }
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static void deleteAllCustomBlock() {
        SORTED_CUSTOM_BLOCK.clear();
        for (var block : ID_TO_CUSTOM_BLOCK.values()) {
            Item.removeCreativeItem(block.toItem());
        }
        RuntimeItems.getRuntimeMapping().deleteCustomBlock(ID_TO_CUSTOM_BLOCK.values().stream().toList());
        BlockStateRegistry.deleteCustomBlockState();
        ID_TO_CUSTOM_BLOCK.clear();
        CUSTOM_BLOCK_ID_MAP.clear();
        CUSTOM_BLOCK_DEFINITIONS.clear();
        nextBlockId = 1000;
    }

    @PowerNukkitXOnly
    @Since("1.19.31-r1")
    public static List<CustomBlockDefinition> getCustomBlockDefinitionList() {
        return new ArrayList<>(CUSTOM_BLOCK_DEFINITIONS);
    }

    @PowerNukkitXOnly
    public static HashMap<Integer, CustomBlock> getCustomBlockMap() {
        return new HashMap<>(ID_TO_CUSTOM_BLOCK);
    }

    @Nullable
    private MutableBlockState mutableState;

    @PowerNukkitOnly
    public int layer;

    protected Block() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public final MutableBlockState getMutableState() {
        if (mutableState == null) {
            mutableState = getProperties().createMutableState(getId());
        }
        return mutableState;
    }

    /**
     * Place and initialize a this block correctly in the world.
     * <p>The current instance must have level, x, y, z, and layer properties already set before calling this method.</p>
     *
     * @param item   The item being used to place the block. Should be used as an optional reference, may mismatch the block that is being placed depending on plugin implementations.
     * @param block  The current block that is in the world and is getting replaced by this instance. It has the same x, y, z, layer, and level as this block.
     * @param target The block that was clicked to create the place action in this block position.
     * @param face   The face that was clicked in the target block
     * @param fx     The detailed X coordinate of the clicked target block face
     * @param fy     The detailed Y coordinate of the clicked target block face
     * @param fz     The detailed Z coordinate of the clicked target block face
     * @param player The player that is placing the block. May be null.
     * @return {@code true} if the block was properly place. The implementation is responsible for reverting any partial change.
     */
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return this.getLevel().setBlock(this, this, true, true);
    }

    //http://minecraft.gamepedia.com/Breaking
    public boolean canHarvestWithHand() {  //used for calculating breaking time
        return true;
    }

    public boolean isBreakable(Item item) {
        return true;
    }

    public int tickRate() {
        return 10;
    }

    public boolean onBreak(Item item) {
        return this.getLevel().setBlock(this, layer, Block.get(BlockID.AIR), true, true);
    }

    public int onUpdate(int type) {
        return 0;
    }

    /**
     * 当玩家使用与左键或者右键方块时会触发，常被用于处理例如物品展示框左键掉落物品这种逻辑<br>
     * 触发点在{@link Player}的onBlockBreakStart中
     * <p>
     * It will be triggered when the player uses the left or right click on the block, which is often used to deal with logic such as left button dropping items in the item frame<br>
     * The trigger point is in the onBlockBreakStart of {@link Player}
     *
     * @param player the player
     * @param action the action
     * @return 状态值，返回值不为0代表这是一个touch操作而不是一个挖掘方块的操作<br>Status value, if the return value is not 0, it means that this is a touch operation rather than a mining block operation
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int onTouch(@Nullable Player player, PlayerInteractEvent.Action action) {
        onUpdate(Level.BLOCK_UPDATE_TOUCH);
        return 0;
    }

    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    public void onPlayerRightClick(@NotNull Player player, Item item, BlockFace face, Vector3 clickPoint) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void onNeighborChange(@NotNull BlockFace side) {

    }

    public boolean onActivate(@NotNull Item item) {
        return this.onActivate(item, null);
    }

    public boolean onActivate(@NotNull Item item, @Nullable Player player) {
        return false;
    }

    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    public void afterRemoval(Block newBlock, boolean update) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSoulSpeedCompatible() {
        return false;
    }

    /**
     * 控制方块硬度
     *
     * @return 方块的硬度
     */
    public double getHardness() {
        return 10;
    }

    /**
     * 控制方块爆炸抗性
     *
     * @return 方块的爆炸抗性
     */
    public double getResistance() {
        return 1;
    }

    /**
     * 这个值越大，这个方块本身越容易起火
     * 返回-1,这个方块不能被点燃
     * <p>
     * The higher this value, the more likely the block itself is to catch fire
     *
     * @return the burn chance
     */
    public int getBurnChance() {
        return 0;
    }

    /**
     * 这个值越大，越有可能被旁边的火焰引燃
     * <p>
     * The higher this value, the more likely it is to be ignited by the fire next to it
     */
    public int getBurnAbility() {
        return 0;
    }

    /**
     * 控制挖掘方块的工具类型
     *
     * @return 挖掘方块的工具类型
     */
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    public static final double DEFAULT_FRICTION_FACTOR = 0.6;

    /**
     * 服务端侧的摩擦系数，用于控制玩家丢弃物品、实体、船其在上方移动的速度。值越大，移动越快。
     * <p>
     * The friction on the server side, which is used to control the speed that player drops item,entity walk and boat movement on the block.The larger the value, the faster the movement.
     */
    public double getFrictionFactor() {
        return DEFAULT_FRICTION_FACTOR;
    }

    public static final double DEFAULT_AIR_FLUID_FRICTION = 0.95;

    /**
     * 控制方块的通过阻力因素（0-1）。此值越小阻力越大<p/>
     * 对于不可穿过的方块，若未覆写，此值始终为1（无效）<p/>
     */
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public double getPassableBlockFrictionFactor() {
        if (!this.canPassThrough()) return 1;
        return DEFAULT_AIR_FLUID_FRICTION;
    }

    /**
     * 获取走过这个方块所需要的额外代价，通常用于水、浆果丛等难以让实体经过的方块
     *
     * @return 走过这个方块所需要的额外代价
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public int getWalkThroughExtraCost() {
        return 0;
    }

    /**
     * 控制方块的发光等级
     *
     * @return 发光等级(0 - 15)
     */
    public int getLightLevel() {
        return 0;
    }

    public boolean canBePlaced() {
        return true;
    }

    public boolean canBeReplaced() {
        return false;
    }

    /**
     * 控制方块是否透明(默认为false)
     *
     * @return 方块是否透明
     */
    public boolean isTransparent() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    /**
     * Check if blocks can be attached in the given side.
     */
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean isSolid(BlockFace side) {
        return isSideFull(side);
    }

    // https://minecraft.gamepedia.com/Opacity#Lighting
    @PowerNukkitOnly
    public boolean diffusesSkyLight() {
        return false;
    }

    public boolean canBeFlowedInto() {
        return false;
    }

    @PowerNukkitOnly
    public int getWaterloggingLevel() {
        return 0;
    }

    @PowerNukkitOnly
    public final boolean canWaterloggingFlowInto() {
        return canBeFlowedInto() || getWaterloggingLevel() > 1;
    }

    public boolean canBeActivated() {
        return false;
    }

    public boolean hasEntityCollision() {
        return false;
    }

    public boolean canPassThrough() {
        return false;
    }

    /**
     * @return 方块是否可以被活塞推动
     */
    public boolean canBePushed() {
        return true;
    }

    /**
     * @return 方块是否可以被活塞拉动
     */
    @PowerNukkitOnly
    public boolean canBePulled() {
        return true;
    }

    /**
     * @return 当被活塞移动时是否会被破坏
     */
    @PowerNukkitOnly
    public boolean breaksWhenMoved() {
        return false;
    }

    /**
     * @return 是否可以粘在粘性活塞上
     */
    @PowerNukkitOnly
    public boolean sticksToPiston() {
        return true;
    }

    /**
     * @return 被活塞移动的时候是否可以粘住其他方块。eg:粘液块，蜂蜜块
     */
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public boolean canSticksBlock() {
        return false;
    }

    public boolean hasComparatorInputOverride() {
        return false;
    }

    public int getComparatorInputOverride() {
        return 0;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean canHarvest(Item item) {
        return getToolTier() == 0 || getToolType() == 0 || correctTool0(getToolType(), item, getId()) && item.getTier() >= getToolTier();
    }


    /**
     * 控制挖掘方块的最低工具级别(木质、石质...)
     *
     * @return 挖掘方块的最低工具级别
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getToolTier() {
        return 0;
    }

    public boolean canBeClimbed() {
        return false;
    }

    protected static final Map<Long, BlockColor> VANILLA_BLOCK_COLOR_MAP = new Long2ObjectOpenHashMap<>();

    static {
        try (var reader = new InputStreamReader(new BufferedInputStream(Objects.requireNonNull(Block.class.getClassLoader().getResourceAsStream("block_color.json"))))) {
            var parser = JsonParser.parseReader(reader);
            for (var entry : parser.getAsJsonObject().entrySet()) {
                var r = entry.getValue().getAsJsonObject().get("r").getAsInt();
                var g = entry.getValue().getAsJsonObject().get("g").getAsInt();
                var b = entry.getValue().getAsJsonObject().get("b").getAsInt();
                var a = entry.getValue().getAsJsonObject().get("a").getAsInt();
                VANILLA_BLOCK_COLOR_MAP.put(Long.parseLong(entry.getKey()), new BlockColor(r, g, b, a));
            }
        } catch (IOException e) {
            log.error("Failed to load block color map", e);
        }
    }

    protected BlockColor color;

    public BlockColor getColor() {
        if (color != null) return color;
        else color = VANILLA_BLOCK_COLOR_MAP.get(computeUnsignedBlockStateHash());
        if (color == null) {
            log.error("Failed to get color of block " + getName());
            log.error("Current block state hash: " + computeUnsignedBlockStateHash());
            color = BlockColor.VOID_BLOCK_COLOR;
        }
        return color;
    }

    public abstract String getName();

    public abstract int getId();

    @PowerNukkitOnly
    public int getItemId() {
        int id = getId();
        if (id > 255) {
            return 255 - id;
        } else {
            return id;
        }
    }

    /**
     * The full id is a combination of the id and data.
     *
     * @return full id
     * @deprecated PowerNukkit: The meta is limited to 32 bits
     */
    @Override
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    public int getFullId() {
        return mutableState == null ? 0 : mutableState.getFullId();
    }

    /**
     * The properties that fully describe all possible and valid states that this block can have.
     */
    @Override
    @NotNull
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public final BlockState getCurrentState() {
        return mutableState == null ? BlockState.of(getId()) : mutableState.getCurrentState();
    }

    @Override
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public final int getRuntimeId() {
        return getCurrentState().getRuntimeId();
    }

    public void addVelocityToEntity(Entity entity, Vector3 vector) {

    }

    @Deprecated
    @DeprecationDetails(reason = "Limited to 32 bits", since = "1.4.0.0-PN")
    public int getDamage() {
        return mutableState == null ? 0 : mutableState.getBigDamage();
    }

    @Deprecated
    @DeprecationDetails(reason = "Limited to 32 bits", since = "1.4.0.0-PN")
    public void setDamage(int meta) {
        if (meta == 0 && isDefaultState()) {
            return;
        }
        getMutableState().setDataStorageFromInt(meta);
    }

    @Deprecated
    @DeprecationDetails(reason = "Limited to 32 bits", since = "1.4.0.0-PN")
    public final void setDamage(Integer meta) {
        setDamage((meta == null ? 0 : meta & 0x0f));
    }

    final public void position(Position v) {
        this.x = (int) v.x;
        this.y = (int) v.y;
        this.z = (int) v.z;
        this.level = v.level;
    }

    /**
     * 控制方块被破坏时掉落的物品
     * 常在{@link cn.nukkit.level.Level#useBreakOn(Vector3, int, BlockFace, Item, Player, boolean, boolean)}方法被调用
     *
     * @return 掉落的物品数组
     */
    public Item[] getDrops(Item item) {
        if (this instanceof CustomBlock) {
            return new Item[]{
                    this.toItem()
            };
        } else if (this.getId() < 0 || this.getId() > list.length) { //Unknown blocks
            return Item.EMPTY_ARRAY;
        } else if (canHarvestWithHand() || canHarvest(item)) {
            return new Item[]{
                    this.toItem()
            };
        }
        return Item.EMPTY_ARRAY;
    }

    private double toolBreakTimeBonus0(Item item) {
        if (item instanceof ItemCustomTool itemCustomTool && itemCustomTool.getSpeed() != null) {
            return customToolBreakTimeBonus(customToolType(item), itemCustomTool.getSpeed());
        } else return toolBreakTimeBonus0(toolType0(item, getId()), item.getTier(), getId());
    }

    private double customToolBreakTimeBonus(int toolType, @Nullable Integer speed) {
        if (speed != null) return speed;
        else if (toolType == ItemTool.TYPE_SWORD) {
            if (this instanceof BlockCobweb) {
                return 15.0;
            } else if (this instanceof BlockBamboo) {
                return 30.0;
            } else return 1.0;
        } else if (toolType == ItemTool.TYPE_SHEARS) {
            if (this instanceof BlockWool || this instanceof BlockLeaves) {
                return 5.0;
            } else if (this instanceof BlockCobweb) {
                return 15.0;
            } else return 1.0;
        } else if (toolType == ItemTool.TYPE_NONE) return 1.0;
        return 0;
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    private int customToolType(Item item) {
        if (this instanceof BlockLeaves && item.isHoe()) return ItemTool.TYPE_SHEARS;
        if (item.isSword()) return ItemTool.TYPE_SWORD;
        if (item.isShovel()) return ItemTool.TYPE_SHOVEL;
        if (item.isPickaxe()) return ItemTool.TYPE_PICKAXE;
        if (item.isAxe()) return ItemTool.TYPE_AXE;
        if (item.isHoe()) return ItemTool.TYPE_HOE;
        if (item.isShears()) return ItemTool.TYPE_SHEARS;
        return ItemTool.TYPE_NONE;
    }


    private static double toolBreakTimeBonus0(int toolType, int toolTier, int blockId) {
        if (toolType == ItemTool.TYPE_SWORD) {
            if (blockId == BlockID.COBWEB) {
                return 15.0;
            }
            if (blockId == BlockID.BAMBOO) {
                return 30.0;
            }
            return 1.0;
        }
        if (toolType == ItemTool.TYPE_SHEARS) {
            if (blockId == Block.WOOL || blockId == LEAVES || blockId == LEAVES2) {
                return 5.0;
            } else if (blockId == COBWEB) {
                return 15.0;
            }
            return 1.0;
        }
        if (toolType == ItemTool.TYPE_NONE) return 1.0;
        switch (toolTier) {
            case ItemTool.TIER_WOODEN:
                return 2.0;
            case ItemTool.TIER_STONE:
                return 4.0;
            case ItemTool.TIER_IRON:
                return 6.0;
            case ItemTool.TIER_DIAMOND:
                return 8.0;
            case ItemTool.TIER_NETHERITE:
                return 9.0;
            case ItemTool.TIER_GOLD:
                return 12.0;
            default:
                if (toolTier == ItemTool.TIER_NETHERITE) {
                    return 9.0;
                }
                return 1.0;
        }
    }

    private static double speedBonusByEfficiencyLore0(int efficiencyLoreLevel) {
        if (efficiencyLoreLevel == 0) return 0;
        return efficiencyLoreLevel * efficiencyLoreLevel + 1;
    }

    private static double speedRateByHasteLore0(int hasteLoreLevel) {
        return 1.0 + (0.2 * hasteLoreLevel);
    }

    @PowerNukkitDifference(info = "Special condition for the leaves", since = "1.4.0.0-PN")
    private static int toolType0(Item item, int blockId) {
        if ((blockId == LEAVES && item.isHoe()) || (blockId == LEAVES2 && item.isHoe())) return ItemTool.TYPE_SHEARS;
        if (item.isSword()) return ItemTool.TYPE_SWORD;
        if (item.isShovel()) return ItemTool.TYPE_SHOVEL;
        if (item.isPickaxe()) return ItemTool.TYPE_PICKAXE;
        if (item.isAxe()) return ItemTool.TYPE_AXE;
        if (item.isHoe()) return ItemTool.TYPE_HOE;
        if (item.isShears()) return ItemTool.TYPE_SHEARS;
        return ItemTool.TYPE_NONE;
    }

    @PowerNukkitDifference(info = "Special condition for the leaves and cobweb", since = "1.4.0.0-PN")
    private static boolean correctTool0(int blockToolType, Item item, int blockId) {
        if ((blockId == LEAVES && item.isHoe()) ||
                (blockId == LEAVES2 && item.isHoe())) {
            return (blockToolType == ItemTool.TYPE_SHEARS && item.isHoe());
        } else if (blockId == BAMBOO && item.isSword()) {
            return (blockToolType == ItemTool.TYPE_AXE && item.isSword());
        } else return (blockToolType == ItemTool.TYPE_SWORD && item.isSword()) ||
                (blockToolType == ItemTool.TYPE_SHOVEL && item.isShovel()) ||
                (blockToolType == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                (blockToolType == ItemTool.TYPE_AXE && item.isAxe()) ||
                (blockToolType == ItemTool.TYPE_HOE && item.isHoe()) ||
                (blockToolType == ItemTool.TYPE_SHEARS && item.isShears()) ||
                blockToolType == ItemTool.TYPE_NONE ||
                (blockId == COBWEB && item.isShears());
    }

    //http://minecraft.gamepedia.com/Breaking
    private static double breakTime0(double blockHardness, boolean correctTool, boolean canHarvestWithHand,
                                     int blockId, int toolType, int toolTier, int efficiencyLoreLevel, int hasteEffectLevel,
                                     boolean insideOfWaterWithoutAquaAffinity, boolean outOfWaterButNotOnGround) {
        double baseTime = ((correctTool || canHarvestWithHand) ? 1.5 : 5.0) * blockHardness;
        double speed = 1.0 / baseTime;
        if (correctTool) speed *= toolBreakTimeBonus0(toolType, toolTier, blockId);
        speed += correctTool ? speedBonusByEfficiencyLore0(efficiencyLoreLevel) : 0;
        speed *= speedRateByHasteLore0(hasteEffectLevel);
        if (insideOfWaterWithoutAquaAffinity) speed *= 0.2;
        if (outOfWaterButNotOnGround) speed *= 0.2;
        return 1.0 / speed;
    }

    public double getBreakTime(Item item, Player player) {
        return this.calculateBreakTime(item, player);
    }

    public double getBreakTime(Item item) {
        return this.calculateBreakTime(item);
    }


    /**
     * @link calculateBreakTime(@ Nonnull Item item, @ Nullable Player player)
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public double calculateBreakTime(@NotNull Item item) {
        return calculateBreakTime(item, null);
    }

    /**
     * 计算方块挖掘时间
     *
     * @param item   挖掘该方块的物品
     * @param player 挖掘该方块的玩家
     * @return 方块的挖掘时间
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public double calculateBreakTime(@NotNull Item item, @Nullable Player player) {
        double seconds = this.calculateBreakTimeNotInAir(item, player);

        if (player != null) {
            //玩家距离上次在空中过去5tick之后，才认为玩家是在地上挖掘。
            //如果单纯用onGround检测，这个方法返回的时间将会不连续。
            if (player.getServer().getTick() - player.getLastInAirTick() < 5) {
                seconds *= 5;
            }
        }
        return seconds;
    }

    /**
     * 忽略玩家在空中时，计算方块的挖掘时间
     *
     * @param item   挖掘该方块的物品
     * @param player 挖掘该方块的玩家
     * @return 方块的挖掘时间
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public double calculateBreakTimeNotInAir(@NotNull Item item, @Nullable Player player) {
        double seconds = 0;
        double blockHardness = getHardness();
        boolean canHarvest = canHarvest(item);

        if (canHarvest) {
            seconds = blockHardness * 1.5;
        } else {
            seconds = blockHardness * 5;
        }

        double speedMultiplier = 1;
        boolean hasConduitPower = false;
        boolean hasAquaAffinity = false;
        int hasteEffectLevel = 0;
        int miningFatigueLevel = 0;

        if (player != null) {
            hasConduitPower = player.hasEffect(Effect.CONDUIT_POWER);
            hasAquaAffinity = Optional.ofNullable(player.getInventory().getHelmet().getEnchantment(Enchantment.ID_WATER_WORKER))
                    .map(Enchantment::getLevel).map(l -> l >= 1).orElse(false);
            hasteEffectLevel = Optional.ofNullable(player.getEffect(Effect.HASTE))
                    .map(Effect::getAmplifier).orElse(0);
            miningFatigueLevel = Optional.ofNullable(player.getEffect(Effect.MINING_FATIGUE))
                    .map(Effect::getAmplifier).orElse(0);
        }

        if (correctTool0(getToolType(), item, getId())) {
            speedMultiplier = toolBreakTimeBonus0(item);

            int efficiencyLevel = Optional.ofNullable(item.getEnchantment(Enchantment.ID_EFFICIENCY))
                    .map(Enchantment::getLevel).orElse(0);

            if (canHarvest && efficiencyLevel > 0) {
                speedMultiplier += efficiencyLevel ^ 2 + 1;
            }

            if (hasConduitPower) hasteEffectLevel = Integer.max(hasteEffectLevel, 2);

            if (hasteEffectLevel > 0) {
                speedMultiplier *= 1 + (0.2 * hasteEffectLevel);
            }

        }

        if (miningFatigueLevel > 0) {
            speedMultiplier /= 3 ^ miningFatigueLevel;
        }

        seconds /= speedMultiplier;

        if (player != null) {
            if (player.isInsideOfWater() && !hasAquaAffinity) {
                seconds *= hasConduitPower && blockHardness >= 0.5 ? 2.5 : 5;
            }
        }
        return seconds;
    }

    /**
     * 计算方块挖掘需要多少tick (计算算法来自https://minecraft.fandom.com/wiki/Breaking)
     *
     * @param item   挖掘工具
     * @param player 挖掘方块的玩家
     * @return 挖掘耗费的tick
     */
    @PowerNukkitXOnly
    public double calculateBreakTick(@NotNull Item item, @Nullable Player player) {
        double blockHardness = getHardness();
        boolean canHarvest = canHarvest(item);
        double speedMultiplier = 1;
        boolean hasConduitPower = false;
        boolean hasAquaAffinity = false;
        int hasteEffectLevel = 0;
        int miningFatigueLevel = 0;

        if (player != null) {
            hasConduitPower = player.hasEffect(Effect.CONDUIT_POWER);
            hasAquaAffinity = Optional.ofNullable(player.getInventory().getHelmet().getEnchantment(Enchantment.ID_WATER_WORKER))
                    .map(Enchantment::getLevel).map(l -> l >= 1).orElse(false);
            hasteEffectLevel = Optional.ofNullable(player.getEffect(Effect.HASTE))
                    .map(Effect::getAmplifier).orElse(0);
            miningFatigueLevel = Optional.ofNullable(player.getEffect(Effect.MINING_FATIGUE))
                    .map(Effect::getAmplifier).orElse(0);
        }

        if (correctTool0(getToolType(), item, getId())) {
            speedMultiplier = toolBreakTimeBonus0(item);

            int efficiencyLevel = Optional.ofNullable(item.getEnchantment(Enchantment.ID_EFFICIENCY))
                    .map(Enchantment::getLevel).orElse(0);

            if (canHarvest && efficiencyLevel > 0) {
                speedMultiplier += efficiencyLevel ^ 2 + 1;
            }

            if (hasConduitPower) hasteEffectLevel = Integer.max(hasteEffectLevel, 2);

            if (hasteEffectLevel > 0) {
                speedMultiplier *= 1 + (0.2 * hasteEffectLevel);
            }

        }

        if (miningFatigueLevel > 0) {
            speedMultiplier /= 3 ^ miningFatigueLevel;
        }

        if (player != null) {
            if (player.isInsideOfWater() && !hasAquaAffinity) {
                speedMultiplier /= hasConduitPower && blockHardness >= 0.5 ? 2.5 : 5;
            }

            if (player.getServer().getTick() - player.getLastInAirTick() < 5) {
                speedMultiplier /= 5;
            }
        }

        double damage = 0;

        damage = speedMultiplier / blockHardness;

        if (canHarvest) {
            damage /= 30;
        } else {
            damage /= 100;
        }

        if (damage > 1) {
            return 0;
        }

        return Math.ceil(1 / damage);
    }

    public boolean canBeBrokenWith(Item item) {
        return this.getHardness() != -1;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public Block getTickCachedSide(BlockFace face) {
        return getTickCachedSideAtLayer(layer, face);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public Block getTickCachedSide(BlockFace face, int step) {
        return getTickCachedSideAtLayer(layer, face, step);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public Block getTickCachedSideAtLayer(int layer, BlockFace face) {
        if (this.isValid()) {
            return this.getLevel().getTickCachedBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset(), layer);
        }
        return this.getTickCachedSide(face, 1);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public Block getTickCachedSideAtLayer(int layer, BlockFace face, int step) {
        if (this.isValid()) {
            if (step == 1) {
                return this.getLevel().getTickCachedBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset(), layer);
            } else {
                return this.getLevel().getTickCachedBlock((int) x + face.getXOffset() * step, (int) y + face.getYOffset() * step, (int) z + face.getZOffset() * step, layer);
            }
        }
        Block block = Block.get(Item.AIR, 0);
        block.x = (int) x + face.getXOffset() * step;
        block.y = (int) y + face.getYOffset() * step;
        block.z = (int) z + face.getZOffset() * step;
        block.layer = layer;
        return block;
    }

    @Override
    public Block getSide(BlockFace face) {
        return getSideAtLayer(layer, face);
    }

    @Override
    public Block getSide(BlockFace face, int step) {
        return getSideAtLayer(layer, face, step);
    }

    @PowerNukkitOnly
    public Block getSideAtLayer(int layer, BlockFace face) {
        if (this.isValid()) {
            return this.getLevel().getBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset(), layer);
        }
        return this.getSide(face, 1);
    }

    @PowerNukkitOnly
    public Block getSideAtLayer(int layer, BlockFace face, int step) {
        if (this.isValid()) {
            if (step == 1) {
                return this.getLevel().getBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset(), layer);
            } else {
                return this.getLevel().getBlock((int) x + face.getXOffset() * step, (int) y + face.getYOffset() * step, (int) z + face.getZOffset() * step, layer);
            }
        }
        Block block = Block.get(Item.AIR, 0);
        block.x = (int) x + face.getXOffset() * step;
        block.y = (int) y + face.getYOffset() * step;
        block.z = (int) z + face.getZOffset() * step;
        block.layer = layer;
        return block;
    }

    @Override
    public Block up() {
        return up(1);
    }

    @Override
    public Block up(int step) {
        return getSide(BlockFace.UP, step);
    }

    @PowerNukkitOnly
    public Block up(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.UP, step);
    }

    @Override
    public Block down() {
        return down(1);
    }

    @Override
    public Block down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    @PowerNukkitOnly
    public Block down(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.DOWN, step);
    }

    @Override
    public Block north() {
        return north(1);
    }

    @Override
    public Block north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    @PowerNukkitOnly
    public Block north(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.NORTH, step);
    }

    @Override
    public Block south() {
        return south(1);
    }

    @Override
    public Block south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    @PowerNukkitOnly
    public Block south(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.SOUTH, step);
    }

    @Override
    public Block east() {
        return east(1);
    }

    @Override
    public Block east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    @PowerNukkitOnly
    public Block east(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.EAST, step);
    }

    @Override
    public Block west() {
        return west(1);
    }

    @Override
    public Block west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    @PowerNukkitOnly
    public Block west(int step, int layer) {
        return getSideAtLayer(layer, BlockFace.WEST, step);
    }

    @Override
    public String toString() {
        return "Block[" + this.getName() + "] (" + this.getId() + ":" + (mutableState != null ? mutableState.getDataStorage() : "0") + ")" +
                (isValid() ? " at " + super.toString() : "");
    }

    public boolean collidesWithBB(AxisAlignedBB bb) {
        return collidesWithBB(bb, false);
    }

    public boolean collidesWithBB(AxisAlignedBB bb, boolean collisionBB) {
        AxisAlignedBB bb1 = collisionBB ? this.getCollisionBoundingBox() : this.getBoundingBox();
        return bb1 != null && bb.intersectsWith(bb1);
    }

    public void onEntityCollide(Entity entity) {

    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void onEntityFallOn(Entity entity, float fallDistance) {

    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public boolean useDefaultFallDamage() {
        return true;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.recalculateBoundingBox();
    }

    public AxisAlignedBB getCollisionBoundingBox() {
        return this.recalculateCollisionBoundingBox();
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x;
    }

    @Override
    public double getMinY() {
        return this.y;
    }

    @Override
    public double getMinZ() {
        return this.z;
    }

    @Override
    public double getMaxX() {
        return this.x + 1;
    }

    @Override
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    public double getMaxZ() {
        return this.z + 1;
    }

    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return getBoundingBox();
    }

    @Override
    public MovingObjectPosition calculateIntercept(Vector3 pos1, Vector3 pos2) {
        AxisAlignedBB bb = this.getBoundingBox();
        if (bb == null) {
            return null;
        }

        Vector3 v1 = pos1.getIntermediateWithXValue(pos2, bb.getMinX());
        Vector3 v2 = pos1.getIntermediateWithXValue(pos2, bb.getMaxX());
        Vector3 v3 = pos1.getIntermediateWithYValue(pos2, bb.getMinY());
        Vector3 v4 = pos1.getIntermediateWithYValue(pos2, bb.getMaxY());
        Vector3 v5 = pos1.getIntermediateWithZValue(pos2, bb.getMinZ());
        Vector3 v6 = pos1.getIntermediateWithZValue(pos2, bb.getMaxZ());

        if (v1 != null && !bb.isVectorInYZ(v1)) {
            v1 = null;
        }

        if (v2 != null && !bb.isVectorInYZ(v2)) {
            v2 = null;
        }

        if (v3 != null && !bb.isVectorInXZ(v3)) {
            v3 = null;
        }

        if (v4 != null && !bb.isVectorInXZ(v4)) {
            v4 = null;
        }

        if (v5 != null && !bb.isVectorInXY(v5)) {
            v5 = null;
        }

        if (v6 != null && !bb.isVectorInXY(v6)) {
            v6 = null;
        }

        Vector3 vector = v1;

        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2;
        }

        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3;
        }

        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4;
        }

        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5;
        }

        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6;
        }

        if (vector == null) {
            return null;
        }

        BlockFace f = null;

        if (vector == v1) {
            f = BlockFace.WEST;
        } else if (vector == v2) {
            f = BlockFace.EAST;
        } else if (vector == v3) {
            f = BlockFace.DOWN;
        } else if (vector == v4) {
            f = BlockFace.UP;
        } else if (vector == v5) {
            f = BlockFace.NORTH;
        } else if (vector == v6) {
            f = BlockFace.SOUTH;
        }

        return MovingObjectPosition.fromBlock((int) this.x, (int) this.y, (int) this.z, f, vector.add(this.x, this.y, this.z));
    }

    public String getSaveId() {
        String name = getClass().getName();
        return name.substring(16);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
        }
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) throws Exception {
        if (this.getLevel() != null) {
            return this.getLevel().getBlockMetadata().getMetadata(this, metadataKey);

        }
        return null;
    }

    @Override
    public boolean hasMetadata(String metadataKey) throws Exception {
        return this.getLevel() != null && this.getLevel().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
        }
    }

    @Override
    public Block clone() {
        Block clone = (Block) super.clone();
        clone.mutableState = mutableState != null ? mutableState.copy() : null;
        return clone;
    }

    public int getWeakPower(BlockFace face) {
        return 0;
    }

    public int getStrongPower(BlockFace side) {
        return 0;
    }

    public boolean isPowerSource() {
        return false;
    }

    public String getLocationHash() {
        return this.getFloorX() + ":" + this.getFloorY() + ":" + this.getFloorZ();
    }

    public int getDropExp() {
        return 0;
    }

    /**
     * Check if the block is not transparent, is solid and can't provide redstone power.
     */
    public boolean isNormalBlock() {
        return !isTransparent() && isSolid() && !isPowerSource();
    }

    /**
     * Check if the block is not transparent, is solid and is a full cube like a stone block.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSimpleBlock() {
        return !isTransparent() && isSolid() && isFullBlock();
    }

    /**
     * Check if the given face is fully occupied by the block bounding box.
     *
     * @param face The face to be checked
     * @return If and only if the bounding box completely cover the face
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSideFull(BlockFace face) {
        AxisAlignedBB boundingBox = getBoundingBox();
        if (boundingBox == null) {
            return false;
        }

        if (face.getAxis().getPlane() == BlockFace.Plane.HORIZONTAL) {
            if (boundingBox.getMinY() != getY() || boundingBox.getMaxY() != getY() + 1) {
                return false;
            }
            int offset = face.getXOffset();
            if (offset < 0) {
                return boundingBox.getMinX() == getX()
                        && boundingBox.getMinZ() == getZ() && boundingBox.getMaxZ() == getZ() + 1;
            } else if (offset > 0) {
                return boundingBox.getMaxX() == getX() + 1
                        && boundingBox.getMaxZ() == getZ() + 1 && boundingBox.getMinZ() == getZ();
            }

            offset = face.getZOffset();
            if (offset < 0) {
                return boundingBox.getMinZ() == getZ()
                        && boundingBox.getMinX() == getX() && boundingBox.getMaxX() == getX() + 1;
            }

            return boundingBox.getMaxZ() == getZ() + 1
                    && boundingBox.getMaxX() == getX() + 1 && boundingBox.getMinX() == getX();
        }

        if (boundingBox.getMinX() != getX() || boundingBox.getMaxX() != getX() + 1 ||
                boundingBox.getMinZ() != getZ() || boundingBox.getMaxZ() != getZ() + 1) {
            return false;
        }

        if (face.getYOffset() < 0) {
            return boundingBox.getMinY() == getY();
        }

        return boundingBox.getMaxY() == getY() + 1;
    }

    /**
     * Check if the block occupies the entire block space, like a stone and normal glass blocks
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isFullBlock() {
        AxisAlignedBB boundingBox = getBoundingBox();
        if (boundingBox == null) {
            return false;
        }
        return boundingBox.getMinX() == getX() && boundingBox.getMaxX() == getX() + 1
                && boundingBox.getMinY() == getY() && boundingBox.getMaxY() == getY() + 1
                && boundingBox.getMinZ() == getZ() && boundingBox.getMaxZ() == getZ() + 1;
    }

    public static boolean equals(Block b1, Block b2) {
        return equals(b1, b2, true);
    }

    public static boolean equals(Block b1, Block b2, boolean checkDamage) {
        if (b1 == null || b2 == null || b1.getId() != b2.getId()) {
            return false;
        }
        if (checkDamage) {
            boolean b1Default = b1.isDefaultState();
            boolean b2Default = b2.isDefaultState();
            if (b1Default != b2Default) {
                return false;
            } else if (b1Default) { // both are default
                return true;
            } else {
                return b1.getMutableState().equals(b2.getMutableState());
            }
        } else {
            return true;
        }
    }

    public Item toItem() {
        return asItemBlock(1);
    }

    /**
     * If the block, when in item form, is resistant to lava and fire and can float on lava like if it was on water.
     *
     * @since 1.4.0.0-PN
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isLavaResistant() {
        return false;
    }

    @NotNull
    @Override
    @PowerNukkitOnly
    public final ItemBlock asItemBlock() {
        return asItemBlock(1);
    }

    public boolean canSilkTouch() {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.2.1.0-PN")
    public boolean mustSilkTouch(Vector3 vector, int layer, BlockFace face, Item item, Player player) {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.2.1.0-PN")
    public boolean mustDrop(Vector3 vector, int layer, BlockFace face, Item item, Player player) {
        return false;
    }

    @PowerNukkitOnly
    public Optional<Block> firstInLayers(Predicate<Block> condition) {
        return firstInLayers(0, condition);
    }

    @PowerNukkitOnly
    public Optional<Block> firstInLayers(int startingLayer, Predicate<Block> condition) {
        int maximumLayer = this.level.requireProvider().getMaximumLayer();
        for (int layer = startingLayer; layer <= maximumLayer; layer++) {
            Block block = this.getLevelBlockAtLayer(layer);
            if (condition.test(block)) {
                return Optional.of(block);
            }
        }

        return Optional.empty();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setState(@NotNull IBlockState state) throws InvalidBlockStateException {
        if (state.getBlockId() == getId() && this.isDefaultState() && state.isDefaultState()) {
            return;
        }
        getMutableState().setState(state);
    }

    @Since("1.5.1.0-PN")
    @PowerNukkitOnly
    @Override
    @NotNull
    public Block forState(@NotNull IBlockState state) throws InvalidBlockStateException {
        return (Block) IMutableBlockState.super.forState(state);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setDataStorage(@Nonnegative @NotNull Number storage) {
        if (NukkitMath.isZero(storage) && isDefaultState()) {
            return;
        }
        getMutableState().setDataStorage(storage);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setDataStorageFromInt(@Nonnegative int storage) {
        if (storage == 0 && isDefaultState()) {
            return;
        }
        getMutableState().setDataStorageFromInt(storage);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setDataStorage(@Nonnegative @NotNull Number storage, boolean repair, Consumer<BlockStateRepair> callback) {
        if (NukkitMath.isZero(storage) && isDefaultState()) {
            return false;
        }
        return getMutableState().setDataStorage(storage, repair, callback);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean setDataStorageFromInt(@Nonnegative int storage, boolean repair, Consumer<BlockStateRepair> callback) {
        if (storage == 0 && isDefaultState()) {
            return false;
        }
        return getMutableState().setDataStorageFromInt(storage, repair, callback);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setPropertyValue(@NotNull String propertyName, @Nullable Serializable value) {
        if (isDefaultState() && getProperties().isDefaultValue(propertyName, value)) {
            return;
        }
        getMutableState().setPropertyValue(propertyName, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setBooleanValue(@NotNull String propertyName, boolean value) {
        if (isDefaultState() && getProperties().isDefaultBooleanValue(propertyName, value)) {
            return;
        }
        getMutableState().setBooleanValue(propertyName, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setIntValue(@NotNull String propertyName, int value) {
        if (isDefaultState() && getProperties().isDefaultIntValue(propertyName, value)) {
            return;
        }
        getMutableState().setIntValue(propertyName, value);
    }

    @Nonnegative
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    @Deprecated
    @DeprecationDetails(reason = "Does the same as getId() but the other is compatible with NukkitX and this is not", since = "1.4.0.0-PN")
    public final int getBlockId() {
        return getId();
    }

    @Nonnegative
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public final Number getDataStorage() {
        return mutableState == null ? 0 : mutableState.getDataStorage();
    }

    @Nonnegative
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getLegacyDamage() {
        return mutableState == null ? 0 : mutableState.getLegacyDamage();
    }

    @Unsigned
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getBigDamage() {
        return mutableState == null ? 0 : mutableState.getBigDamage();
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getSignedBigDamage() {
        return mutableState == null ? 0 : mutableState.getSignedBigDamage();
    }

    @Nonnegative
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public BigInteger getHugeDamage() {
        return mutableState == null ? BigInteger.ZERO : mutableState.getHugeDamage();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public Serializable getPropertyValue(@NotNull String propertyName) {
        if (isDefaultState()) {
            return getProperties().getBlockProperty(propertyName).getDefaultValue();
        }
        return getMutableState().getPropertyValue(propertyName);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public int getIntValue(@NotNull String propertyName) {
        if (isDefaultState()) {
            return getProperties().getBlockProperty(propertyName).getDefaultIntValue();
        }
        return getMutableState().getIntValue(propertyName);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public boolean getBooleanValue(@NotNull String propertyName) {
        if (isDefaultState()) {
            return getProperties().getBlockProperty(propertyName).getDefaultBooleanValue();
        }
        return getMutableState().getBooleanValue(propertyName);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public String getPersistenceValue(@NotNull String propertyName) {
        if (isDefaultState()) {
            return getProperties().getBlockProperty(propertyName).getPersistenceValueForMeta(0);
        }
        return getMutableState().getPersistenceValue(propertyName);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public final int getExactIntStorage() {
        return mutableState == null ? 0 : mutableState.getExactIntStorage();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isBreakable(@Nonnull Vector3 vector, int layer, @Nonnull BlockFace face, @Nonnull Item item, @Nonnull Player player, boolean setBlockDestroy) {
        return true;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final boolean isBlockChangeAllowed() {
        return getChunk().isBlockChangeAllowed(getFloorX() & 0xF, getFloorY(), getFloorZ() & 0xF);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final boolean isBlockChangeAllowed(@Nullable Player player) {
        if (isBlockChangeAllowed()) {
            return true;
        }
        return player != null && player.isCreative() && player.isOp();
    }

    /**
     * 控制方块吸收的光亮
     *
     * @return 方块吸收的光亮
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getLightFilter() {
        return isSolid() && !isTransparent() ? 15 : 1;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final boolean canRandomTick() {
        return Level.canRandomTick(getId());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        return false;
    }

    @PowerNukkitOnly
    @NotNull
    @Override
    public final Block getBlock() {
        return clone();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultState() {
        return mutableState == null || mutableState.isDefaultState();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getItemMaxStackSize() {
        return 64;
    }

    /**
     * Check if a block is getting powered threw a block or directly.
     *
     * @return if the gets powered.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @PowerNukkitDifference(info = "Used so often, why not create own method here?", since = "1.4.0.0-PN")
    public boolean isGettingPower() {
        if (!this.level.getServer().isRedstoneEnabled()) return false;

        for (BlockFace side : BlockFace.values()) {
            Block b = this.getSide(side).getLevelBlock();

            if (this.level.isSidePowered(b.getLocation(), side)) {
                return true;
            }
        }
        return this.level.isBlockPowered(this.getLocation());
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public boolean cloneTo(Position pos) {
        return cloneTo(pos, true);
    }

    /**
     * 将方块克隆到指定位置<p/>
     * 此方法会连带克隆方块实体<p/>
     * 注意，此方法会先清除指定位置的方块为空气再进行克隆
     *
     * @param pos    要克隆到的位置
     * @param update 是否需要更新克隆的方块
     * @return 是否克隆成功
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public boolean cloneTo(Position pos, boolean update) {
        //清除旧方块
        level.setBlock(pos, this.layer, Block.get(Block.AIR), false, false);
        if (this instanceof BlockEntityHolder<?> holder && holder.getBlockEntity() != null) {
            var clonedBlock = this.clone();
            clonedBlock.position(pos);
            CompoundTag tag = holder.getBlockEntity().getCleanedNBT();
            //方块实体要求direct=true
            return BlockEntityHolder.setBlockAndCreateEntity((BlockEntityHolder<?>) clonedBlock, true, update, tag) != null;
        } else {
            return pos.level.setBlock(pos, this.layer, this.clone(), true, update);
        }
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public boolean equalsBlock(Object obj) {
        if (obj instanceof Block otherBlock) {
            if (!(this instanceof BlockEntityHolder<?>) && !(otherBlock instanceof BlockEntityHolder<?>)) {
                return this.getId() == otherBlock.getId() && this.getDamage() == otherBlock.getDamage();
            }
            if (this instanceof BlockEntityHolder<?> holder1 && otherBlock instanceof BlockEntityHolder<?> holder2) {
                BlockEntity be1 = holder1.getOrCreateBlockEntity();
                BlockEntity be2 = holder2.getOrCreateBlockEntity();
                return this.getId() == otherBlock.getId() && this.getDamage() == otherBlock.getDamage() && be1.getCleanedNBT().equals(be2.getCleanedNBT());
            }
        }
        return false;
    }

    private int cachedBlockStateHash = -1;

    @PowerNukkitXOnly
    @Since("1.19.80-r3")
    @SneakyThrows
    public int computeBlockStateHash() {
        if (cachedBlockStateHash != -1) {
            return cachedBlockStateHash;
        } else {
            if (getPersistenceName().equals("minecraft:unknown")) {
                return cachedBlockStateHash = -2; // This is special case
            }
            var tag = NBTIO.putBlockHelper(this, "").remove("version");
            return cachedBlockStateHash = MinecraftNamespaceComparator.fnv1a_32(NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN));
        }
    }

    @Override
    public int hashCode() {
        return ((int) x ^ ((int) z << 12)) ^ ((int) (y + 64) << 23);
    }


    @PowerNukkitXOnly
    @Since("1.19.80-r3")
    @SneakyThrows
    public long computeUnsignedBlockStateHash() {
        return Integer.toUnsignedLong(computeBlockStateHash());
    }
}
