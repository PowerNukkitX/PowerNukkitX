package cn.nukkit.registry;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Allay Project 12/21/2023
 *
 * @author Cool_Loong
 */
@Slf4j
public class CreativeItemRegistry implements ItemID, IRegistry<Integer, Item, Item> {
    private static final Int2ObjectLinkedOpenHashMap<Item> MAP = new Int2ObjectLinkedOpenHashMap<>();

    @Override
    public void init() {
        try (var input = CreativeItemRegistry.class.getClassLoader().getResourceAsStream("creative_items.nbt")) {
            CompoundTag compoundTag = NBTIO.readCompressed(input);
            TreeMap<Integer, Tag> tagTreeMap = new TreeMap<>();
            compoundTag.getTags().forEach((key, value) -> tagTreeMap.put(Integer.parseInt(key), value));

            for (var entry : tagTreeMap.entrySet()) {
                int index = entry.getKey();
                CompoundTag tag = (CompoundTag) entry.getValue();
                int damage = tag.getInt("damage");
                if (tag.containsInt("blockStateHash")) {
                    int blockStateHash = tag.getInt("blockStateHash");
                    BlockState blockState = Registries.BLOCKSTATE.get(blockStateHash);
                    if (blockState == null) {
                        log.warn("Item {} block state Hash cannot be found!", tag.getString("name"));
                        continue;
                    }
                    Block block = Registries.BLOCK.get(blockState);
                    register(index, new ItemBlock(block, damage));
                } else {
                    String name = tag.getString("name");
                    Item item = Item.get(name, damage);
                    register(index, item);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RegisterException e) {
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
        try {
            this.register(i + 1, item.clone());
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
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
    public void register(Integer key, Item value) throws RegisterException {
        if (MAP.putIfAbsent(key, value) == null) {
        } else {
            throw new RegisterException("This creative item has already been registered with the identifier: " + key);
        }
    }
}
