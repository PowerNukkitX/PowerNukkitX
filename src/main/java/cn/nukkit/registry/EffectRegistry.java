package cn.nukkit.registry;

import cn.nukkit.entity.effect.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.sunlan.fastreflection.FastConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EffectRegistry implements IRegistry<EffectType, Effect, Class<? extends Effect>> {
    private static final Object2ObjectOpenHashMap<EffectType, FastConstructor<? extends Effect>> CACHE_CONSTRUCTORS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, EffectType> STRING_ID_2_TYPE = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<Integer, EffectType> INT_ID_2_TYPE = new Object2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        register0(EffectType.SPEED, EffectSpeed.class);
        register0(EffectType.SLOWNESS, EffectSlowness.class);
        register0(EffectType.HASTE, EffectHaste.class);
        register0(EffectType.MINING_FATIGUE, EffectMiningFatigue.class);
        register0(EffectType.STRENGTH, EffectStrength.class);
        register0(EffectType.INSTANT_HEALTH, EffectInstantHealth.class);
        register0(EffectType.INSTANT_DAMAGE, EffectInstantDamage.class);
        register0(EffectType.JUMP_BOOST, EffectJumpBoost.class);
        register0(EffectType.NAUSEA, EffectNausea.class);
        register0(EffectType.REGENERATION, EffectRegeneration.class);
        register0(EffectType.RESISTANCE, EffectResistance.class);
        register0(EffectType.FIRE_RESISTANCE, EffectFireResistance.class);
        register0(EffectType.WATER_BREATHING, EffectWaterBreathing.class);
        register0(EffectType.INVISIBILITY, EffectInvisibility.class);
        register0(EffectType.BLINDNESS, EffectBlindness.class);
        register0(EffectType.NIGHT_VISION, EffectNightVision.class);
        register0(EffectType.HUNGER, EffectHunger.class);
        register0(EffectType.WEAKNESS, EffectWeakness.class);
        register0(EffectType.POISON, EffectPoison.class);
        register0(EffectType.WITHER, EffectWither.class);
        register0(EffectType.HEALTH_BOOST, EffectHealthBoost.class);
        register0(EffectType.ABSORPTION, EffectAbsorption.class);
        register0(EffectType.SATURATION, EffectSaturation.class);
        register0(EffectType.LEVITATION, EffectLevitation.class);
        register0(EffectType.FATAL_POISON, EffectFatalPoison.class);
        register0(EffectType.SLOW_FALLING, EffectSlowFalling.class);
        register0(EffectType.CONDUIT_POWER, EffectConduitPower.class);
        // Effects that cannot be realized at the moment
        //register0(EffectType.BAD_OMEN, BadOmenEffect.class);
        //register0(EffectType.VILLAGE_HERO, VillageHeroEffect.class);
        register0(EffectType.DARKNESS, EffectDarkness.class);
    }

    @Override
    public Effect get(EffectType key) {
        try {
            FastConstructor<? extends Effect> fastConstructor = CACHE_CONSTRUCTORS.get(key);
            if (fastConstructor == null) return null;
            return (Effect) fastConstructor.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public EffectType getType(String stringId) {
        return STRING_ID_2_TYPE.get(stringId);
    }

    public EffectType getType(Integer id) {
        return INT_ID_2_TYPE.get(id);
    }

    public Map<String, EffectType> getEffectStringId2TypeMap() {
        return Collections.unmodifiableMap(STRING_ID_2_TYPE);
    }

    public Map<Integer, EffectType> getEffectId2TypeMap() {
        return Collections.unmodifiableMap(INT_ID_2_TYPE);
    }

    @Override
    public void trim() {
        CACHE_CONSTRUCTORS.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        INT_ID_2_TYPE.clear();
        STRING_ID_2_TYPE.clear();
        CACHE_CONSTRUCTORS.clear();
        init();
    }

    @Override
    public void register(EffectType type, Class<? extends Effect> effect) throws RegisterException {
        try {
            FastConstructor<? extends Effect> c = FastConstructor.create(effect.getConstructor());
            if (CACHE_CONSTRUCTORS.putIfAbsent(type, c) == null) {
                STRING_ID_2_TYPE.put(type.stringId(), type);
                if (type.id() != null) {
                    INT_ID_2_TYPE.put(type.id(), type);
                }
            } else {
                throw new RegisterException("This effect has already been registered with the identifier: " + type);
            }
        } catch (NoSuchMethodException e) {
            throw new RegisterException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void register0(EffectType type, Class<? extends Effect> effect){
        try {
            register(type, effect);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
