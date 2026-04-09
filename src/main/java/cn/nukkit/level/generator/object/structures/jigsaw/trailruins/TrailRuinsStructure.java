package cn.nukkit.level.generator.object.structures.jigsaw.trailruins;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBrushable;
import cn.nukkit.block.BlockCoarseDirt;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGravel;
import cn.nukkit.block.BlockSuspiciousGravel;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockentity.BlockEntityBrushable;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Location;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RuledObjectGenerator;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.JigsawStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.RandomSourceProvider;
import cn.nukkit.utils.random.Xoroshiro128;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.nukkit.block.BlockAir.STATE;
import static cn.nukkit.block.BlockID.BLUE_STAINED_GLASS_PANE;
import static cn.nukkit.block.BlockID.BROWN_CANDLE;
import static cn.nukkit.block.BlockID.DEADBUSH;
import static cn.nukkit.block.BlockID.FLOWER_POT;
import static cn.nukkit.block.BlockID.GREEN_CANDLE;
import static cn.nukkit.block.BlockID.LIGHT_BLUE_STAINED_GLASS_PANE;
import static cn.nukkit.block.BlockID.MAGENTA_STAINED_GLASS_PANE;
import static cn.nukkit.block.BlockID.OAK_HANGING_SIGN;
import static cn.nukkit.block.BlockID.PINK_STAINED_GLASS_PANE;
import static cn.nukkit.block.BlockID.PURPLE_CANDLE;
import static cn.nukkit.block.BlockID.PURPLE_STAINED_GLASS_PANE;
import static cn.nukkit.block.BlockID.RED_CANDLE;
import static cn.nukkit.block.BlockID.RED_STAINED_GLASS_PANE;
import static cn.nukkit.block.BlockID.SPRUCE_HANGING_SIGN;
import static cn.nukkit.block.BlockID.WHEAT;
import static cn.nukkit.block.BlockID.YELLOW_STAINED_GLASS_PANE;

/**
 * Trail Ruins for PowerNukkitX
 * @author Buddelbubi
 * @since 2026/03/31
 */
public class TrailRuinsStructure extends JigsawStructure implements RuledObjectGenerator {

    private static final StructurePoolCollection COLLECTION;
    private final Map<BlockVector3, TrailRuinsLoot> pendingBrushLoot = new HashMap<>();

    @Override
    public StructurePoolCollection getStructurePoolCollection() {
        return COLLECTION;
    }

    @Override
    public String getEntryPool() {
        return "trail_ruins/tower";
    }

    @Override
    protected int getMaxDepth() {
        return 16;
    }

    @Override
    protected void postProcessStructure(StructureHelper helper) {
        List<Block> placedBlocks = new ArrayList<>(helper.getBlocks());
        Dimension level = helper.getLevel();
        helper.addHook(() -> populatePendingBrushLoot(level));

        helper.applySubChunkUpdate();

        placedBlocks.stream()
                .filter(BlockUnknown.class::isInstance)
                .forEach(block -> level.setBlock(block, STATE.toBlock(block), true, true));
    }

    @Override
    protected void postProcessStructurePiece(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        List<Block> gravelBlocks = new ArrayList<>();
        for (Block block : blockManager.getBlocks()) {
            if (block instanceof BlockGravel) {
                gravelBlocks.add(block);
            }
        }
        if (gravelBlocks.isEmpty()) {
            return;
        }

        RandomSourceProvider random = createRandom(blockManager.getLevel(), gravelBlocks.getFirst().asBlockVector3());
        boolean towerTopPiece = structureName.contains("/tower/tower_top_");
        boolean roadPiece = structureName.contains("/roads/");

        if (!towerTopPiece) {
            replaceRandomGravel(blockManager, gravelBlocks, random, (int) Math.floor(gravelBlocks.size() * 0.2), BlockDirt.PROPERTIES.getDefaultState(), null);
            int remaining = gravelBlocks.size();
            replaceRandomGravel(blockManager, gravelBlocks, random, (int) Math.floor(remaining * 0.1), BlockCoarseDirt.PROPERTIES.getDefaultState(), null);
        }

        int commonCount = towerTopPiece || roadPiece ? 2 : 6;
        int rareCount = towerTopPiece || roadPiece ? 0 : 3;
        replaceRandomGravel(blockManager, gravelBlocks, random, commonCount, BlockSuspiciousGravel.PROPERTIES.getDefaultState(), TrailRuinsLoot.COMMON);
        replaceRandomGravel(blockManager, gravelBlocks, random, rareCount, BlockSuspiciousGravel.PROPERTIES.getDefaultState(), TrailRuinsLoot.RARE);
    }

