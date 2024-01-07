package cn.nukkit.registry;

import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.OK;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * @author Cool_Loong
 */
public class ItemRuntimeIdRegistry implements IRegistry<String, Integer, Integer> {
    private static final Object2IntOpenHashMap<String> REGISTRY = new Object2IntOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<String> ID2NAME = new Int2ObjectOpenHashMap<>();
    private static byte[] itemPalette;

    public byte[] getItemPalette() {
        return itemPalette;
    }

    private void generatePalette() {
        BinaryStream paletteBuffer = new BinaryStream();
        paletteBuffer.putUnsignedVarInt(REGISTRY.size());
        for (var entry : REGISTRY.object2IntEntrySet()) {
            paletteBuffer.putString(entry.getKey());
            paletteBuffer.putLShort(entry.getIntValue());
            paletteBuffer.putBoolean(false); //Vanilla Item doesnt component item
        }
        itemPalette = paletteBuffer.getBuffer();
    }

    @Override
    public void init() {
        try (var stream = ItemRegistry.class.getClassLoader().getResourceAsStream("runtime_item_states.json")) {
            assert stream != null;
            Gson gson = new GsonBuilder().setObjectToNumberStrategy(JsonReader::nextInt).create();
            TypeToken<List<Map<String, Object>>> typeToken = new TypeToken<>() {
            };
            List<Map<String, Object>> list = gson.fromJson(new InputStreamReader(stream), typeToken.getType());
            for (var v : list) {
                REGISTRY.put(v.get("name").toString(), Integer.parseInt(v.get("id").toString()));
            }
            REGISTRY.defaultReturnValue(Integer.MAX_VALUE);
            trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer get(String key) {
        return REGISTRY.get(key);
    }

    public int getInt(String key) {
        return REGISTRY.getInt(key);
    }

    public String getIdentifier(int runtimeId) {
        return ID2NAME.get(runtimeId);
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public void register(String key, Integer value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key, value.intValue()) == Integer.MAX_VALUE) {
            ID2NAME.put(value.intValue(), key);
        } else {
            throw new RegisterException("The item runtime has been registered!");
        }
    }
}
