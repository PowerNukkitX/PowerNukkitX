package cn.nukkit.registry;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.sunlan.fastreflection.FastConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Allay Project 12/15/2023
 *
 * @author Cool_Loong
 */
public final class ItemRegistry extends BaseRegistry<String, Item, Class<Item>> {
    private static final Set<String> KEYSET = new HashSet<>();
    private static final Object2ObjectOpenHashMap<String, FastConstructor<Item>> CACHE_CONSTRUCTORS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, String> FALLBACK_BLOCK_ITEM_ID = new Object2ObjectOpenHashMap<>();

    @Override
    public void init() {

    }

    @Override
    public Item get(String key) {
        return null;
    }

    public Item get(String id, Integer meta) {

    }

    public Item get(String id, Integer meta, int count) {

    }

    public Item get(String id, Integer meta, int count, CompoundTag tags) {

    }

    public Item get(String id, Integer meta, int count, byte[] tags) {

    }

    public void trim() {
        CACHE_CONSTRUCTORS.trim();
    }

    @Override
    public OK<?> register(String key, Class<Item> value) {
        try {
            FastConstructor<Item> itemFastConstructor = FastConstructor.create(value.getConstructor(String.class, Integer.class, int.class, String.class));

        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            return new OK<>(false, e);
        }

    }
}
