package cn.nukkit.registry;

import cn.nukkit.block.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.gson.Gson;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Allay Project 12/21/2023
 *
 * @author Cool_Loong
 */
@Slf4j
public class CreativeItemRegistry implements ItemID, IRegistry<Integer, Item, Item> {
    static final Int2ObjectLinkedOpenHashMap<Item> MAP = new Int2ObjectLinkedOpenHashMap<>();
    static final Int2ObjectOpenHashMap<Item> INTERNAL_DIFF_ITEM = new Int2ObjectOpenHashMap<>();
    static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try (var input = CreativeItemRegistry.class.getClassLoader().getResourceAsStream("creative_items.json")) {
            Map data = new Gson().fromJson(new InputStreamReader(input), Map.class);
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
            for (int i = 0; i < items.size(); i++) {
                Map<String, Object> tag = items.get(i);
                int damage = ((Number) tag.getOrDefault("damage", 0)).intValue();
                var nbt = tag.containsKey("nbt_b64") ? Base64.getDecoder().decode(tag.get("nbt_b64").toString()) : EmptyArrays.EMPTY_BYTES;
                String name = tag.get("id").toString();
                Item item = Item.get(name, damage, 1, nbt, false);
                item.setCompoundTag(nbt);
                if (item.isNull() || (item.isBlock() && item.getBlockUnsafe().isAir())) {
                    item = Item.AIR;
                    log.warn("load creative item {} damage {} is null", name, damage);
                }
                var isBlock = tag.containsKey("block_state_b64");
                if (isBlock) {
                    byte[] blockTag = Base64.getDecoder().decode(tag.get("block_state_b64").toString());
                    CompoundTag blockCompoundTag = NBTIO.read(blockTag, ByteOrder.LITTLE_ENDIAN);
                    int blockHash = blockCompoundTag.getInt("network_id");
                    BlockState block = Registries.BLOCKSTATE.get(blockHash);
                    if (block == null) {
                        item = Item.AIR;
                        log.warn("load creative item {} blockHash {} is null", name, blockHash);
                    } else {
                        item.setBlockUnsafe(block.toBlock());
                        Item updateDamage = block.toBlock().toItem();
                        if (updateDamage.getDamage() != 0) {
                            item.setDamage(updateDamage.getDamage());
                        }
                    }
                } else {
                    INTERNAL_DIFF_ITEM.put(i, item.clone());
                    item.setBlockUnsafe(null);
                }
                register(i, item);
            }
        } catch (IOException | RegisterException e) {
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

    @NotNull
    public Item getCreativeItem(int index) {
        if (INTERNAL_DIFF_ITEM.containsKey(index)) {
            return INTERNAL_DIFF_ITEM.get(index);
        }
        return (index >= 0 && index < MAP.size()) ? MAP.get(index) : Item.AIR;
    }

    /**
     * 取消创造模式下创造背包中的物品
     * <p>
     * Cancel the Creative of items in the backpack in Creative mode
     */

    public void clearCreativeItems() {
        MAP.clear();
        INTERNAL_DIFF_ITEM.clear();
    }

    /**
     * Get all creative items
     */
    public Item[] getCreativeItems() {
        return MAP.values().toArray(Item[]::new);
    }

    /**
     * Add an item to {@link CreativeItemRegistry}
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
            INTERNAL_DIFF_ITEM.remove(index);
        }
    }

    /**
     * 检测这个物品是否存在于创造背包
     * <p>
     * Detect if the item exists in the Creative backpack
     */
    public boolean isCreativeItem(Item item) {
        for (Item aCreative : INTERNAL_DIFF_ITEM.values()) {
            if (item.equals(aCreative, !item.isTool())) {
                return true;
            }
        }
        for (Item aCreative : MAP.values()) {
            if (item.equals(aCreative, !item.isTool())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Item get(Integer key) {
        if (INTERNAL_DIFF_ITEM.containsKey(key.intValue())) {
            return INTERNAL_DIFF_ITEM.get(key.intValue());
        }
        return MAP.get(key.intValue());
    }

    @Override
    public void trim() {
        MAP.trim();
        INTERNAL_DIFF_ITEM.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        MAP.clear();
        INTERNAL_DIFF_ITEM.clear();
        init();
    }

    @Override
    public void register(Integer key, Item value) throws RegisterException {
        if (MAP.putIfAbsent(key, value) != null) {
            throw new RegisterException("This creative item has already been registered with the identifier: " + key);
        }
    }
}