    private void replaceRandomGravel(BlockManager blockManager, List<Block> gravelBlocks, RandomSourceProvider random, int count, cn.nukkit.block.BlockState replacement, TrailRuinsLoot loot) {
        for (int i = 0; i < count && !gravelBlocks.isEmpty(); i++) {
            int index = random.nextBoundedInt(gravelBlocks.size() - 1);
            Block gravel = gravelBlocks.remove(index);
            blockManager.setBlockStateAt(gravel.getFloorX(), gravel.getFloorY(), gravel.getFloorZ(), replacement);
            if (loot != null) {
                pendingBrushLoot.put(gravel.asBlockVector3(), loot);
            }
        }
    }

    private void populatePendingBrushLoot(Dimension level) {
        for (Map.Entry<BlockVector3, TrailRuinsLoot> entry : pendingBrushLoot.entrySet()) {
            Block block = level.getBlock(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ());
            if (!(block instanceof BlockBrushable brushable)) {
                continue;
            }
            BlockEntityBrushable blockEntity = brushable.getOrCreateBlockEntity();
            blockEntity.setItem(entry.getValue().roll(createRandom(level, entry.getKey())));
        }
        pendingBrushLoot.clear();
    }

    private RandomSourceProvider createRandom(Dimension level, BlockVector3 pos) {
        long seed = level.getSeed();
        seed ^= 0x9E3779B97F4A7C15L * pos.getX();
        seed ^= 0xC2B2AE3D27D4EB4FL * pos.getY();
        seed ^= 0x165667B19E3779F9L * pos.getZ();
        return RandomSourceProvider.create(seed);
    }

