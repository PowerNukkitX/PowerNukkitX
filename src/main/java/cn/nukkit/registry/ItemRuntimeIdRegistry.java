package cn.nukkit.registry;

import cn.nukkit.utils.OK;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * @author Cool_Loong
 */
public class ItemRuntimeIdRegistry implements IRegistry<String, Integer, Integer> {
    private static final Object2IntOpenHashMap<String> REGISTRY = new Object2IntOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, String> FALLBACK_BLOCK_ITEM_ID = new Object2ObjectOpenHashMap<>();
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

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public OK<?> register(String key, Integer value) {
        if (REGISTRY.putIfAbsent(key, value.intValue()) == 0) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("The item runtime has been registered!"));
        }
    }
}
