package cn.nukkit.registry;

import cn.nukkit.block.Block;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;

import java.io.IOException;
import java.util.Map;

/**
 * Allay Project 12/21/2023
 *
 * @author Cool_Loong
 */
public class CreativeItemRegistry extends BaseRegistry<Integer, Item, Item> implements ItemID {
    private static final Int2ObjectLinkedOpenHashMap<Item> MAP = new Int2ObjectLinkedOpenHashMap<>();

    @Override
    public void init() {
        try (var input = CreativeItemRegistry.class.getClassLoader().getResourceAsStream("creative_items.nbt")) {
            CompoundTag compoundTag = NBTIO.readCompressed(input);
            Map<String, Tag> tags = compoundTag.getTags();
            for (var entry : tags.entrySet()) {
                String index = entry.getKey();
                CompoundTag tag = (CompoundTag) entry.getValue();
                int damage = tag.getInt("damage");
                if (tag.containsInt("blockStateHash")) {
                    int blockStateHash = tag.getInt("blockStateHash");
                    BlockState blockState = Registries.BLOCKSTATE.get(blockStateHash);
                    Block block = Registries.BLOCK.get(blockState);
                    register(Integer.parseInt(index), new ItemBlock(block, damage));
                } else {
                    String name = tag.getString("name");
                    Item item = Item.get(name, damage);
                    register(Integer.parseInt(index), item);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取指定物品在{@link CreativeItemRegistry}中的索引
     * <p>
     * Get the index of the specified item in {@link CreativeItemRegistry}
     *
     * @param item 指定物品 <br>specified item
     * @return Unable to find return -1
     */
    public int getCreativeItemIndex(Item item) {
        for (int i = 0; i < MAP.size(); i++) {
            if (item.equals(MAP.get(i), !item.isTool())) {
                return i;
            }
        }
        return -1;
    }

    public Item getCreativeItem(int index) {
        return (index >= 0 && index < MAP.size()) ? MAP.get(index) : null;
    }

    /**
     * 取消创造模式下创造背包中的物品
     * <p>
     * Cancel the Creative of items in the backpack in Creative mode
     */

    public void clearCreativeItems() {
        MAP.clear();
    }

    /**
     * Get all creative items
     */
    public Item[] getCreativeItems() {
        return MAP.values().toArray(Item[]::new);
    }

    /**
     * Add a item to {@link CreativeItemRegistry}
     */
    public void addCreativeItem(Item item) {
        int i = MAP.lastIntKey();
        this.register(i + 1, item.clone());
    }

    /**
     * 移除一个指定的创造物品
     * <p>
     * Remove a specified created item
     */
    public void removeCreativeItem(Item item) {
        int index = getCreativeItemIndex(item);
        if (index != -1) {
            int lastIntKey = MAP.lastIntKey();
            for (var i = index; i < lastIntKey; ++i) {
                MAP.put(i, MAP.get(i + 1));
            }
            MAP.remove(lastIntKey);
        }
    }

    /**
     * 检测这个物品是否存在于创造背包
     * <p>
     * Detect if the item exists in the Creative backpack
     */
    public boolean isCreativeItem(Item item) {
        for (Item aCreative : MAP.values()) {
            if (item.equals(aCreative, !item.isTool())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Item get(Integer key) {
        return MAP.get(key.intValue());
    }

    @Override
    public void trim() {
        MAP.trim();
    }

    @Override
    public OK<?> register(Integer key, Item value) {
        if (MAP.putIfAbsent(key, value) == null) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("This creative item has already been registered with the identifier: " + key));
        }
    }
}