    static {
        COLLECTION = new StructurePoolCollection();

        COLLECTION.put("trail_ruins/tower", pool(
                "trail_ruins/tower",
                entry("trail_ruins/tower/tower_1", 1),
                entry("trail_ruins/tower/tower_2", 1),
                entry("trail_ruins/tower/tower_3", 1),
                entry("trail_ruins/tower/tower_4", 1),
                entry("trail_ruins/tower/tower_5", 1)
        ));

        COLLECTION.put("trail_ruins/tower/tower_top", pool(
                "trail_ruins/tower/tower_top",
                entry("trail_ruins/tower/tower_top_1", 1),
                entry("trail_ruins/tower/tower_top_2", 1),
                entry("trail_ruins/tower/tower_top_3", 1),
                entry("trail_ruins/tower/tower_top_4", 1),
                entry("trail_ruins/tower/tower_top_5", 1)
        ));

        COLLECTION.put("trail_ruins/tower/additions", pool(
                "trail_ruins/tower/additions",
                entry("trail_ruins/tower/hall_1", 1),
                entry("trail_ruins/tower/hall_2", 1),
                entry("trail_ruins/tower/hall_3", 1),
                entry("trail_ruins/tower/hall_4", 1),
                entry("trail_ruins/tower/hall_5", 1),
                entry("trail_ruins/tower/large_hall_1", 1),
                entry("trail_ruins/tower/large_hall_2", 1),
                entry("trail_ruins/tower/large_hall_3", 1),
                entry("trail_ruins/tower/large_hall_4", 1),
                entry("trail_ruins/tower/large_hall_5", 1),
                entry("trail_ruins/tower/one_room_1", 1),
                entry("trail_ruins/tower/one_room_2", 1),
                entry("trail_ruins/tower/one_room_3", 1),
                entry("trail_ruins/tower/one_room_4", 1),
                entry("trail_ruins/tower/one_room_5", 1),
                entry("trail_ruins/tower/platform_1", 1),
                entry("trail_ruins/tower/platform_2", 1),
                entry("trail_ruins/tower/platform_3", 1),
                entry("trail_ruins/tower/platform_4", 1),
                entry("trail_ruins/tower/platform_5", 1),
                entry("trail_ruins/tower/stable_1", 1),
                entry("trail_ruins/tower/stable_2", 1),
                entry("trail_ruins/tower/stable_3", 1),
                entry("trail_ruins/tower/stable_4", 1),
                entry("trail_ruins/tower/stable_5", 1)
        ));

        COLLECTION.put("trail_ruins/roads", pool(
                "trail_ruins/roads",
                entry("trail_ruins/roads/long_road_end", 1),
                entry("trail_ruins/roads/road_end_1", 1),
                entry("trail_ruins/roads/road_section_1", 1),
                entry("trail_ruins/roads/road_section_2", 1),
                entry("trail_ruins/roads/road_section_3", 1),
                entry("trail_ruins/roads/road_section_4", 1),
                entry("trail_ruins/roads/road_spacer_1", 1)
        ));

        COLLECTION.put("trail_ruins/buildings", pool(
                "trail_ruins/buildings",
                entry("trail_ruins/buildings/group_hall_1", 1),
                entry("trail_ruins/buildings/group_hall_2", 1),
                entry("trail_ruins/buildings/group_hall_3", 1),
                entry("trail_ruins/buildings/group_hall_4", 1),
                entry("trail_ruins/buildings/group_hall_5", 1),
                entry("trail_ruins/buildings/large_room_1", 1),
                entry("trail_ruins/buildings/large_room_2", 1),
                entry("trail_ruins/buildings/large_room_3", 1),
                entry("trail_ruins/buildings/large_room_4", 1),
                entry("trail_ruins/buildings/large_room_5", 1),
                entry("trail_ruins/buildings/one_room_1", 1),
                entry("trail_ruins/buildings/one_room_2", 1),
                entry("trail_ruins/buildings/one_room_3", 1),
                entry("trail_ruins/buildings/one_room_4", 1),
                entry("trail_ruins/buildings/one_room_5", 1)
        ));

        COLLECTION.put("trail_ruins/buildings/grouped", pool(
                "trail_ruins/buildings/grouped",
                entry("trail_ruins/buildings/group_full_1", 1),
                entry("trail_ruins/buildings/group_full_2", 1),
                entry("trail_ruins/buildings/group_full_3", 1),
                entry("trail_ruins/buildings/group_full_4", 1),
                entry("trail_ruins/buildings/group_full_5", 1),
                entry("trail_ruins/buildings/group_lower_1", 1),
                entry("trail_ruins/buildings/group_lower_2", 1),
                entry("trail_ruins/buildings/group_lower_3", 1),
                entry("trail_ruins/buildings/group_lower_4", 1),
                entry("trail_ruins/buildings/group_lower_5", 1),
                entry("trail_ruins/buildings/group_upper_1", 1),
                entry("trail_ruins/buildings/group_upper_2", 1),
                entry("trail_ruins/buildings/group_upper_3", 1),
                entry("trail_ruins/buildings/group_upper_4", 1),
                entry("trail_ruins/buildings/group_upper_5", 1),
                entry("trail_ruins/buildings/group_room_1", 1),
                entry("trail_ruins/buildings/group_room_2", 1),
                entry("trail_ruins/buildings/group_room_3", 1),
                entry("trail_ruins/buildings/group_room_4", 1),
                entry("trail_ruins/buildings/group_room_5", 1)
        ));

        COLLECTION.put("trail_ruins/decor", pool(
                "trail_ruins/decor",
                entry("trail_ruins/decor/decor_1", 1),
                entry("trail_ruins/decor/decor_2", 1),
                entry("trail_ruins/decor/decor_3", 1),
                entry("trail_ruins/decor/decor_4", 1),
                entry("trail_ruins/decor/decor_5", 1),
                entry("trail_ruins/decor/decor_6", 1),
                entry("trail_ruins/decor/decor_7", 1)
        ));
    }

    private static StructurePool pool(String name, StructurePool.Entry... entries) {
        return new StructurePool(name, entries);
    }

    private static StructurePool.Entry entry(String structureName, int weight) {
        return new StructurePool.Entry(structureName, weight);
    }

    @Override
    public String getName() {
        return "trail_ruins";
    }

