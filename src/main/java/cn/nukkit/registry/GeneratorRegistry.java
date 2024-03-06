package cn.nukkit.registry;

import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class GeneratorRegistry implements IRegistry<String, Class<? extends Generator>, Class<? extends Generator>> {
    private static final Object2ObjectOpenHashMap<String, Class<? extends Generator>> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try {
            register("flat", Flat.class);
            register("normal", Flat.class);
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

    public Set<String> getGeneratorList() {
        return REGISTRY.keySet();
    }

    @Override
    public Class<? extends Generator> get(String key) {
        return REGISTRY.get(key.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        REGISTRY.clear();
        init();
    }

    @Override
    public void register(String key, Class<? extends Generator> value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key.toLowerCase(Locale.ENGLISH), value) == null) {
        } else {
            throw new RegisterException("This generator has already been registered with the key: " + key);
        }
    }
}
