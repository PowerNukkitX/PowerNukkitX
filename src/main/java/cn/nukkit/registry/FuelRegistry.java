package cn.nukkit.registry;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class FuelRegistry implements IRegistry<Item, Integer, Integer> {
    private static final Object2IntOpenHashMap<String> REGISTRY = new Object2IntOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);
    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        register0(BlockID.COAL_BLOCK, 16000);
        register0(ItemID.COAL, 1600);
        register0(BlockID.WOOD, 300);

        register0(BlockID.ACACIA_PLANKS, 300);
        register0(BlockID.BAMBOO_PLANKS, 300);
        register0(BlockID.BIRCH_PLANKS, 300);
        register0(BlockID.CHERRY_PLANKS, 300);
        register0(BlockID.CRIMSON_PLANKS, 300);
        register0(BlockID.JUNGLE_PLANKS, 300);
        register0(BlockID.OAK_PLANKS, 300);
        register0(BlockID.WARPED_PLANKS, 300);
        register0(BlockID.MANGROVE_PLANKS, 300);
        register0(BlockID.SPRUCE_PLANKS, 300);
        register0(BlockID.DARK_OAK_PLANKS, 300);

        register0(BlockID.ACACIA_FENCE, 300);
        register0(BlockID.BAMBOO_FENCE, 300);
        register0(BlockID.BIRCH_FENCE, 300);
        register0(BlockID.CHERRY_FENCE, 300);
        register0(BlockID.CRIMSON_FENCE, 300);
        register0(BlockID.JUNGLE_FENCE, 300);
        register0(BlockID.OAK_FENCE, 300);
        register0(BlockID.WARPED_FENCE, 300);
        register0(BlockID.MANGROVE_FENCE, 300);
        register0(BlockID.SPRUCE_FENCE, 300);
        register0(BlockID.DARK_OAK_FENCE, 300);

        register0(BlockID.ACACIA_FENCE_GATE, 300);
        register0(BlockID.BAMBOO_FENCE_GATE, 300);
        register0(BlockID.BIRCH_FENCE_GATE, 300);
        register0(BlockID.CHERRY_FENCE_GATE, 300);
        register0(BlockID.CRIMSON_FENCE_GATE, 300);
        register0(BlockID.JUNGLE_FENCE_GATE, 300);
        register0(BlockID.FENCE_GATE, 300);
        register0(BlockID.WARPED_FENCE_GATE, 300);
        register0(BlockID.MANGROVE_FENCE_GATE, 300);
        register0(BlockID.SPRUCE_FENCE_GATE, 300);
        register0(BlockID.DARK_OAK_FENCE_GATE, 300);

        register0(BlockID.OAK_STAIRS, 300);
        register0(BlockID.ACACIA_STAIRS, 300);
        register0(BlockID.BAMBOO_STAIRS, 300);
        register0(BlockID.BIRCH_STAIRS, 300);
        register0(BlockID.CHERRY_STAIRS, 300);
        register0(BlockID.CRIMSON_STAIRS, 300);
        register0(BlockID.JUNGLE_STAIRS, 300);
        register0(BlockID.WARPED_STAIRS, 300);
        register0(BlockID.MANGROVE_STAIRS, 300);
        register0(BlockID.SPRUCE_STAIRS, 300);
        register0(BlockID.DARK_OAK_STAIRS, 300);

        register0(ItemID.ACACIA_SIGN, 200);
        register0(ItemID.BAMBOO_SIGN, 200);
        register0(ItemID.BIRCH_SIGN, 200);
        register0(ItemID.CHERRY_SIGN, 200);
        register0(ItemID.CRIMSON_SIGN, 200);
        register0(ItemID.JUNGLE_SIGN, 200);
        register0(ItemID.WARPED_SIGN, 200);
        register0(ItemID.MANGROVE_SIGN, 200);
        register0(ItemID.SPRUCE_SIGN, 200);
        register0(ItemID.DARK_OAK_SIGN, 200);

        register0(BlockID.SAPLING, 100);
        register0(ItemID.STICK, 100);
        register0(BlockID.AZALEA, 100);
        register0(ItemID.BOWL, 100);

        register0(ItemID.WOODEN_AXE, 200);
        register0(ItemID.WOODEN_PICKAXE, 200);
        register0(ItemID.WOODEN_SWORD, 200);
        register0(ItemID.WOODEN_SHOVEL, 200);
        register0(ItemID.WOODEN_HOE, 200);
        register0(ItemID.BOW, 200);

        register0(BlockID.TRAPDOOR, 300);
        register0(BlockID.CRAFTING_TABLE, 300);
        register0(BlockID.BOOKSHELF, 300);
        register0(BlockID.CHEST, 300);
        register0(BlockID.LADDER, 300);
        register0(ItemID.LAVA_BUCKET, 20000);

        register0(BlockID.CHERRY_WOOD, 300);
        register0(BlockID.MANGROVE_WOOD, 300);
        register0(BlockID.STRIPPED_CHERRY_WOOD, 300);
        register0(BlockID.STRIPPED_MANGROVE_WOOD, 300);
        register0(BlockID.WOODEN_PRESSURE_PLATE, 300);
        register0(BlockID.TRAPPED_CHEST, 300);
        register0(BlockID.DAYLIGHT_DETECTOR, 300);
        register0(BlockID.DAYLIGHT_DETECTOR_INVERTED, 300);
        register0(BlockID.JUKEBOX, 300);
        register0(BlockID.NOTEBLOCK, 300);
        register0(BlockID.WOODEN_SLAB, 300);
        register0(BlockID.DOUBLE_WOODEN_SLAB, 300);
        register0(ItemID.BOAT, 1200);
        register0(ItemID.BLAZE_ROD, 2400);
        register0(BlockID.BROWN_MUSHROOM_BLOCK, 300);
        register0(BlockID.RED_MUSHROOM_BLOCK, 300);
        register0(ItemID.FISHING_ROD, 300);
        register0(BlockID.WOODEN_BUTTON, 100);
        register0(ItemID.WOODEN_DOOR, 200);
        register0(ItemID.SPRUCE_DOOR, 200);
        register0(ItemID.BIRCH_DOOR, 200);
        register0(ItemID.JUNGLE_DOOR, 200);
        register0(ItemID.ACACIA_DOOR, 200);
        register0(ItemID.DARK_OAK_DOOR, 200);
        register0(ItemID.BANNER, 300);
        register0(BlockID.DEADBUSH, 100);
        register0(BlockID.DRIED_KELP_BLOCK, 4000);
        register0(ItemID.CROSSBOW, 200);
        register0(BlockID.BEE_NEST, 300);
        register0(BlockID.BEEHIVE, 300);
        register0(BlockID.BAMBOO, 50);
        register0(BlockID.SCAFFOLDING, 50);
        register0(BlockID.CARTOGRAPHY_TABLE, 300);
        register0(BlockID.FLETCHING_TABLE, 300);
        register0(BlockID.SMITHING_TABLE, 300);
        register0(BlockID.LOOM, 300);
        register0(BlockID.LECTERN, 300);
        register0(BlockID.COMPOSTER, 300);
        register0(BlockID.BARREL, 300);
    }

    @Override
    public Integer get(Item key) {
        String hash;
        if (key.isBlock()) hash = key.getBlockId() + "#" + key.getDamage();
        else hash = key.getId() + "#" + key.getDamage();
        return REGISTRY.get(hash);
    }

    /**
     * @param item item
     * @return fuel duration, if it cannot be used as fuel, return -1.
     */
    public int getFuelDuration(@NotNull Item item) {
        int id = REGISTRY.getInt(item.getId() + "#" + item.getDamage());
        if (id == 0 && item.isBlock()) {
            id = REGISTRY.getInt(item.getBlockId() + "#" + item.getDamage());
        }
        return id;
    }

    public boolean isFuel(@NotNull Item item) {
        boolean b = REGISTRY.containsKey(item.getId() + "#" + item.getDamage());
        if (!b && item.isBlock()) {
            b = REGISTRY.containsKey(item.getBlockId() + "#" + item.getDamage());
        }
        return b;
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public void register(Item key, Integer value) throws RegisterException {
        String hash;
        if (key.isBlock()) hash = key.getBlockId() + "#" + key.getDamage();
        else hash = key.getId() + "#" + key.getDamage();
        if (REGISTRY.putIfAbsent(hash, value.intValue()) == 0) {
        } else {
            throw new RegisterException("This Fuel has already been registered with the key: " + key);
        }
    }

    private void register0(String key, Integer value) {
        try {
            if (REGISTRY.putIfAbsent(key + "#0", value.intValue()) == 0) {
            } else {
                throw new RegisterException("This Fuel has already been registered with the key: " + key);
            }
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    private void register1(String key, int meta, Integer value) {
        try {
            if (REGISTRY.putIfAbsent(key + "#" + meta, value.intValue()) == 0) {
            } else {
                throw new RegisterException("This Fuel has already been registered with the key: " + key);
            }
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
