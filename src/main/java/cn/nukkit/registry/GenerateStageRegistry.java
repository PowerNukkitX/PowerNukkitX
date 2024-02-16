package cn.nukkit.registry;

import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.GenerateStages;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class GenerateStageRegistry implements IRegistry<String, GenerateStage, GenerateStage> {
    private static final Object2ObjectOpenHashMap<String, GenerateStage> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);
    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try {
            this.register(GenerateStages.FINISHED.name(), GenerateStages.FINISHED);
            this.register(GenerateStages.FLAT_GENERATE.name(), GenerateStages.FLAT_GENERATE);
            this.register(GenerateStages.LIGHT_POPULATION.name(), GenerateStages.LIGHT_POPULATION);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    public GenerateStage get(Class<? extends GenerateStage> c) {
        for (var entry : REGISTRY.entrySet()) {
            if (entry.getValue().getClass().equals(c)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public GenerateStage get(String key) {
        return REGISTRY.get(key.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public void register(String key, GenerateStage value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key.toLowerCase(Locale.ENGLISH), value) != null) {
            throw new RegisterException("This generator has already been registered with the key: " + key);
        }
    }
}
