package cn.nukkit.entity.effect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MEFRREEX
 */
public enum EffectType {
    SPEED("speed", 1),
    SLOWNESS("slowness", 2),
    HASTE("haste", 3),
    MINING_FATIGUE("mining_fatigue", 4),
    STRENGTH("strength", 5),
    INSTANT_HEALTH("instant_health", 6),
    INSTANT_DAMAGE("instant_damage", 7),
    JUMP_BOOST("jump_boost", 8),
    NAUSEA("nausea", 9),
    REGENERATION("regeneration", 10),
    RESISTANCE("resistance", 11),
    FIRE_RESISTANCE("fire_resistance", 12),
    WATER_BREATHING("water_breathing", 13),
    INVISIBILITY("invisibility", 14),
    BLINDNESS("blindness", 15),
    NIGHT_VISION("night_vision", 16),
    HUNGER("hunger", 17),
    WEAKNESS("weakness", 18),
    POISON("poison", 19),
    WITHER("wither", 20),
    HEALTH_BOOST("health_boost", 21),
    ABSORPTION("absorption", 22),
    SATURATION("saturation", 23),
    LEVITATION("levitation", 24),
    FATAL_POISON("fatal_poison", 25),
    CONDUIT_POWER("conduit_power", 26),
    SLOW_FALLING("slow_falling", 27),
    BAD_OMEN("bad_omen", 28),
    VILLAGE_HERO("village_hero", 29),
    DARKNESS("darkness", 30);

    private final String stringId;
    private final int id;

    private static final Map<Integer, EffectType> BY_ID = new HashMap<>();
    private static final Map<String, EffectType> BY_STRING_ID = new HashMap<>();

    EffectType(String namespaceId, int id) {
        this.stringId = namespaceId;
        this.id = id;
    }

    public String getStringId() {
        return stringId;
    }

    public int getId() {
        return id;
    }

    public static EffectType get(String stringId) {
        return BY_STRING_ID.get(stringId);
    }

    public static EffectType get(int id) {
        return BY_ID.get(id);
    }

    static {
        for (EffectType type : values()) {
            BY_ID.put(type.getId(), type);
            BY_STRING_ID.put(type.getStringId(), type);
        }
    }
}

