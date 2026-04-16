package cn.nukkit.level.generator.object.structures.jigsaw.bastion;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockEntityHolder;
import cn.nukkit.blockentity.BlockEntityMobSpawner;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RandomizableContainer;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.JigsawStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.RandomSourceProvider;
import cn.nukkit.utils.random.Xoroshiro128;

/**
 * Bastion Remnant Jigsaw Structure for PowerNukkitX.
 */
public class BastionStructure extends JigsawStructure {

    private static final String START = "bastion/starts";
    private static final StructurePoolCollection COLLECTION;
    private static final RandomizableContainer BRIDGE_CHEST = new BridgeChestPopulator();
    private static final RandomizableContainer HOGLIN_STABLE_CHEST = new HoglinStableChestPopulator();
    private static final RandomizableContainer OTHER_CHEST = new OtherChestPopulator();
    private static final RandomizableContainer TREASURE_CHEST = new TreasureChestPopulator();
    private static final Enchantment[] SOUL_SPEED_ONLY = new Enchantment[]{Enchantment.getEnchantment(Enchantment.ID_SOUL_SPEED)};

    @Override
    public StructurePoolCollection getStructurePoolCollection() {
        return COLLECTION;
    }

    @Override
    public String getEntryPool() {
        return START;
    }

    @Override
    protected int getMaxDepth() {
        return 6;
    }

    @Override
    protected boolean strictlyIntersects(BoundingBox first, BoundingBox second) {
        return false;
    }

    @Override
    protected void postProcessStructurePiece(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        registerBastionMobSpawns(structureName, blockManager, jigsaws);
        registerMagmaCubeSpawners(blockManager);

        RandomizableContainer container = getChestLootContainer(structureName);
        if (container == null) {
            return;
        }

        for (Block block : blockManager.getBlocks()) {
            if (!(block instanceof BlockChest chest)) {
                continue;
            }
            Level level = blockManager.getLevel();
            blockManager.addHook(() -> container.create(chest.getOrCreateBlockEntity().getInventory(), createRandom(level, chest.asBlockVector3())));
        }
    }

    private void registerMagmaCubeSpawners(BlockManager blockManager) {
        int magmaCubeEntityId = Registries.ENTITY.getEntityNetworkId(EntityID.MAGMA_CUBE);

        for (Block block : blockManager.getBlocks()) {
            if (!(block instanceof BlockEntityHolder<?> holder) || !(holder.getOrCreateBlockEntity() instanceof BlockEntityMobSpawner spawner)) {
                continue;
            }
            blockManager.addHook(() -> spawner.setSpawnEntityType(magmaCubeEntityId));
        }
    }

    private void registerBastionMobSpawns(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        String entityId = getBastionEntityId(structureName);
        if (entityId == null) {
            return;
        }
        BlockVector3 anchor = resolveAnchor(blockManager, jigsaws);
        if (anchor == null) {
            return;
        }
        Level level = blockManager.getLevel();
        blockManager.addHook(() -> spawnEntity(level, anchor, entityId));
    }

    private String getBastionEntityId(String structureName) {
        if (structureName.contains("/mobs/hoglin")) {
            return EntityID.HOGLIN;
        }
        if (structureName.contains("/mobs/melee_piglin_always")) {
            return EntityID.PIGLIN_BRUTE;
        }
        if (structureName.contains("/mobs/melee_piglin")
                || structureName.contains("/mobs/sword_piglin")
                || structureName.contains("/mobs/crossbow_piglin")) {
            return EntityID.PIGLIN;
        }
        if (structureName.contains("/treasure/brains/center_brain")
                || structureName.contains("/treasure/entrances/entrance_0")
                || structureName.contains("/bridge/starting_pieces/entrance")) {
            return EntityID.PIGLIN_BRUTE;
        }
        return null;
    }

    private BlockVector3 resolveAnchor(BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
        if (jigsaws.length > 0) {
            PNXStructure.Jigsaw jigsaw = jigsaws[0];
            return new BlockVector3(jigsaw.getX(), jigsaw.getY(), jigsaw.getZ());
        }
        if (!blockManager.getBlocks().isEmpty()) {
            Block block = blockManager.getBlocks().getFirst();
            return block.asBlockVector3();
        }
        return null;
    }

    private void spawnEntity(Level level, BlockVector3 anchor, String entityId) {
        Position position = new Position(anchor.getX() + 0.5, anchor.getY() + 1, anchor.getZ() + 0.5, level);
        Entity entity = Entity.createEntity(entityId, position);
        entity.setPersistent(true);
        entity.spawnToAll();
    }

