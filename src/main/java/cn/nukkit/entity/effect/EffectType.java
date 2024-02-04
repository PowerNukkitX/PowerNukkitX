package cn.nukkit.entity.effect;

import cn.nukkit.registry.Registries;

import java.util.Objects;

/**
 * @author MEFRREEX
 */
public record EffectType(String stringId, Integer id) {

    public static final EffectType SPEED = new EffectType("speed", 1);

    public static final EffectType SLOWNESS = new EffectType("slowness", 2);

    public static final EffectType HASTE = new EffectType("haste", 3);

    public static final EffectType MINING_FATIGUE = new EffectType("mining_fatigue", 4);

    public static final EffectType STRENGTH = new EffectType("strength", 5);

    public static final EffectType INSTANT_HEALTH = new EffectType("instant_health", 6);

    public static final EffectType INSTANT_DAMAGE = new EffectType("instant_damage", 7);

    public static final EffectType JUMP_BOOST = new EffectType("jump_boost", 8);

    public static final EffectType NAUSEA = new EffectType("nausea", 9);

    public static final EffectType REGENERATION = new EffectType("regeneration", 10);

    public static final EffectType RESISTANCE = new EffectType("resistance", 11);

    public static final EffectType FIRE_RESISTANCE = new EffectType("fire_resistance", 12);

    public static final EffectType WATER_BREATHING = new EffectType("water_breathing", 13);

    public static final EffectType INVISIBILITY = new EffectType("invisibility", 14);

    public static final EffectType BLINDNESS = new EffectType("blindness", 15);

    public static final EffectType NIGHT_VISION = new EffectType("night_vision", 16);

    public static final EffectType HUNGER = new EffectType("hunger", 17);

    public static final EffectType WEAKNESS = new EffectType("weakness", 18);

    public static final EffectType POISON = new EffectType("poison", 19);

    public static final EffectType WITHER = new EffectType("wither", 20);

    public static final EffectType HEALTH_BOOST = new EffectType("health_boost", 21);

    public static final EffectType ABSORPTION = new EffectType("absorption", 22);

    public static final EffectType SATURATION = new EffectType("saturation", 23);

    public static final EffectType LEVITATION = new EffectType("levitation", 24);

    public static final EffectType FATAL_POISON = new EffectType("fatal_poison", 25);

    public static final EffectType CONDUIT_POWER = new EffectType("conduit_power", 26);

    public static final EffectType SLOW_FALLING = new EffectType("slow_falling", 27);

    public static final EffectType BAD_OMEN = new EffectType("bad_omen", 28);

    public static final EffectType VILLAGE_HERO = new EffectType("village_hero", 29);

    public static final EffectType DARKNESS = new EffectType("darkness", 30);

    public static EffectType get(String stringId) {
        return Registries.EFFECT.getType(stringId);
    }

    public static EffectType get(int id) {
        return Registries.EFFECT.getType(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EffectType type) {
            return type.stringId.equals(this.stringId) && Objects.equals(type.id, this.id);
        }
        return false;
    }
}
