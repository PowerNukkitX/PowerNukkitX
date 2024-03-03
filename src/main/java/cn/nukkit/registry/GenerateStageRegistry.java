package cn.nukkit.registry;

import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.stages.FinishedStage;
import cn.nukkit.level.generator.stages.FlatGenerateStage;
import cn.nukkit.level.generator.stages.LightPopulationStage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class GenerateStageRegistry implements IRegistry<String, GenerateStage, Class<? extends GenerateStage>> {
    private static final Object2ObjectOpenHashMap<String, Class<? extends GenerateStage>> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try {
            this.register(FinishedStage.NAME, FinishedStage.class);
            this.register(FlatGenerateStage.NAME, FlatGenerateStage.class);
            this.register(LightPopulationStage.NAME, LightPopulationStage.class);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    public GenerateStage get(Class<? extends GenerateStage> c) {
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

    @Override
    public GenerateStage get(String key) {
        try {
            return REGISTRY.get(key.toLowerCase(Locale.ENGLISH)).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<? extends GenerateStage> getStageByName(String key) {
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
    public void register(String key, Class<? extends GenerateStage> value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key.toLowerCase(Locale.ENGLISH), value) != null) {
            throw new RegisterException("This generator has already been registered with the key: " + key);
        }
    }
}
