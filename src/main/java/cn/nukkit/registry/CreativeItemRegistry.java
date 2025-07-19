package cn.nukkit.registry;

import cn.nukkit.block.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.customitem.data.CreativeCategory;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemData;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemGroup;
import com.google.gson.Gson;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.HashMap;
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

    static final ObjectLinkedOpenHashSet<CreativeItemGroup> GROUPS = new ObjectLinkedOpenHashSet<>();
    static final ObjectLinkedOpenHashSet<CreativeItemData> ITEM_DATA = new ObjectLinkedOpenHashSet<>();
    public static final Map<String, String> ITEM_GROUP_MAP = new HashMap<>();
    static final Map<CreativeCategory, Map<String, Integer>> CATEGORY_GROUP_INDEX_MAP = new HashMap<>();

    public static int LAST_CONSTRUCTION_INDEX = -1;
    public static int LAST_EQUIPMENTS_INDEX = -1;
    public static int LAST_ITEMS_INDEX = -1;
    public static int LAST_NATURE_INDEX = -1;

    @Override
    public void init() {
        if (isLoad.getAndSet(true))
            return;

        try (var input = CreativeItemRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/creative_items.json")) {
            Map data = new Gson().fromJson(new InputStreamReader(input), Map.class);
            List<Map<String, Object>> groups = (List<Map<String, Object>>) data.get("groups");
            int index = 0;
            for (Map<String, Object> tag : groups) {
                int creativeCategory = ((Number) tag.getOrDefault("creative_category", 0)).intValue();
                String name = (String) tag.get("name");
                Map iconMap = (Map) tag.get("icon");
                Item icon = Item.get((String) iconMap.get("id"));
                CreativeItemGroup group = new CreativeItemGroup(CreativeItemCategory.VALUES[creativeCategory], name, icon);
                GROUPS.add(group);

                CreativeCategory category = CreativeCategory.fromID(creativeCategory);
                CATEGORY_GROUP_INDEX_MAP.computeIfAbsent(category, k -> new HashMap<>());
                CATEGORY_GROUP_INDEX_MAP.get(category).put(name, index);
                index++;
            }
            CreativeItemRegistry.LAST_CONSTRUCTION_INDEX = getLastGroupIndexFrom("CONSTRUCTION");
            CreativeItemRegistry.LAST_EQUIPMENTS_INDEX = getLastGroupIndexFrom("EQUIPMENT");
            CreativeItemRegistry.LAST_ITEMS_INDEX = getLastGroupIndexFrom("ITEMS");
            CreativeItemRegistry.LAST_NATURE_INDEX = getLastGroupIndexFrom("NATURE");

            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
            for (int i = 0; i < items.size(); i++) {
                Map<String, Object> tag = items.get(i);
                int damage = ((Number) tag.getOrDefault("damage", 0)).intValue();
                int groupIndex = ((Number) tag.getOrDefault("group_index", -1)).intValue();
                byte[] nbt = tag.containsKey("nbt_b64") ? Base64.getDecoder().decode(tag.get("nbt_b64").toString()) : EmptyArrays.EMPTY_BYTES;
                String name = tag.get("id").toString();
                Item item = Item.get(name, damage, 1, nbt, false);
                item.setCompoundTag(nbt);
                if(ItemRegistry.getItemComponents().containsCompound(name)) {
                    item.setNamedTag(ItemRegistry.getItemComponents().getCompound(name).getCompound("components"));
                }
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
                ITEM_DATA.add(new CreativeItemData(item, groupIndex));
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
     * Add an item to {@link CreativeItemRegistry} with a specific group index.
     */
    public void addCreativeItem(Item item, int groupIndex) {
        int i = MAP.isEmpty() ? 0 : MAP.lastIntKey() + 1;
        try {
            this.register(i, item.clone(), groupIndex);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Determines whether a block should be shown in the creative inventory
     * based on its NBT "menu_category" tag.
     */
    public boolean shouldBeRegisteredBlock(@NotNull CompoundTag nbt) {
        if (nbt.contains("menu_category")) {
            CompoundTag menu = nbt.getCompound("menu_category");

            if (menu.contains("category")) {
                try {
                    CreativeCategory category = CreativeCategory.valueOf(menu.getString("category").toUpperCase());
                    return category != CreativeCategory.NONE;
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid creative category in NBT: {}", e.getMessage());
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * Determines if a custom item should be registered in creative inventory.
     * Based on the `item_properties.creative_category` component.
     */
    public boolean shouldBeRegisteredItem(@NotNull CompoundTag nbt) {
        if (nbt.contains("components")) {
            CompoundTag components = nbt.getCompound("components");
            if (components.contains("item_properties")) {
                CompoundTag props = components.getCompound("item_properties");
                if (props.contains("creative_category")) {
                    int cat = props.getInt("creative_category");
                    return CreativeCategory.fromID(cat) != CreativeCategory.NONE;
                }
            }
        }
        return true;
    }

    /**
     * Register a creative item and specify its group index directly.
     */
    public void register(Integer key, Item value, int groupIndex) throws RegisterException {
        if (MAP.putIfAbsent(key, value) != null || ITEM_DATA.stream().anyMatch(data -> data.getItem().equals(value))) {
            return;
        }
        ITEM_DATA.add(new CreativeItemData(value, groupIndex));
    }
    @Override
    public void register(Integer key, Item value) throws RegisterException {
        if (MAP.putIfAbsent(key, value) != null || ITEM_DATA.stream().anyMatch(data -> data.getItem().equals(value))) {
            return;
            //throw new RegisterException("This creative item has already been registered with the identifier: " + key);
        } else {
            ITEM_DATA.add(new CreativeItemData(value, CreativeItemRegistry.LAST_ITEMS_INDEX));
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

    public ObjectLinkedOpenHashSet<CreativeItemGroup> getCreativeGroups() {
        return GROUPS;
    }

    public ObjectLinkedOpenHashSet<CreativeItemGroup> getGroupList() {
        return GROUPS;
    }

    public ObjectLinkedOpenHashSet<CreativeItemData> getCreativeItemData() {
        return ITEM_DATA;
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

    public int resolveGroupIndexFromBlockDefinition(String identifier, CompoundTag nbt) {
        if (nbt != null && nbt.contains("menu_category")) {
            CompoundTag menu = nbt.getCompound("menu_category");

            if (menu.contains("category")) {
                try {
                    String categoryStr = menu.getString("category");
                    CreativeCategory category = CreativeCategory.valueOf(categoryStr.toUpperCase());
                    Map<String, Integer> groupMap = CATEGORY_GROUP_INDEX_MAP.getOrDefault(category, Map.of());

                    if (menu.contains("group")) {
                        String groupName = menu.getString("group");
                        CreativeItemRegistry.ITEM_GROUP_MAP.put(identifier, groupName);

                        Integer index = groupMap.get(groupName);
                        if (index != null) {
                            return index;
                        }
                    }
                    return getLastGroupIndexFrom(categoryStr);
                } catch (Exception e) {
                    log.warn("Invalid creative category/group in block definition NBT for '{}': {}", identifier, e.getMessage());
                }
            }
        }
        return CreativeItemRegistry.LAST_CONSTRUCTION_INDEX;
    }

    public int resolveGroupIndexFromItemDefinition(String identifier, CompoundTag nbt) {
        if (nbt != null && nbt.contains("components")) {
            CompoundTag components = nbt.getCompound("components");

            if (components.contains("item_properties")) {
                CompoundTag itemProps = components.getCompound("item_properties");

                if (itemProps.contains("creative_category")) {
                    try {
                        int catId = itemProps.getInt("creative_category");
                        CreativeCategory category = CreativeCategory.fromID(catId);
                        Map<String, Integer> groupMap = CATEGORY_GROUP_INDEX_MAP.getOrDefault(category, Map.of());

                        if (itemProps.contains("creative_group")) {
                            String groupName = itemProps.getString("creative_group");
                            CreativeItemRegistry.ITEM_GROUP_MAP.put(identifier, groupName);

                            Integer index = groupMap.get(groupName);
                            if (index != null) {
                                return index;
                            }
                        }
                        return getLastGroupIndexFrom(category.name());
                    } catch (Exception e) {
                        log.warn("Invalid creative category/group in item definition NBT for '{}': {}", identifier, e.getMessage());
                    }
                }
            }
        }
        return CreativeItemRegistry.LAST_ITEMS_INDEX;
    }

    public static int getLastGroupIndexFrom(String categoryName) {
        try {
            CreativeCategory category = CreativeCategory.valueOf(categoryName.toUpperCase());
            Map<String, Integer> groupMap = CreativeItemRegistry.CATEGORY_GROUP_INDEX_MAP.getOrDefault(category, Map.of());

            if (!groupMap.isEmpty()) {
                return groupMap.values().stream().mapToInt(i -> i).max().orElse(CreativeItemRegistry.LAST_ITEMS_INDEX);
            }
        } catch (IllegalArgumentException e) {
            // categoryName is invalid (not in enum)
            log.warn("Invalid category '{}', cannot resolve last group index.", categoryName);
        }

        return CreativeItemRegistry.LAST_ITEMS_INDEX;
    }
}
