package cn.nukkit.registry;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.NotNull;

public class FuelRegistry extends BaseRegistry<String, Integer, Integer> {
    private static final Object2IntOpenHashMap<String> REGISTRY = new Object2IntOpenHashMap<>();

    @Override
    public void init() {
        register(BlockID.COAL_BLOCK, 16000);
        register(ItemID.COAL, 1600);
        register(BlockID.WOOD, 300);

        register(BlockID.ACACIA_PLANKS, 300);
        register(BlockID.BAMBOO_PLANKS, 300);
        register(BlockID.BIRCH_PLANKS, 300);
        register(BlockID.CHERRY_PLANKS, 300);
        register(BlockID.CRIMSON_PLANKS, 300);
        register(BlockID.JUNGLE_PLANKS, 300);
        register(BlockID.OAK_PLANKS, 300);
        register(BlockID.WARPED_PLANKS, 300);
        register(BlockID.MANGROVE_PLANKS, 300);
        register(BlockID.SPRUCE_PLANKS, 300);
        register(BlockID.DARK_OAK_PLANKS, 300);

        register(BlockID.ACACIA_FENCE, 300);
        register(BlockID.BAMBOO_FENCE, 300);
        register(BlockID.BIRCH_FENCE, 300);
        register(BlockID.CHERRY_FENCE, 300);
        register(BlockID.CRIMSON_FENCE, 300);
        register(BlockID.JUNGLE_FENCE, 300);
        register(BlockID.OAK_FENCE, 300);
        register(BlockID.WARPED_FENCE, 300);
        register(BlockID.MANGROVE_FENCE, 300);
        register(BlockID.SPRUCE_FENCE, 300);
        register(BlockID.DARK_OAK_FENCE, 300);

        register(BlockID.ACACIA_FENCE_GATE, 300);
        register(BlockID.BAMBOO_FENCE_GATE, 300);
        register(BlockID.BIRCH_FENCE_GATE, 300);
        register(BlockID.CHERRY_FENCE_GATE, 300);
        register(BlockID.CRIMSON_FENCE_GATE, 300);
        register(BlockID.JUNGLE_FENCE_GATE, 300);
        register(BlockID.FENCE_GATE, 300);
        register(BlockID.WARPED_FENCE_GATE, 300);
        register(BlockID.MANGROVE_FENCE_GATE, 300);
        register(BlockID.SPRUCE_FENCE_GATE, 300);
        register(BlockID.DARK_OAK_FENCE_GATE, 300);

        register(BlockID.ACACIA_STAIRS, 300);
        register(BlockID.BAMBOO_STAIRS, 300);
        register(BlockID.BIRCH_STAIRS, 300);
        register(BlockID.CHERRY_STAIRS, 300);
        register(BlockID.CRIMSON_STAIRS, 300);
        register(BlockID.JUNGLE_STAIRS, 300);
        register(BlockID.WARPED_STAIRS, 300);
        register(BlockID.MANGROVE_STAIRS, 300);
        register(BlockID.SPRUCE_STAIRS, 300);
        register(BlockID.DARK_OAK_STAIRS, 300);

        register(ItemID.ACACIA_SIGN, 200);
        register(ItemID.BAMBOO_SIGN, 200);
        register(ItemID.BIRCH_SIGN, 200);
        register(ItemID.CHERRY_SIGN, 200);
        register(ItemID.CRIMSON_SIGN, 200);
        register(ItemID.JUNGLE_SIGN, 200);
        register(ItemID.WARPED_SIGN, 200);
        register(ItemID.MANGROVE_SIGN, 200);
        register(ItemID.SPRUCE_SIGN, 200);
        register(ItemID.DARK_OAK_SIGN, 200);

        register(BlockID.SAPLING, 100);
        register(ItemID.WOODEN_AXE, 200);
        register(ItemID.WOODEN_PICKAXE, 200);
        register(ItemID.WOODEN_SWORD, 200);
        register(ItemID.WOODEN_SHOVEL, 200);
        register(ItemID.WOODEN_HOE, 200);
        register(ItemID.STICK, 100);
        register(BlockID.OAK_STAIRS, 300);
        register(BlockID.SPRUCE_STAIRS, 300);
        register(BlockID.BIRCH_STAIRS, 300);
        register(BlockID.JUNGLE_STAIRS, 300);
        register(BlockID.TRAPDOOR, 300);
        register(BlockID.CRAFTING_TABLE, 300);
        register(BlockID.BOOKSHELF, 300);
        register(BlockID.CHEST, 300);
        register(ItemID.BUCKET, 20000);
        register(BlockID.LADDER, 300);
        register(ItemID.BOW, 200);
        register(ItemID.BOWL, 100);
        register(BlockID.CHERRY_WOOD, 300);
        register(BlockID.MANGROVE_WOOD, 300);
        register(BlockID.STRIPPED_CHERRY_WOOD, 300);
        register(BlockID.STRIPPED_MANGROVE_WOOD, 300);
        register(BlockID.WOODEN_PRESSURE_PLATE, 300);
        register(BlockID.TRAPPED_CHEST, 300);
        register(BlockID.DAYLIGHT_DETECTOR, 300);
        register(BlockID.DAYLIGHT_DETECTOR_INVERTED, 300);
        register(BlockID.JUKEBOX, 300);
        register(BlockID.NOTEBLOCK, 300);
        register(BlockID.WOODEN_SLAB, 300);
        register(BlockID.DOUBLE_WOODEN_SLAB, 300);
        register(ItemID.BOAT, 1200);
        register(ItemID.BLAZE_ROD, 2400);
        register(BlockID.BROWN_MUSHROOM_BLOCK, 300);
        register(BlockID.RED_MUSHROOM_BLOCK, 300);
        register(ItemID.FISHING_ROD, 300);
        register(BlockID.WOODEN_BUTTON, 100);
        register(ItemID.WOODEN_DOOR, 200);
        register(ItemID.SPRUCE_DOOR, 200);
        register(ItemID.BIRCH_DOOR, 200);
        register(ItemID.JUNGLE_DOOR, 200);
        register(ItemID.ACACIA_DOOR, 200);
        register(ItemID.DARK_OAK_DOOR, 200);
        register(ItemID.BANNER, 300);
        register(BlockID.DEADBUSH, 100);
        register(BlockID.DRIED_KELP_BLOCK, 4000);
        register(ItemID.CROSSBOW, 200);
        register(BlockID.BEE_NEST, 300);
        register(BlockID.BEEHIVE, 300);
        register(BlockID.BAMBOO, 50);
        register(BlockID.SCAFFOLDING, 50);
        register(BlockID.CARTOGRAPHY_TABLE, 300);
        register(BlockID.FLETCHING_TABLE, 300);
        register(BlockID.SMITHING_TABLE, 300);
        register(BlockID.LOOM, 300);
        register(BlockID.LECTERN, 300);
        register(BlockID.COMPOSTER, 300);
        register(BlockID.BARREL, 300);
        register(BlockID.AZALEA, 100);
    }

    @Override
    public Integer get(String key) {
        return REGISTRY.get(key);
    }

    /**
     * @param item item
     * @return fuel duration, if it cannot be used as fuel, return -1.
     */
    public int getFuelDuration(@NotNull Item item) {
        int id = REGISTRY.getInt(item.getId());
        if (id == 0 && item.isBlock()) {
            id = REGISTRY.getInt(item.getBlockId());
        }
        return id;
    }

    public boolean isFuel(@NotNull Item item) {
        boolean b = REGISTRY.containsKey(item.getId());
        if (!b && item.isBlock()) {
            b = REGISTRY.containsKey(item.getBlockId());
        }
        return b;
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public OK<?> register(String key, Integer value) {
        if (REGISTRY.putIfAbsent(key, value.intValue()) == 0) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("This Fuel has already been registered with the key: " + key));
        }
    }
}
