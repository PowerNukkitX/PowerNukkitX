package cn.nukkit.registry;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAcaciaSlab;
import cn.nukkit.block.BlockBirchSlab;
import cn.nukkit.block.BlockDarkOakSlab;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockJungleSlab;
import cn.nukkit.block.BlockSpruceSlab;
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
        register0(ItemID.CHARCOAL, 1600);
        register0(ItemID.WOOD, 300);

        register0(BlockID.WHITE_WOOL, 100);
        register0(BlockID.LIGHT_GRAY_WOOL, 100);
        register0(BlockID.GRAY_WOOL, 100);
        register0(BlockID.BLACK_WOOL, 100);
        register0(BlockID.BROWN_WOOL, 100);
        register0(BlockID.RED_WOOL, 100);
        register0(BlockID.ORANGE_WOOL, 100);
        register0(BlockID.YELLOW_WOOL, 100);
        register0(BlockID.LIME_WOOL, 100);
        register0(BlockID.GREEN_WOOL, 100);
        register0(BlockID.CYAN_WOOL, 100);
        register0(BlockID.LIGHT_BLUE_WOOL, 100);
        register0(BlockID.BLUE_WOOL, 100);
        register0(BlockID.PURPLE_WOOL, 100);
        register0(BlockID.MAGENTA_WOOL, 100);
        register0(BlockID.PINK_WOOL, 100);

        register0(BlockID.WHITE_CARPET, 100);
        register0(BlockID.LIGHT_GRAY_CARPET, 100);
        register0(BlockID.GRAY_CARPET, 100);
        register0(BlockID.BLACK_CARPET, 100);
        register0(BlockID.BROWN_CARPET, 100);
        register0(BlockID.RED_CARPET, 100);
        register0(BlockID.ORANGE_CARPET, 100);
        register0(BlockID.YELLOW_CARPET, 100);
        register0(BlockID.LIME_CARPET, 100);
        register0(BlockID.GREEN_CARPET, 100);
        register0(BlockID.CYAN_CARPET, 100);
        register0(BlockID.LIGHT_BLUE_CARPET, 100);
        register0(BlockID.BLUE_CARPET, 100);
        register0(BlockID.PURPLE_CARPET, 100);
        register0(BlockID.MAGENTA_CARPET, 100);
        register0(BlockID.PINK_CARPET, 100);

        register0(BlockID.OAK_LOG, 300);
        register0(BlockID.DARK_OAK_LOG, 300);
        register0(BlockID.BIRCH_LOG, 300);
        register0(BlockID.SPRUCE_LOG, 300);
        register0(BlockID.JUNGLE_LOG, 300);
        register0(BlockID.ACACIA_LOG, 300);
        register0(BlockID.MANGROVE_LOG, 300);
        register0(BlockID.CHERRY_LOG, 300);

        register0(BlockID.STRIPPED_OAK_LOG, 300);
        register0(BlockID.STRIPPED_DARK_OAK_LOG, 300);
        register0(BlockID.STRIPPED_BIRCH_LOG, 300);
        register0(BlockID.STRIPPED_SPRUCE_LOG, 300);
        register0(BlockID.STRIPPED_JUNGLE_LOG, 300);
        register0(BlockID.STRIPPED_ACACIA_LOG, 300);
        register0(BlockID.STRIPPED_MANGROVE_LOG, 300);
        register0(BlockID.STRIPPED_CHERRY_LOG, 300);

        register0(BlockID.ACACIA_PLANKS, 300);
        register0(BlockID.BAMBOO_PLANKS, 300);
        register0(BlockID.BIRCH_PLANKS, 300);
        register0(BlockID.CHERRY_PLANKS, 300);
        register0(BlockID.JUNGLE_PLANKS, 300);
        register0(BlockID.OAK_PLANKS, 300);
        register0(BlockID.MANGROVE_PLANKS, 300);
        register0(BlockID.SPRUCE_PLANKS, 300);
        register0(BlockID.DARK_OAK_PLANKS, 300);

        register0(BlockID.ACACIA_FENCE, 300);
        register0(BlockID.BAMBOO_FENCE, 300);
        register0(BlockID.BIRCH_FENCE, 300);
        register0(BlockID.CHERRY_FENCE, 300);
        register0(BlockID.JUNGLE_FENCE, 300);
        register0(BlockID.OAK_FENCE, 300);
        register0(BlockID.MANGROVE_FENCE, 300);
        register0(BlockID.SPRUCE_FENCE, 300);
        register0(BlockID.DARK_OAK_FENCE, 300);

        register0(BlockID.ACACIA_FENCE_GATE, 300);
        register0(BlockID.BAMBOO_FENCE_GATE, 300);
        register0(BlockID.BIRCH_FENCE_GATE, 300);
        register0(BlockID.CHERRY_FENCE_GATE, 300);
        register0(BlockID.JUNGLE_FENCE_GATE, 300);
        register0(BlockID.FENCE_GATE, 300);
        register0(BlockID.MANGROVE_FENCE_GATE, 300);
        register0(BlockID.SPRUCE_FENCE_GATE, 300);
        register0(BlockID.DARK_OAK_FENCE_GATE, 300);

        register0(BlockID.OAK_STAIRS, 300);
        register0(BlockID.ACACIA_STAIRS, 300);
        register0(BlockID.BAMBOO_STAIRS, 300);
        register0(BlockID.BIRCH_STAIRS, 300);
        register0(BlockID.CHERRY_STAIRS, 300);
        register0(BlockID.JUNGLE_STAIRS, 300);
        register0(BlockID.MANGROVE_STAIRS, 300);
        register0(BlockID.SPRUCE_STAIRS, 300);
        register0(BlockID.DARK_OAK_STAIRS, 300);
        register0(BlockID.BAMBOO_MOSAIC_STAIRS, 300);

        register0(ItemID.ACACIA_SIGN, 200);
        register0(ItemID.BAMBOO_SIGN, 200);
        register0(ItemID.BIRCH_SIGN, 200);
        register0(ItemID.CHERRY_SIGN, 200);
        register0(ItemID.JUNGLE_SIGN, 200);
        register0(ItemID.MANGROVE_SIGN, 200);
        register0(ItemID.SPRUCE_SIGN, 200);
        register0(ItemID.DARK_OAK_SIGN, 200);
        register0(ItemID.OAK_SIGN, 200);

        register0(Block.ACACIA_HANGING_SIGN, 200);
        register0(Block.BAMBOO_HANGING_SIGN, 200);
        register0(Block.BIRCH_HANGING_SIGN, 200);
        register0(Block.CHERRY_HANGING_SIGN, 200);
        register0(Block.JUNGLE_HANGING_SIGN, 200);
        register0(Block.MANGROVE_HANGING_SIGN, 200);
        register0(Block.SPRUCE_HANGING_SIGN, 200);
        register0(Block.DARK_OAK_HANGING_SIGN, 200);
        register0(Block.OAK_HANGING_SIGN, 200);

        register0(BlockID.WOODEN_PRESSURE_PLATE, 300);
        register0(BlockID.SPRUCE_PRESSURE_PLATE, 300);
        register0(BlockID.BIRCH_PRESSURE_PLATE, 300);
        register0(BlockID.JUNGLE_PRESSURE_PLATE, 300);
        register0(BlockID.ACACIA_PRESSURE_PLATE, 300);
        register0(BlockID.DARK_OAK_PRESSURE_PLATE, 300);
        register0(BlockID.MANGROVE_PRESSURE_PLATE, 300);
        register0(BlockID.CHERRY_PRESSURE_PLATE, 300);
        register0(BlockID.BAMBOO_PRESSURE_PLATE, 300);

        register0(BlockID.ACACIA_SAPLING,100);
        register0(BlockID.CHERRY_SAPLING,100);
        register0(BlockID.SPRUCE_SAPLING,100);
        register0(BlockID.BAMBOO_SAPLING,100);
        register0(BlockID.OAK_SAPLING,100);
        register0(BlockID.JUNGLE_SAPLING,100);
        register0(BlockID.DARK_OAK_SAPLING,100);
        register0(BlockID.BIRCH_SAPLING,100);

        register0(ItemID.STICK, 100);
        register0(BlockID.AZALEA, 100);
        register0(ItemID.BOWL, 100);

        register0(ItemID.WOODEN_AXE, 200);
        register0(ItemID.WOODEN_PICKAXE, 200);
        register0(ItemID.WOODEN_SWORD, 200);
        register0(ItemID.WOODEN_SHOVEL, 200);
        register0(ItemID.WOODEN_HOE, 200);
        register0(ItemID.BOW, 200);

        register0(BlockID.BOOKSHELF, 300);
        register0(BlockID.CHEST, 300);
        register0(BlockID.LADDER, 300);
        register0(ItemID.LAVA_BUCKET, 20000);

        register0(BlockID.CHERRY_WOOD, 300);
        register0(BlockID.MANGROVE_WOOD, 300);
        register0(BlockID.STRIPPED_CHERRY_WOOD, 300);
        register0(BlockID.STRIPPED_MANGROVE_WOOD, 300);
        register0(BlockID.TRAPPED_CHEST, 300);
        register0(BlockID.DAYLIGHT_DETECTOR, 300);
        register0(BlockID.DAYLIGHT_DETECTOR_INVERTED, 300);
        register0(BlockID.JUKEBOX, 300);
        register0(BlockID.NOTEBLOCK, 300);

        register0(ItemID.WOODEN_SLAB, 300);
        register0(BlockID.BAMBOO_MOSAIC_SLAB, 300);
        register0(BlockID.BAMBOO_SLAB, 300);
        register0(BlockID.CHERRY_SLAB, 300);
        register0(BlockID.MANGROVE_SLAB, 300);
        register0(BlockAcaciaSlab.PROPERTIES.getDefaultState().toItem(), 300);
        register0(BlockJungleSlab.PROPERTIES.getDefaultState().toItem(), 300);
        register0(BlockSpruceSlab.PROPERTIES.getDefaultState().toItem(), 300);
        register0(BlockBirchSlab.PROPERTIES.getDefaultState().toItem(), 300);
        register0(BlockDarkOakSlab.PROPERTIES.getDefaultState().toItem(), 300);

        register0(BlockID.ACACIA_DOUBLE_SLAB,300);
        register0(BlockID.OAK_DOUBLE_SLAB,300);
        register0(BlockID.SPRUCE_DOUBLE_SLAB,300);
        register0(BlockID.DARK_OAK_DOUBLE_SLAB,300);
        register0(BlockID.BAMBOO_DOUBLE_SLAB,300);
        register0(BlockID.CRIMSON_DOUBLE_SLAB,300);
        register0(BlockID.CHERRY_DOUBLE_SLAB,300);
        register0(BlockID.BIRCH_DOUBLE_SLAB,300);

        register0(ItemID.BLAZE_ROD, 2400);
        register0(BlockID.BROWN_MUSHROOM_BLOCK, 300);
        register0(BlockID.RED_MUSHROOM_BLOCK, 300);
        register0(ItemID.FISHING_ROD, 300);

        register0(ItemID.OAK_BOAT, 1200);
        register0(ItemID.BOAT, 1200);
        register0(ItemID.BIRCH_BOAT, 1200);
        register0(ItemID.SPRUCE_BOAT, 1200);
        register0(ItemID.JUNGLE_BOAT, 1200);
        register0(ItemID.ACACIA_BOAT, 1200);
        register0(ItemID.DARK_OAK_BOAT, 1200);
        register0(ItemID.MANGROVE_BOAT, 1200);
        register0(ItemID.CHERRY_BOAT, 1200);
        register0(ItemID.BAMBOO_RAFT, 1200);

        register0(ItemID.OAK_CHEST_BOAT, 1200);
        register0(ItemID.CHEST_BOAT, 1200);
        register0(ItemID.BIRCH_CHEST_BOAT, 1200);
        register0(ItemID.SPRUCE_CHEST_BOAT, 1200);
        register0(ItemID.JUNGLE_CHEST_BOAT, 1200);
        register0(ItemID.ACACIA_CHEST_BOAT, 1200);
        register0(ItemID.DARK_OAK_CHEST_BOAT, 1200);
        register0(ItemID.MANGROVE_CHEST_BOAT, 1200);
        register0(ItemID.CHERRY_CHEST_BOAT, 1200);
        register0(ItemID.BAMBOO_CHEST_RAFT, 1200);

        register0(BlockID.WOODEN_BUTTON, 100);
        register0(BlockID.SPRUCE_BUTTON, 100);
        register0(BlockID.BIRCH_BUTTON, 100);
        register0(BlockID.JUNGLE_BUTTON, 100);
        register0(BlockID.ACACIA_BUTTON, 100);
        register0(BlockID.DARK_OAK_BUTTON, 100);
        register0(BlockID.MANGROVE_BUTTON, 100);
        register0(BlockID.CHERRY_BUTTON, 100);
        register0(BlockID.BAMBOO_BUTTON, 100);

        register0(BlockID.WOODEN_DOOR, 200);
        register0(BlockID.SPRUCE_DOOR, 200);
        register0(BlockID.BIRCH_DOOR, 200);
        register0(BlockID.JUNGLE_DOOR, 200);
        register0(BlockID.ACACIA_DOOR, 200);
        register0(BlockID.DARK_OAK_DOOR, 200);
        register0(BlockID.BAMBOO_DOOR, 200);
        register0(BlockID.MANGROVE_DOOR, 200);
        register0(BlockID.CHERRY_DOOR, 200);

        register0(BlockID.TRAPDOOR, 300);
        register0(BlockID.SPRUCE_TRAPDOOR, 300);
        register0(BlockID.BIRCH_TRAPDOOR, 300);
        register0(BlockID.JUNGLE_TRAPDOOR, 300);
        register0(BlockID.ACACIA_TRAPDOOR, 300);
        register0(BlockID.DARK_OAK_TRAPDOOR, 300);
        register0(BlockID.MANGROVE_TRAPDOOR, 300);
        register0(BlockID.CHERRY_TRAPDOOR, 300);
        register0(BlockID.BAMBOO_TRAPDOOR, 300);

        register0(BlockID.CARTOGRAPHY_TABLE, 300);
        register0(BlockID.FLETCHING_TABLE, 300);
        register0(BlockID.SMITHING_TABLE, 300);
        register0(BlockID.CRAFTING_TABLE, 300);

        register0(ItemID.BANNER, 300);
        register0(BlockID.DEADBUSH, 100);
        register0(BlockID.DRIED_KELP_BLOCK, 4000);
        register0(ItemID.CROSSBOW, 200);
        register0(BlockID.BEE_NEST, 300);
        register0(BlockID.BEEHIVE, 300);
        register0(BlockID.BAMBOO, 50);
        register0(BlockID.STRIPPED_BAMBOO_BLOCK, 300);
        register0(BlockID.BAMBOO_BLOCK, 300);
        register0(BlockID.MANGROVE_ROOTS, 300);
        register0(BlockID.SCAFFOLDING, 50);
        register0(BlockID.LOOM, 300);
        register0(BlockID.LECTERN, 300);
        register0(BlockID.COMPOSTER, 300);
        register0(BlockID.BARREL, 300);
    }

    @Override
    public Integer get(Item key) {
        String hash;
        if (key.isBlock()) hash = String.valueOf(key.getBlockUnsafe().getRuntimeId());
        else hash = key.getId() + "#" + key.getDamage();
        return REGISTRY.getInt(hash);
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
    public void reload() {
        isLoad.set(false);
        REGISTRY.clear();
        init();
    }

    @Override
    public void register(Item key, Integer value) throws RegisterException {
        String hash;
        if (key.isBlock()) hash = String.valueOf(key.getBlockUnsafe().getRuntimeId());
        else hash = key.getId() + "#" + key.getDamage();
        if (REGISTRY.putIfAbsent(hash, value.intValue()) == 0) {
        } else {
            throw new RegisterException("This Fuel has already been registered with the key: " + key);
        }
    }

    private void register0(Item key, Integer value) {
        try {
            register(key, value);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
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