    private RandomizableContainer getChestLootContainer(String structureName) {
        if (structureName.contains("/treasure/")) {
            return TREASURE_CHEST;
        }
        if (structureName.contains("/hoglin_stable/")) {
            return HOGLIN_STABLE_CHEST;
        }
        if (structureName.contains("/bridge/")) {
            return BRIDGE_CHEST;
        }
        if (structureName.contains("/units/")) {
            return OTHER_CHEST;
        }
        return null;
    }

    private RandomSourceProvider createRandom(Level level, BlockVector3 pos) {
        long seed = level.getSeed();
        seed ^= 0x9E3779B97F4A7C15L * pos.getX();
        seed ^= 0xC2B2AE3D27D4EB4FL * pos.getY();
        seed ^= 0x165667B19E3779F9L * pos.getZ();
        return new Xoroshiro128(seed);
    }

    static {
        COLLECTION = new StructurePoolCollection();

        COLLECTION.put(START, pool(
                START,
                entry("bastion/units/air_base", 1),
                entry("bastion/hoglin_stable/air_base", 1),
                entry("bastion/treasure/big_air_full", 1),
                entry("bastion/bridge/starting_pieces/entrance_base", 1)
        ));

        COLLECTION.put("bastion/units/center_pieces", pool(
                "bastion/units/center_pieces",
                entry("bastion/units/center_pieces/center_0", 1),
                entry("bastion/units/center_pieces/center_1", 1),
                entry("bastion/units/center_pieces/center_2", 1)
        ));
        COLLECTION.put("bastion/units/pathways", pool(
                "bastion/units/pathways",
                entry("bastion/units/pathways/pathway_0", 1),
                entry("bastion/units/pathways/pathway_wall_0", 1)
        ));
        COLLECTION.put("bastion/units/walls/wall_bases", pool(
                "bastion/units/walls/wall_bases",
                entry("bastion/units/walls/wall_base", 1),
                entry("bastion/units/walls/connected_wall", 1)
        ));
        COLLECTION.put("bastion/units/stages/stage_0", pool(
                "bastion/units/stages/stage_0",
                entry("bastion/units/stages/stage_0_0", 1),
                entry("bastion/units/stages/stage_0_1", 1),
                entry("bastion/units/stages/stage_0_2", 1),
                entry("bastion/units/stages/stage_0_3", 1)
        ));
        COLLECTION.put("bastion/units/stages/stage_1", pool(
                "bastion/units/stages/stage_1",
                entry("bastion/units/stages/stage_1_0", 1),
                entry("bastion/units/stages/stage_1_1", 1),
                entry("bastion/units/stages/stage_1_2", 1),
                entry("bastion/units/stages/stage_1_3", 1)
        ));
        COLLECTION.put("bastion/units/stages/rot/stage_1", pool(
                "bastion/units/stages/rot/stage_1",
                entry("bastion/units/stages/rot/stage_1_0", 1)
        ));
        COLLECTION.put("bastion/units/stages/stage_2", pool(
                "bastion/units/stages/stage_2",
                entry("bastion/units/stages/stage_2_0", 1),
                entry("bastion/units/stages/stage_2_1", 1)
        ));
        COLLECTION.put("bastion/units/stages/stage_3", pool(
                "bastion/units/stages/stage_3",
                entry("bastion/units/stages/stage_3_0", 1),
                entry("bastion/units/stages/stage_3_1", 1),
                entry("bastion/units/stages/stage_3_2", 1),
                entry("bastion/units/stages/stage_3_3", 1)
        ));
        COLLECTION.put("bastion/units/fillers/stage_0", pool(
                "bastion/units/fillers/stage_0",
                entry("bastion/units/fillers/stage_0", 1)
        ));
        COLLECTION.put("bastion/units/edges", pool(
                "bastion/units/edges",
                entry("bastion/units/edges/edge_0", 1)
        ));
        COLLECTION.put("bastion/units/wall_units", pool(
                "bastion/units/wall_units",
                entry("bastion/units/wall_units/unit_0", 1)
        ));
        COLLECTION.put("bastion/units/edge_wall_units", pool(
                "bastion/units/edge_wall_units",
                entry("bastion/units/wall_units/edge_0_large", 1)
        ));
        COLLECTION.put("bastion/units/ramparts", pool(
                "bastion/units/ramparts",
                entry("bastion/units/ramparts/ramparts_0", 1),
                entry("bastion/units/ramparts/ramparts_1", 1),
                entry("bastion/units/ramparts/ramparts_2", 1)
        ));
        COLLECTION.put("bastion/units/large_ramparts", pool(
                "bastion/units/large_ramparts",
                entry("bastion/units/ramparts/ramparts_0", 1)
        ));
        COLLECTION.put("bastion/units/rampart_plates", pool(
                "bastion/units/rampart_plates",
                entry("bastion/units/rampart_plates/plate_0", 1)
        ));

        COLLECTION.put("bastion/hoglin_stable/starting_pieces", pool(
                "bastion/hoglin_stable/starting_pieces",
                entry("bastion/hoglin_stable/starting_pieces/starting_stairs_0", 1),
                entry("bastion/hoglin_stable/starting_pieces/starting_stairs_1", 1),
                entry("bastion/hoglin_stable/starting_pieces/starting_stairs_2", 1),
                entry("bastion/hoglin_stable/starting_pieces/starting_stairs_3", 1),
                entry("bastion/hoglin_stable/starting_pieces/starting_stairs_4", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/mirrored_starting_pieces", pool(
                "bastion/hoglin_stable/mirrored_starting_pieces",
                entry("bastion/hoglin_stable/starting_pieces/stairs_0_mirrored", 1),
                entry("bastion/hoglin_stable/starting_pieces/stairs_1_mirrored", 1),
                entry("bastion/hoglin_stable/starting_pieces/stairs_2_mirrored", 1),
                entry("bastion/hoglin_stable/starting_pieces/stairs_3_mirrored", 1),
                entry("bastion/hoglin_stable/starting_pieces/stairs_4_mirrored", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/wall_bases", pool(
                "bastion/hoglin_stable/wall_bases",
                entry("bastion/hoglin_stable/walls/wall_base", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/walls", pool(
                "bastion/hoglin_stable/walls",
                entry("bastion/hoglin_stable/walls/side_wall_0", 1),
                entry("bastion/hoglin_stable/walls/side_wall_1", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/stairs", pool(
                "bastion/hoglin_stable/stairs",
                entry("bastion/hoglin_stable/stairs/stairs_1_0", 1),
                entry("bastion/hoglin_stable/stairs/stairs_1_1", 1),
                entry("bastion/hoglin_stable/stairs/stairs_1_2", 1),
                entry("bastion/hoglin_stable/stairs/stairs_1_3", 1),
                entry("bastion/hoglin_stable/stairs/stairs_1_4", 1),
                entry("bastion/hoglin_stable/stairs/stairs_2_0", 1),
                entry("bastion/hoglin_stable/stairs/stairs_2_1", 1),
                entry("bastion/hoglin_stable/stairs/stairs_2_2", 1),
                entry("bastion/hoglin_stable/stairs/stairs_2_3", 1),
                entry("bastion/hoglin_stable/stairs/stairs_2_4", 1),
                entry("bastion/hoglin_stable/stairs/stairs_3_0", 1),
                entry("bastion/hoglin_stable/stairs/stairs_3_1", 1),
                entry("bastion/hoglin_stable/stairs/stairs_3_2", 1),
                entry("bastion/hoglin_stable/stairs/stairs_3_3", 1),
                entry("bastion/hoglin_stable/stairs/stairs_3_4", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/small_stables/inner", pool(
                "bastion/hoglin_stable/small_stables/inner",
                entry("bastion/hoglin_stable/small_stables/inner_0", 1),
                entry("bastion/hoglin_stable/small_stables/inner_1", 1),
                entry("bastion/hoglin_stable/small_stables/inner_2", 1),
                entry("bastion/hoglin_stable/small_stables/inner_3", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/small_stables/outer", pool(
                "bastion/hoglin_stable/small_stables/outer",
                entry("bastion/hoglin_stable/small_stables/outer_0", 1),
                entry("bastion/hoglin_stable/small_stables/outer_1", 1),
                entry("bastion/hoglin_stable/small_stables/outer_2", 1),
                entry("bastion/hoglin_stable/small_stables/outer_3", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/large_stables/inner", pool(
                "bastion/hoglin_stable/large_stables/inner",
                entry("bastion/hoglin_stable/large_stables/inner_0", 1),
                entry("bastion/hoglin_stable/large_stables/inner_1", 1),
                entry("bastion/hoglin_stable/large_stables/inner_2", 1),
                entry("bastion/hoglin_stable/large_stables/inner_3", 1),
                entry("bastion/hoglin_stable/large_stables/inner_4", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/large_stables/outer", pool(
                "bastion/hoglin_stable/large_stables/outer",
                entry("bastion/hoglin_stable/large_stables/outer_0", 1),
                entry("bastion/hoglin_stable/large_stables/outer_1", 1),
                entry("bastion/hoglin_stable/large_stables/outer_2", 1),
                entry("bastion/hoglin_stable/large_stables/outer_3", 1),
                entry("bastion/hoglin_stable/large_stables/outer_4", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/posts", pool(
                "bastion/hoglin_stable/posts",
                entry("bastion/hoglin_stable/posts/stair_post", 1),
                entry("bastion/hoglin_stable/posts/end_post", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/ramparts", pool(
                "bastion/hoglin_stable/ramparts",
                entry("bastion/hoglin_stable/ramparts/ramparts_1", 1),
                entry("bastion/hoglin_stable/ramparts/ramparts_2", 1),
                entry("bastion/hoglin_stable/ramparts/ramparts_3", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/rampart_plates", pool(
                "bastion/hoglin_stable/rampart_plates",
                entry("bastion/hoglin_stable/rampart_plates/rampart_plate_1", 1)
        ));
        COLLECTION.put("bastion/hoglin_stable/connectors", pool(
                "bastion/hoglin_stable/connectors",
                entry("bastion/hoglin_stable/connectors/end_post_connector", 1)
        ));

        COLLECTION.put("bastion/treasure/bases", pool(
                "bastion/treasure/bases",
                entry("bastion/treasure/bases/lava_basin", 1)
        ));
        COLLECTION.put("bastion/treasure/stairs", pool(
                "bastion/treasure/stairs",
                entry("bastion/treasure/stairs/lower_stairs", 1)
        ));
        COLLECTION.put("bastion/treasure/bases/centers", pool(
                "bastion/treasure/bases/centers",
                entry("bastion/treasure/bases/centers/center_0", 1),
                entry("bastion/treasure/bases/centers/center_1", 1),
                entry("bastion/treasure/bases/centers/center_2", 1),
                entry("bastion/treasure/bases/centers/center_3", 1)
        ));
        COLLECTION.put("bastion/treasure/brains", pool(
                "bastion/treasure/brains",
                entry("bastion/treasure/brains/center_brain", 1)
        ));
        COLLECTION.put("bastion/treasure/walls", pool(
                "bastion/treasure/walls",
                entry("bastion/treasure/walls/lava_wall", 1),
                entry("bastion/treasure/walls/entrance_wall", 1)
        ));
        COLLECTION.put("bastion/treasure/walls/outer", pool(
                "bastion/treasure/walls/outer",
                entry("bastion/treasure/walls/outer/top_corner", 1),
                entry("bastion/treasure/walls/outer/mid_corner", 1),
                entry("bastion/treasure/walls/outer/bottom_corner", 1),
                entry("bastion/treasure/walls/outer/outer_wall", 1),
                entry("bastion/treasure/walls/outer/medium_outer_wall", 1),
                entry("bastion/treasure/walls/outer/tall_outer_wall", 1)
        ));
        COLLECTION.put("bastion/treasure/walls/bottom", pool(
                "bastion/treasure/walls/bottom",
                entry("bastion/treasure/walls/bottom/wall_0", 1),
                entry("bastion/treasure/walls/bottom/wall_1", 1),
                entry("bastion/treasure/walls/bottom/wall_2", 1),
                entry("bastion/treasure/walls/bottom/wall_3", 1)
        ));
        COLLECTION.put("bastion/treasure/walls/mid", pool(
                "bastion/treasure/walls/mid",
                entry("bastion/treasure/walls/mid/wall_0", 1),
                entry("bastion/treasure/walls/mid/wall_1", 1),
                entry("bastion/treasure/walls/mid/wall_2", 1)
        ));
        COLLECTION.put("bastion/treasure/walls/top", pool(
                "bastion/treasure/walls/top",
                entry("bastion/treasure/walls/top/main_entrance", 1),
                entry("bastion/treasure/walls/top/wall_0", 1),
                entry("bastion/treasure/walls/top/wall_1", 1)
        ));
        COLLECTION.put("bastion/treasure/connectors", pool(
                "bastion/treasure/connectors",
                entry("bastion/treasure/connectors/center_to_wall_middle", 1),
                entry("bastion/treasure/connectors/center_to_wall_top", 1),
                entry("bastion/treasure/connectors/center_to_wall_top_entrance", 1)
        ));
        COLLECTION.put("bastion/treasure/entrances", pool(
                "bastion/treasure/entrances",
                entry("bastion/treasure/entrances/entrance_0", 1)
        ));
        COLLECTION.put("bastion/treasure/ramparts", pool(
                "bastion/treasure/ramparts",
                entry("bastion/treasure/ramparts/mid_wall_main", 1),
                entry("bastion/treasure/ramparts/mid_wall_side", 1),
                entry("bastion/treasure/ramparts/bottom_wall_0", 1),
                entry("bastion/treasure/ramparts/top_wall", 1),
                entry("bastion/treasure/ramparts/lava_basin_side", 1),
                entry("bastion/treasure/ramparts/lava_basin_main", 1)
        ));
        COLLECTION.put("bastion/treasure/corners/bottom", pool(
                "bastion/treasure/corners/bottom",
                entry("bastion/treasure/corners/bottom/corner_0", 1),
                entry("bastion/treasure/corners/bottom/corner_1", 1)
        ));
        COLLECTION.put("bastion/treasure/corners/edges", pool(
                "bastion/treasure/corners/edges",
                entry("bastion/treasure/corners/edges/bottom", 1),
                entry("bastion/treasure/corners/edges/middle", 1),
                entry("bastion/treasure/corners/edges/top", 1)
        ));
        COLLECTION.put("bastion/treasure/corners/middle", pool(
                "bastion/treasure/corners/middle",
                entry("bastion/treasure/corners/middle/corner_0", 1),
                entry("bastion/treasure/corners/middle/corner_1", 1)
        ));
        COLLECTION.put("bastion/treasure/corners/top", pool(
                "bastion/treasure/corners/top",
                entry("bastion/treasure/corners/top/corner_0", 1),
                entry("bastion/treasure/corners/top/corner_1", 1)
        ));
        COLLECTION.put("bastion/treasure/extensions/large_pool", pool(
                "bastion/treasure/extensions/large_pool",
                entry("bastion/treasure/extensions/empty", 1),
                entry("bastion/treasure/extensions/empty", 1),
                entry("bastion/treasure/extensions/fire_room", 1),
                entry("bastion/treasure/extensions/large_bridge_0", 1),
                entry("bastion/treasure/extensions/large_bridge_1", 1),
                entry("bastion/treasure/extensions/large_bridge_2", 1),
                entry("bastion/treasure/extensions/large_bridge_3", 1),
                entry("bastion/treasure/extensions/roofed_bridge", 1),
                entry("bastion/treasure/extensions/empty", 1)
        ));
        COLLECTION.put("bastion/treasure/extensions/small_pool", pool(
                "bastion/treasure/extensions/small_pool",
                entry("bastion/treasure/extensions/empty", 1),
                entry("bastion/treasure/extensions/fire_room", 1),
                entry("bastion/treasure/extensions/empty", 1),
                entry("bastion/treasure/extensions/small_bridge_0", 1),
                entry("bastion/treasure/extensions/small_bridge_1", 1),
                entry("bastion/treasure/extensions/small_bridge_2", 1),
                entry("bastion/treasure/extensions/small_bridge_3", 1)
        ));
        COLLECTION.put("bastion/treasure/extensions/houses", pool(
                "bastion/treasure/extensions/houses",
                entry("bastion/treasure/extensions/house_0", 1),
                entry("bastion/treasure/extensions/house_1", 1)
        ));
        COLLECTION.put("bastion/treasure/roofs", pool(
                "bastion/treasure/roofs",
                entry("bastion/treasure/roofs/wall_roof", 1),
                entry("bastion/treasure/roofs/corner_roof", 1),
                entry("bastion/treasure/roofs/center_roof", 1)
        ));

        COLLECTION.put("bastion/bridge/starting_pieces", pool(
                "bastion/bridge/starting_pieces",
                entry("bastion/bridge/starting_pieces/entrance", 1),
                entry("bastion/bridge/starting_pieces/entrance_face", 1)
        ));
        COLLECTION.put("bastion/bridge/bridge_pieces", pool(
                "bastion/bridge/bridge_pieces",
                entry("bastion/bridge/bridge_pieces/bridge", 1)
        ));
        COLLECTION.put("bastion/bridge/legs", pool(
                "bastion/bridge/legs",
                entry("bastion/bridge/legs/leg_0", 1),
                entry("bastion/bridge/legs/leg_1", 1)
        ));
        COLLECTION.put("bastion/bridge/walls", pool(
                "bastion/bridge/walls",
                entry("bastion/bridge/walls/wall_base_0", 1),
                entry("bastion/bridge/walls/wall_base_1", 1)
        ));
        COLLECTION.put("bastion/bridge/ramparts", pool(
                "bastion/bridge/ramparts",
                entry("bastion/bridge/ramparts/rampart_0", 1),
                entry("bastion/bridge/ramparts/rampart_1", 1)
        ));
        COLLECTION.put("bastion/bridge/rampart_plates", pool(
                "bastion/bridge/rampart_plates",
                entry("bastion/bridge/rampart_plates/plate_0", 1)
        ));
        COLLECTION.put("bastion/bridge/connectors", pool(
                "bastion/bridge/connectors",
                entry("bastion/bridge/connectors/back_bridge_top", 1),
                entry("bastion/bridge/connectors/back_bridge_bottom", 1)
        ));

        COLLECTION.put("bastion/mobs/piglin", pool(
                "bastion/mobs/piglin",
                entry("bastion/mobs/melee_piglin", 1),
                entry("bastion/mobs/sword_piglin", 4),
                entry("bastion/mobs/crossbow_piglin", 4),
                entry("bastion/mobs/empty", 1)
        ));
        COLLECTION.put("bastion/mobs/hoglin", pool(
                "bastion/mobs/hoglin",
                entry("bastion/mobs/hoglin", 2),
                entry("bastion/mobs/empty", 1)
        ));
        COLLECTION.put("bastion/blocks/gold", pool(
                "bastion/blocks/gold",
                entry("bastion/blocks/air", 3),
                entry("bastion/blocks/gold", 1)
        ));
        COLLECTION.put("bastion/mobs/piglin_melee", pool(
                "bastion/mobs/piglin_melee",
                entry("bastion/mobs/melee_piglin_always", 1),
                entry("bastion/mobs/melee_piglin", 5),
                entry("bastion/mobs/sword_piglin", 1)
        ));
    }

    private static StructurePool pool(String name, StructurePool.Entry... entries) {
        return new StructurePool(name, entries);
    }

    private static StructurePool.Entry entry(String structureName, int weight) {
        return new StructurePool.Entry(structureName, weight);
    }

    private static class BridgeChestPopulator extends RandomizableContainer {
        BridgeChestPopulator() {
            PoolBuilder fixed = new PoolBuilder()
                    .register(new ItemEntry(Block.LODESTONE, 1));
            this.pools.put(fixed.build(), new RollEntry(1, fixed.getTotalWeight()));

            PoolBuilder rare = new PoolBuilder()
                    .register(new ItemEntry(Item.CROSSBOW, 1, 1, 1, 1, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.ARROW, 0, 28, 10, 1))
                    .register(new ItemEntry(Block.GILDED_BLACKSTONE, 0, 12, 8, 1))
                    .register(new ItemEntry(Block.CRYING_OBSIDIAN, 0, 8, 3, 1))
                    .register(new ItemEntry(Block.GOLD_BLOCK, 1))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 9, 4, 1))
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 9, 4, 1))
                    .register(new ItemEntry(Item.GOLDEN_SWORD, 1))
                    .register(new ItemEntry(Item.GOLDEN_CHESTPLATE, 0, 1, 1, 1, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_HELMET, 0, 1, 1, 1, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_LEGGINGS, 0, 1, 1, 1, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_BOOTS, 0, 1, 1, 1, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.GOLDEN_AXE, 0, 1, 1, 1, getDefaultEnchantments()));
            this.pools.put(rare.build(), new RollEntry(2, 1, rare.getTotalWeight()));

            PoolBuilder common = new PoolBuilder()
                    .register(new ItemEntry(Item.STRING, 0, 6, 1, 1))
                    .register(new ItemEntry(Item.LEATHER, 0, 3, 1, 1))
                    .register(new ItemEntry(Item.ARROW, 0, 17, 5, 1))
                    .register(new ItemEntry(Item.IRON_NUGGET, 0, 6, 2, 1))
                    .register(new ItemEntry(Item.GOLD_NUGGET, 0, 6, 2, 1));
            this.pools.put(common.build(), new RollEntry(4, 2, common.getTotalWeight()));

            PoolBuilder templates = new PoolBuilder()
                    .register(new ItemEntry(Item.AIR.getId(), 11))
                    .register(new ItemEntry(ItemID.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 1));
            this.pools.put(templates.build(), new RollEntry(1, templates.getTotalWeight()));

            PoolBuilder upgrade = new PoolBuilder()
                    .register(new ItemEntry(Item.AIR.getId(), 9))
                    .register(new ItemEntry(ItemID.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1));
            this.pools.put(upgrade.build(), new RollEntry(1, upgrade.getTotalWeight()));
        }
    }

    private static class HoglinStableChestPopulator extends RandomizableContainer {
        HoglinStableChestPopulator() {
            PoolBuilder primary = new PoolBuilder()
                    .register(new ItemEntry(Item.DIAMOND_SHOVEL, 0, 1, 1, 15, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_PICKAXE, 0, 1, 1, 12, getDefaultEnchantments()))
                    .register(new ItemEntry(ItemID.NETHERITE_SCRAP, 8))
                    .register(new ItemEntry(Block.ANCIENT_DEBRIS, 12))
                    .register(new ItemEntry(Block.ANCIENT_DEBRIS, 0, 2, 2, 5))
                    .register(new ItemEntry(Item.SADDLE, 12))
                    .register(new ItemEntry(Block.GOLD_BLOCK, 0, 4, 2, 16))
                    .register(new ItemEntry(ItemID.GOLDEN_CARROT, 0, 17, 8, 10))
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 10));
            this.pools.put(primary.build(), new RollEntry(1, primary.getTotalWeight()));

            PoolBuilder secondary = new PoolBuilder()
                    .register(new ItemEntry(Item.GOLDEN_AXE, 0, 1, 1, 1, getDefaultEnchantments()))
                    .register(new ItemEntry(Block.CRYING_OBSIDIAN, 0, 5, 1, 1))
                    .register(new ItemEntry(Block.GLOWSTONE, 0, 6, 3, 1))
                    .register(new ItemEntry(Block.GILDED_BLACKSTONE, 0, 5, 2, 1))
                    .register(new ItemEntry(Block.SOUL_SAND, 0, 7, 2, 1))
                    .register(new ItemEntry(Block.CRIMSON_NYLIUM, 0, 7, 2, 1))
                    .register(new ItemEntry(Item.GOLD_NUGGET, 0, 8, 2, 1))
                    .register(new ItemEntry(Item.LEATHER, 0, 3, 1, 1))
                    .register(new ItemEntry(Item.ARROW, 0, 17, 5, 1))
                    .register(new ItemEntry(Item.STRING, 0, 8, 3, 1))
                    .register(new ItemEntry(Item.PORKCHOP, 0, 5, 2, 1))
                    .register(new ItemEntry(Item.COOKED_PORKCHOP, 0, 5, 2, 1))
                    .register(new ItemEntry(Block.CRIMSON_FUNGUS, 0, 7, 2, 1))
                    .register(new ItemEntry(Block.CRIMSON_ROOTS, 0, 7, 2, 1));
            this.pools.put(secondary.build(), new RollEntry(4, 3, secondary.getTotalWeight()));

            PoolBuilder templates = new PoolBuilder()
                    .register(new ItemEntry(Item.AIR.getId(), 11))
                    .register(new ItemEntry(ItemID.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 1));
            this.pools.put(templates.build(), new RollEntry(1, templates.getTotalWeight()));

            PoolBuilder upgrade = new PoolBuilder()
                    .register(new ItemEntry(Item.AIR.getId(), 9))
                    .register(new ItemEntry(ItemID.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1));
            this.pools.put(upgrade.build(), new RollEntry(1, upgrade.getTotalWeight()));
        }
    }

    private static class OtherChestPopulator extends RandomizableContainer {
        OtherChestPopulator() {
            PoolBuilder primary = new PoolBuilder()
                    .register(new ItemEntry(Item.DIAMOND_PICKAXE, 0, 1, 1, 6, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_SHOVEL, 6))
                    .register(new ItemEntry(Item.CROSSBOW, 0, 1, 1, 6, getDefaultEnchantments()))
                    .register(new ItemEntry(Block.ANCIENT_DEBRIS, 12))
                    .register(new ItemEntry(ItemID.NETHERITE_SCRAP, 4))
                    .register(new ItemEntry(Item.ARROW, 0, 22, 10, 10))
                    .register(new ItemEntry(ItemID.PIGLIN_BANNER_PATTERN, 9))
                    .register(new ItemEntry(ItemID.MUSIC_DISC_PIGSTEP, 5))
                    .register(new ItemEntry(ItemID.GOLDEN_CARROT, 0, 17, 6, 12))
                    .register(new ItemEntry(Item.GOLDEN_APPLE, 9))
                    .register(new ItemEntry(Item.ENCHANTED_BOOK, 0, 1, 1, 10, SOUL_SPEED_ONLY));
            this.pools.put(primary.build(), new RollEntry(1, primary.getTotalWeight()));

            PoolBuilder equipment = new PoolBuilder()
                    .register(new ItemEntry(Item.IRON_SWORD, 0, 1, 1, 2, getDefaultEnchantments()))
                    .register(new ItemEntry(Block.IRON_BLOCK, 2))
                    .register(new ItemEntry(Item.GOLDEN_BOOTS, 0, 1, 1, 1, SOUL_SPEED_ONLY))
                    .register(new ItemEntry(Item.GOLDEN_AXE, 0, 1, 1, 1, getDefaultEnchantments()))
                    .register(new ItemEntry(Block.GOLD_BLOCK, 2))
                    .register(new ItemEntry(Item.CROSSBOW, 1))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 6, 1, 2))
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 6, 1, 2))
                    .register(new ItemEntry(Item.GOLDEN_SWORD, 1))
                    .register(new ItemEntry(Item.GOLDEN_CHESTPLATE, 1))
                    .register(new ItemEntry(Item.GOLDEN_HELMET, 1))
                    .register(new ItemEntry(Item.GOLDEN_LEGGINGS, 1))
                    .register(new ItemEntry(Item.GOLDEN_BOOTS, 1))
                    .register(new ItemEntry(Block.CRYING_OBSIDIAN, 0, 5, 1, 2));
            this.pools.put(equipment.build(), new RollEntry(2, equipment.getTotalWeight()));

            PoolBuilder materials = new PoolBuilder()
                    .register(new ItemEntry(Block.GILDED_BLACKSTONE, 0, 5, 1, 2))
                    .register(new ItemEntry(Block.IRON_CHAIN, 0, 10, 2, 1))
                    .register(new ItemEntry(ItemID.MAGMA_CREAM, 0, 6, 2, 2))
                    .register(new ItemEntry(Block.BONE_BLOCK, 0, 6, 3, 1))
                    .register(new ItemEntry(Item.IRON_NUGGET, 0, 8, 2, 1))
                    .register(new ItemEntry(Block.OBSIDIAN, 0, 6, 4, 1))
                    .register(new ItemEntry(Item.GOLD_NUGGET, 0, 8, 2, 1))
                    .register(new ItemEntry(Item.STRING, 0, 6, 4, 1))
                    .register(new ItemEntry(Item.ARROW, 0, 17, 5, 2))
                    .register(new ItemEntry(Item.COOKED_PORKCHOP, 1));
            this.pools.put(materials.build(), new RollEntry(4, 3, materials.getTotalWeight()));

            PoolBuilder templates = new PoolBuilder()
                    .register(new ItemEntry(Item.AIR.getId(), 11))
                    .register(new ItemEntry(ItemID.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 1));
            this.pools.put(templates.build(), new RollEntry(1, templates.getTotalWeight()));

            PoolBuilder upgrade = new PoolBuilder()
                    .register(new ItemEntry(Item.AIR.getId(), 9))
                    .register(new ItemEntry(ItemID.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1));
            this.pools.put(upgrade.build(), new RollEntry(1, upgrade.getTotalWeight()));
        }
    }

    private static class TreasureChestPopulator extends RandomizableContainer {
        TreasureChestPopulator() {
            PoolBuilder primary = new PoolBuilder()
                    .register(new ItemEntry(ItemID.NETHERITE_INGOT, 15))
                    .register(new ItemEntry(Block.ANCIENT_DEBRIS, 10))
                    .register(new ItemEntry(ItemID.NETHERITE_SCRAP, 8))
                    .register(new ItemEntry(Block.ANCIENT_DEBRIS, 0, 2, 2, 4))
                    .register(new ItemEntry(Item.DIAMOND_SWORD, 0, 1, 1, 6, getDefaultEnchantments()))
                    .register(new ItemEntry(ItemID.DIAMOND_SPEAR, 0, 1, 1, 6, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_CHESTPLATE, 0, 1, 1, 6, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_HELMET, 0, 1, 1, 6, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_LEGGINGS, 0, 1, 1, 6, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_BOOTS, 0, 1, 1, 6, getDefaultEnchantments()))
                    .register(new ItemEntry(Item.DIAMOND_SWORD, 6))
                    .register(new ItemEntry(ItemID.DIAMOND_SPEAR, 6))
                    .register(new ItemEntry(Item.DIAMOND_CHESTPLATE, 5))
                    .register(new ItemEntry(Item.DIAMOND_HELMET, 5))
                    .register(new ItemEntry(Item.DIAMOND_BOOTS, 5))
                    .register(new ItemEntry(Item.DIAMOND_LEGGINGS, 5))
                    .register(new ItemEntry(Item.DIAMOND, 0, 6, 2, 5))
                    .register(new ItemEntry(Item.ENCHANTED_GOLDEN_APPLE, 2));
            this.pools.put(primary.build(), new RollEntry(3, primary.getTotalWeight()));

            PoolBuilder secondary = new PoolBuilder()
                    .register(new ItemEntry(Item.ARROW, 0, 25, 12, 1))
                    .register(new ItemEntry(Block.GOLD_BLOCK, 0, 5, 2, 1))
                    .register(new ItemEntry(Block.IRON_BLOCK, 0, 5, 2, 1))
                    .register(new ItemEntry(Item.GOLD_INGOT, 0, 9, 3, 1))
                    .register(new ItemEntry(Item.IRON_INGOT, 0, 9, 3, 1))
                    .register(new ItemEntry(Block.CRYING_OBSIDIAN, 0, 5, 3, 1))
                    .register(new ItemEntry(Item.QUARTZ, 0, 23, 8, 1))
                    .register(new ItemEntry(Block.GILDED_BLACKSTONE, 0, 15, 5, 1))
                    .register(new ItemEntry(ItemID.MAGMA_CREAM, 0, 8, 3, 1));
            this.pools.put(secondary.build(), new RollEntry(4, 3, secondary.getTotalWeight()));

            PoolBuilder templates = new PoolBuilder()
                    .register(new ItemEntry(Item.AIR.getId(), 11))
                    .register(new ItemEntry(ItemID.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 1));
            this.pools.put(templates.build(), new RollEntry(1, templates.getTotalWeight()));

            PoolBuilder upgrade = new PoolBuilder()
                    .register(new ItemEntry(ItemID.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1));
            this.pools.put(upgrade.build(), new RollEntry(1, upgrade.getTotalWeight()));
        }
    }

    @Override
    protected void postProcessStructure(StructureHelper helper) {
        helper.applySubChunkUpdate();
    }
}