    protected static final int MIN_DISTANCE = 8;
    protected static final int MAX_DISTANCE = 34;

    @Override
    public boolean canGenerateAt(Location location) {
        int x = location.getFloorX();
        int y = location.getFloorY();
        int z = location.getFloorZ();
        int chunkX = location.getChunkX();
        int chunkZ = location.getChunkZ();
        Dimension level = location.getLevel();
        Xoroshiro128 random = new Xoroshiro128(level.getSeed() ^ Dimension.chunkHash(chunkX, chunkZ));

        int biome = level.getBiomeId(x, y, z);
        BiomeDefinition definition = Registries.BIOME.get(biome);
        return definition.getTags().contains(BiomeTags.HAS_STRUCTURE_TRAIL_RUINS) &&
                ((chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX && (chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ);
    }

    private enum TrailRuinsLoot {
        COMMON(
                weighted(ItemID.BLUE_DYE, 2),
                weighted(ItemID.BRICK, 2),
                weighted(BROWN_CANDLE, 2),
                weighted(ItemID.CLAY_BALL, 2),
                weighted(ItemID.EMERALD, 2),
                weighted(GREEN_CANDLE, 2),
                weighted(ItemID.LIGHT_BLUE_DYE, 2),
                weighted(ItemID.ORANGE_DYE, 2),
                weighted(PURPLE_CANDLE, 2),
                weighted(RED_CANDLE, 2),
                weighted(WHEAT, 2),
                weighted(ItemID.WHITE_DYE, 2),
                weighted(ItemID.WOODEN_HOE, 2),
                weighted(ItemID.YELLOW_DYE, 2),
                weighted(ItemID.BEETROOT_SEEDS, 1),
                weighted(BLUE_STAINED_GLASS_PANE, 1),
                weighted(ItemID.COAL, 1),
                weighted(DEADBUSH, 1),
                weighted(FLOWER_POT, 1),
                weighted(ItemID.LEAD, 1),
                weighted(LIGHT_BLUE_STAINED_GLASS_PANE, 1),
                weighted(MAGENTA_STAINED_GLASS_PANE, 1),
                weighted(OAK_HANGING_SIGN, 1),
                weighted(PINK_STAINED_GLASS_PANE, 1),
                weighted(PURPLE_STAINED_GLASS_PANE, 1),
                weighted(RED_STAINED_GLASS_PANE, 1),
                weighted(SPRUCE_HANGING_SIGN, 1),
                weighted(ItemID.STRING, 1),
                weighted(ItemID.WHEAT_SEEDS, 1),
                weighted(YELLOW_STAINED_GLASS_PANE, 1),
                weighted(ItemID.GOLD_NUGGET, 1)
        ),
        RARE(
                weighted(ItemID.BURN_POTTERY_SHERD, 1),
                weighted(ItemID.DANGER_POTTERY_SHERD, 1),
                weighted(ItemID.MUSIC_DISC_RELIC, 1),
                weighted(ItemID.FRIEND_POTTERY_SHERD, 1),
                weighted(ItemID.HEART_POTTERY_SHERD, 1),
                weighted(ItemID.HEARTBREAK_POTTERY_SHERD, 1),
                weighted(ItemID.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, 1),
                weighted(ItemID.HOWL_POTTERY_SHERD, 1),
                weighted(ItemID.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, 1),
                weighted(ItemID.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, 1),
                weighted(ItemID.SHEAF_POTTERY_SHERD, 1),
                weighted(ItemID.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
        );

        private final List<WeightedItem> items;
        private final int totalWeight;

        TrailRuinsLoot(WeightedItem... items) {
            this.items = List.of(items);
            int total = 0;
            for (WeightedItem item : items) {
                total += item.weight;
            }
            this.totalWeight = total;
        }

        private Item roll(RandomSourceProvider random) {
            int target = random.nextBoundedInt(totalWeight - 1);
            for (WeightedItem item : items) {
                if (target < item.weight) {
                    return Item.get(item.id);
                }
                target -= item.weight;
            }
            return Item.get(items.getFirst().id);
        }

        private static WeightedItem weighted(String id, int weight) {
            return new WeightedItem(id, weight);
        }
    }

    private record WeightedItem(String id, int weight) {
    }
}
