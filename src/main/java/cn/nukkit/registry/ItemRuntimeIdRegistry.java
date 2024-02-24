package cn.nukkit.registry;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Cool_Loong
 */
public class ItemRuntimeIdRegistry implements IRegistry<String, Integer, Integer> {
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);
    private static final Object2IntOpenHashMap<String> REGISTRY = new Object2IntOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, RuntimeEntry> CUSTOM_REGISTRY = new Object2ObjectOpenHashMap<>();

    static {
        REGISTRY.defaultReturnValue(Integer.MAX_VALUE);
    }

    private static final Int2ObjectOpenHashMap<String> ID2NAME = new Int2ObjectOpenHashMap<>();
    private static byte[] itemPalette;

    public byte[] getItemPalette() {
        return itemPalette;
    }

    private void generatePalette() {
        BinaryStream paletteBuffer = new BinaryStream();
        HashMap<Integer, Boolean> verify = new HashMap<>();
        paletteBuffer.putUnsignedVarInt(REGISTRY.size() + CUSTOM_REGISTRY.size());
        for (var entry : REGISTRY.object2IntEntrySet()) {
            paletteBuffer.putString(entry.getKey());
            int rid = entry.getIntValue();
            paletteBuffer.putLShort(rid);
            if (verify.putIfAbsent(rid, true) != null) {
                throw new IllegalArgumentException("Runtime ID is already registered: " + rid);
            }
            paletteBuffer.putBoolean(false); //Vanilla Item doesnt component item
        }
        for (var entry : CUSTOM_REGISTRY.object2ObjectEntrySet()) {
            paletteBuffer.putString(entry.getKey());
            int rid = entry.getValue().runtimeId();
            paletteBuffer.putLShort(rid);
            if (verify.putIfAbsent(rid, true) != null) {
                throw new IllegalArgumentException("Runtime ID is already registered: " + rid);
            }
            paletteBuffer.putBoolean(entry.getValue().isComponent());
        }
        itemPalette = paletteBuffer.getBuffer();
    }

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try (var stream = ItemRegistry.class.getClassLoader().getResourceAsStream("item_data.nbt")) {
            assert stream != null;
            CompoundTag compoundTag = NBTIO.readCompressed(stream);
            for (var tag : compoundTag.getList("item", CompoundTag.class).getAll()) {
                register0(tag.getString("name"), (int) tag.getShort("id"));
            }
            trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer get(String key) {
        int i = REGISTRY.getInt(key);
        if (i == Integer.MAX_VALUE) {
            RuntimeEntry runtimeEntry = CUSTOM_REGISTRY.get(key);
            if (runtimeEntry == null) return Integer.MAX_VALUE;
            else return runtimeEntry.runtimeId;
        }
        return i;
    }

    public int getInt(String key) {
        int i = REGISTRY.getInt(key);
        if (i == Integer.MAX_VALUE) {
            RuntimeEntry runtimeEntry = CUSTOM_REGISTRY.get(key);
            if (runtimeEntry == null) return Integer.MAX_VALUE;
            else return runtimeEntry.runtimeId;
        }
        return i;
    }

    public String getIdentifier(int runtimeId) {
        return ID2NAME.get(runtimeId);
    }

    @Override
    public void trim() {
        REGISTRY.trim();
        CUSTOM_REGISTRY.trim();
        generatePalette();
    }

    @Override
    public void register(String key, Integer value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key, value.intValue()) == Integer.MAX_VALUE) {
            ID2NAME.put(value.intValue(), key);
        } else {
            throw new RegisterException("The item: " + key + "runtime id has been registered!");
        }
    }

    public void registerCustomRuntimeItem(RuntimeEntry entry) throws RegisterException {
        if (CUSTOM_REGISTRY.putIfAbsent(entry.identifier, entry) == null) {
            ID2NAME.put(entry.runtimeId(), entry.identifier);
        } else {
            throw new RegisterException("The item: " + entry.identifier + " runtime id has been registered!");
        }
    }

    private void register0(String key, Integer value) {
        if (REGISTRY.putIfAbsent(key, value.intValue()) == Integer.MAX_VALUE) {
            ID2NAME.put(value.intValue(), key);
        }
    }

    public record RuntimeEntry(String identifier, int runtimeId, boolean isComponent) {
    }
}
