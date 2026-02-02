package cn.nukkit.registry;

import cn.nukkit.utils.BinaryStream;
import cn.nukkit.block.Block;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Cool_Loong
 */
@Slf4j
public class ItemRuntimeIdRegistry implements IRegistry<String, Integer, Integer> {
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    private static final Object2IntOpenHashMap<String> REGISTRY = new Object2IntOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, RuntimeEntry> CUSTOM_REGISTRY = new Object2ObjectOpenHashMap<>();

    static {
        REGISTRY.defaultReturnValue(Integer.MAX_VALUE);
    }

    private static final Int2ObjectOpenHashMap<String> ID2NAME = new Int2ObjectOpenHashMap<>();
    @Getter
    private static final ObjectOpenHashSet<ItemData> ITEMDATA = new ObjectOpenHashSet<>();

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
        if (isLoad.getAndSet(true))
            return;

        // We use ProxyPass data since protocol 776 since we need item version and componentBased now.
        try (InputStream stream = ItemRegistry.class.getClassLoader().getResourceAsStream("gamedata/proxypass/runtime_item_states.json")){
            if (stream == null) {
                throw new IllegalStateException("Failed to load runtime_item_states.json");
            }

            JsonArray items = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();

            for (JsonElement element : items) {
                JsonObject item = element.getAsJsonObject();
                register1(new ItemData(item.get("name").getAsString(), item.get("id").getAsInt(), item.get("version").getAsInt(), item.get("componentBased").getAsBoolean()));
            }
            trim();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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

    public void reload() {
        isLoad.set(false);
        REGISTRY.clear();
        CUSTOM_REGISTRY.clear();
        init();
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
            int version = ItemVersionResolver.resolveItemVersion(entry.isComponent, ItemVersionResolver.isBlock(entry.identifier));
            ITEMDATA.add(new ItemData(entry.identifier, entry.runtimeId, version, entry.isComponent));
        } else {
            throw new RegisterException("The item: " + entry.identifier + " runtime id has been registered!");
        }
    }

    public class ItemVersionResolver {
        public static final int LEGACY = 0;
        public static final int DATA_DRIVEN = 1;
        public static final int NONE = 2;

        public static int resolveItemVersion(boolean componentBased, boolean isBlock) {
            if (isBlock) return NONE;
            return componentBased ? DATA_DRIVEN : LEGACY;
        }

        public static boolean isBlock(String identifier) {
            Block block = Registries.BLOCK.get(identifier);
            return block != null && !block.isAir();
        }
    }

    private void register0(String key, Integer value) {
        if (REGISTRY.putIfAbsent(key, value.intValue()) == Integer.MAX_VALUE) {
            ID2NAME.put(value.intValue(), key);
        }
    }

    public void register1(ItemData entry) {
        if(!ITEMDATA.contains(entry)) {
            ITEMDATA.add(entry);
            register0(entry.identifier, entry.runtimeId);
        }
    }

    public record RuntimeEntry(String identifier, int runtimeId, boolean isComponent) {
    }

    public record ItemData(String identifier, int runtimeId, int version, boolean componentBased) {
    }
}
