package cn.nukkit.registry;

import cn.nukkit.entity.effect.PotionType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PotionRegistry implements IRegistry<String, PotionType, PotionType> {
    private static final Object2ObjectOpenHashMap<String, PotionType> REGISTRY = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<Integer, PotionType> ID_2_POTION = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        register0(PotionType.WATER);
        register0(PotionType.MUNDANE);
        register0(PotionType.MUNDANE_LONG);
        register0(PotionType.THICK);
        register0(PotionType.AWKWARD);
        register0(PotionType.NIGHT_VISION);
        register0(PotionType.NIGHT_VISION_LONG);
        register0(PotionType.INVISIBILITY);
        register0(PotionType.INVISIBILITY_LONG);
        register0(PotionType.LEAPING);
        register0(PotionType.LEAPING_LONG);
        register0(PotionType.LEAPING_STRONG);
        register0(PotionType.FIRE_RESISTANCE);
        register0(PotionType.FIRE_RESISTANCE_LONG);
        register0(PotionType.SWIFTNESS);
        register0(PotionType.SWIFTNESS_LONG);
        register0(PotionType.SWIFTNESS_STRONG);
        register0(PotionType.SLOWNESS);
        register0(PotionType.SLOWNESS_LONG);
        register0(PotionType.WATER_BREATHING);
        register0(PotionType.WATER_BREATHING_LONG);
        register0(PotionType.HEALING);
        register0(PotionType.HEALING_STRONG);
        register0(PotionType.HARMING);
        register0(PotionType.HARMING_STRONG);
        register0(PotionType.POISON);
        register0(PotionType.POISON_LONG);
        register0(PotionType.POISON_STRONG);
        register0(PotionType.REGENERATION);
        register0(PotionType.REGENERATION_LONG);
        register0(PotionType.REGENERATION_STRONG);
        register0(PotionType.STRENGTH);
        register0(PotionType.STRENGTH_LONG);
        register0(PotionType.STRENGTH_STRONG);
        register0(PotionType.WEAKNESS);
        register0(PotionType.WEAKNESS_LONG);
        register0(PotionType.WITHER);
        register0(PotionType.TURTLE_MASTER);
        register0(PotionType.TURTLE_MASTER_LONG);
        register0(PotionType.TURTLE_MASTER_STRONG);
        register0(PotionType.SLOW_FALLING);
        register0(PotionType.SLOW_FALLING_LONG);
        register0(PotionType.SLOWNESS_STRONG);
        register0(PotionType.WIND_CHARGED);
        register0(PotionType.WEAVING);
        register0(PotionType.OOZING);
        register0(PotionType.INFESTED);
    }

    @Override
    public PotionType get(String key) {
        return REGISTRY.get(key);
    }

    public PotionType get(int id) {
        return ID_2_POTION.get(id);
    }

    public Map<String, PotionType> getPotions() {
        return Collections.unmodifiableMap(REGISTRY);
    }

    public Map<Integer, PotionType> getPotionId2TypeMap() {
        return Collections.unmodifiableMap(ID_2_POTION);
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    public void reload() {
        isLoad.set(false);
        REGISTRY.clear();
        ID_2_POTION.clear();
        init();
    }

    @Override
    public void register(String key, PotionType value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key, value) == null) {
            ID_2_POTION.put(value.id(), value);
        } else {
            throw new RegisterException("This potion has already been registered with the identifier: " + key);
        }
    }

    private void register0(PotionType value) {
        try {
            register(value.stringId(), value);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
