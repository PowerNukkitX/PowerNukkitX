package cn.nukkit.registry;

import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.utils.OK;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class GeneratorRegistry extends BaseRegistry<String, Class<? extends Generator>, Class<? extends Generator>> {
    private static final Object2ObjectOpenHashMap<String, Class<? extends Generator>> REGISTRY = new Object2ObjectOpenHashMap<>();

    @Override
    public void init() {
        try {
            register("flat", Flat.class);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
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
    public void register(String key, Class<? extends Generator> value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key.toLowerCase(), value) == null) {
        } else {
            throw new RegisterException("This generator has already been registered with the key: " + key);
        }
    }
}
