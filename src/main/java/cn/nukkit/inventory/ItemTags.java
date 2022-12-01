package cn.nukkit.inventory;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemTags {
    private static final Map<String, List<Item>> MAP = new HashMap<>();

    static {
        var planks = new ArrayList<Item>();
        for (int i = 0; i <= 5; ++i) {
            planks.add(Item.getBlock(BlockID.PLANKS, i));
        }
        planks.add(Item.getBlock(BlockID.MANGROVE_PLANKS));
        planks.add(Item.getBlock(BlockID.WARPED_PLANKS));
        planks.add(Item.getBlock(BlockID.CRIMSON_PLANKS));
        MAP.put("minecraft:planks", planks);

        var slab = new ArrayList<Item>();
        for (int i = 0; i <= 5; ++i) {
            slab.add(Item.getBlock(BlockID.WOOD_SLAB, i));
        }
        slab.add(Item.getBlock(BlockID.MANGROVE_SLAB));
        slab.add(Item.getBlock(BlockID.WARPED_SLAB));
        slab.add(Item.getBlock(BlockID.CRIMSON_SLAB));
        MAP.put("minecraft:wooden_slabs", slab);

        var coals = new ArrayList<Item>();
        coals.add(Item.get(ItemID.COAL, 0));
        coals.add(Item.get(ItemID.COAL, 1));
        MAP.put("minecraft:coals", coals);

        var stone_crafting_materials = new ArrayList<Item>();
        stone_crafting_materials.add(Item.getBlock(BlockID.COBBLESTONE));
        stone_crafting_materials.add(Item.getBlock(BlockID.BLACKSTONE));
        stone_crafting_materials.add(Item.getBlock(BlockID.COBBLED_DEEPSLATE));
        MAP.put("minecraft:stone_crafting_materials", stone_crafting_materials);
        MAP.put("minecraft:stone_tool_materials", stone_crafting_materials);

        var logs = new ArrayList<Item>();
        //logs_that_burn
        for (int i = 0; i <= 3; ++i) {
            logs.add(Item.getBlock(BlockID.LOG, i));
        }
        for (int i = 0; i <= 1; ++i) {
            logs.add(Item.getBlock(BlockID.LOG2, i));
        }
        logs.add(Item.getBlock(BlockID.MANGROVE_WOOD));
        //crimson_stems
        logs.add(Item.getBlock(BlockID.STRIPPED_CRIMSON_HYPHAE));
        logs.add(Item.getBlock(BlockID.CRIMSON_HYPHAE));
        logs.add(Item.getBlock(BlockID.STRIPPED_CRIMSON_STEM));
        logs.add(Item.getBlock(BlockID.CRIMSON_STEM));
        //warped_stems
        logs.add(Item.getBlock(BlockID.STRIPPED_WARPED_HYPHAE));
        logs.add(Item.getBlock(BlockID.WARPED_HYPHAE));
        logs.add(Item.getBlock(BlockID.STRIPPED_WARPED_STEM));
        logs.add(Item.getBlock(BlockID.WARPED_STEM));
        MAP.put("minecraft:logs", logs);

        var wool = new ArrayList<Item>();
        for (int i = 0; i <= 15; ++i) {
            wool.add(Item.getBlock(BlockID.WOOL, i));
        }
        MAP.put("minecraft:wool", wool);

        var soul_fire_base_blocks = new ArrayList<Item>();
        wool.add(Item.getBlock(BlockID.SOUL_SAND));
        wool.add(Item.getBlock(BlockID.SOUL_SOIL));
        MAP.put("minecraft:soul_fire_base_blocks", soul_fire_base_blocks);
    }

    public void registerTags(String key, List<Item> value) {
        MAP.put(key, value);
    }

    public Map<String, List<Item>> getMapping() {
        return new HashMap<>(MAP);
    }

    public List<Item> fromTags(String tags) {
        return MAP.get(tags);
    }
}
