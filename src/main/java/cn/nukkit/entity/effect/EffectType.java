package cn.nukkit.entity.effect;

import cn.nukkit.registry.Registries;

import java.util.Objects;

/**
 * @author MEFRREEX
 */
public record EffectType(String stringId, Integer id) {

    public static final EffectType $1 = new EffectType("speed", 1);

    public static final EffectType $2 = new EffectType("slowness", 2);

    public static final EffectType $3 = new EffectType("haste", 3);

    public static final EffectType $4 = new EffectType("mining_fatigue", 4);

    public static final EffectType $5 = new EffectType("strength", 5);

    public static final EffectType $6 = new EffectType("instant_health", 6);

    public static final EffectType $7 = new EffectType("instant_damage", 7);

    public static final EffectType $8 = new EffectType("jump_boost", 8);

    public static final EffectType $9 = new EffectType("nausea", 9);

    public static final EffectType $10 = new EffectType("regeneration", 10);

    public static final EffectType $11 = new EffectType("resistance", 11);

    public static final EffectType $12 = new EffectType("fire_resistance", 12);

    public static final EffectType $13 = new EffectType("water_breathing", 13);

    public static final EffectType $14 = new EffectType("invisibility", 14);

    public static final EffectType $15 = new EffectType("blindness", 15);

    public static final EffectType $16 = new EffectType("night_vision", 16);

    public static final EffectType $17 = new EffectType("hunger", 17);

    public static final EffectType $18 = new EffectType("weakness", 18);

    public static final EffectType $19 = new EffectType("poison", 19);

    public static final EffectType $20 = new EffectType("wither", 20);

    public static final EffectType $21 = new EffectType("health_boost", 21);

    public static final EffectType $22 = new EffectType("absorption", 22);

    public static final EffectType $23 = new EffectType("saturation", 23);

    public static final EffectType $24 = new EffectType("levitation", 24);

    public static final EffectType $25 = new EffectType("fatal_poison", 25);

    public static final EffectType $26 = new EffectType("conduit_power", 26);

    public static final EffectType $27 = new EffectType("slow_falling", 27);

    public static final EffectType $28 = new EffectType("bad_omen", 28);

    public static final EffectType $29 = new EffectType("village_hero", 29);

    public static final EffectType $30 = new EffectType("darkness", 30);

    public static EffectType get(String stringId) {
        return Registries.EFFECT.getType(stringId);
    }

    public static EffectType get(int id) {
        return Registries.EFFECT.getType(id);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (obj instanceof EffectType type) {
            return type.stringId.equals(this.stringId) && Objects.equals(type.id, this.id);
        }
        return false;
    }
}
