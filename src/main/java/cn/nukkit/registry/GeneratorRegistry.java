package cn.nukkit.registry;

import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.HashMap;
import java.util.Map;

public class GeneratorRegistry extends BaseRegistry<String, Class<? extends Generator>, Class<? extends Generator>> {
    private static final Object2ObjectOpenHashMap<String, Class<? extends Generator>> REGISTRY = new Object2ObjectOpenHashMap<>();

    @Override
    public void init() {
        register("flat", Flat.class);
    }

    public String getGeneratorName(Class<? extends Generator> c) {
        for (var entry : REGISTRY.entrySet()) {
            if (entry.getValue().equals(c)) {
                return entry.getKey();
            }
        }
        return "unknown";
    }

    @Override
    public Class<? extends Generator> get(String key) {
        return REGISTRY.get(key);
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public OK<?> register(String key, Class<? extends Generator> value) {
        if (REGISTRY.putIfAbsent(key.toLowerCase(), value) == null) {
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("This generator has already been registered with the key: " + key));
        }
    }
}
