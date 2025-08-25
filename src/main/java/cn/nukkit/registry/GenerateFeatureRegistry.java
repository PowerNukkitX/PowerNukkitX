package cn.nukkit.registry;

import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.feature.LegacyJungleTreeFeature;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class GenerateFeatureRegistry implements IRegistry<String, GenerateFeature, Class<? extends GenerateFeature>> {
    private static final Object2ObjectOpenHashMap<String, Class<? extends GenerateFeature>> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try {
            this.register(LegacyJungleTreeFeature.NAME, LegacyJungleTreeFeature.class);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    public GenerateFeature get(Class<? extends GenerateFeature> c) {
        for (var entry : REGISTRY.entrySet()) {
            if (entry.getValue().equals(c)) {
                try {
                    return entry.getValue().getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public boolean has(String key) {
        return REGISTRY.containsKey(key.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public GenerateFeature get(String key) {
        try {
            return REGISTRY.get(key.toLowerCase(Locale.ENGLISH)).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<? extends GenerateFeature> getStageByName(String key) {
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
    public void register(String key, Class<? extends GenerateFeature> value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key.toLowerCase(Locale.ENGLISH), value) != null) {
            throw new RegisterException("This generator has already been registered with the key: " + key);
        }
    }
}
