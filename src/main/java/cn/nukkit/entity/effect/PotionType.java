package cn.nukkit.entity.effect;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.registry.Registries;

import java.util.List;

/**
 * @author MEFRREEX
 */
public record PotionType(String name, String stringId, int id, int level, PotionEffectDefinition effects) {

    public PotionType(String name, String stringId, int id, PotionEffectDefinition effects) {
        this(name, stringId, id, 1, effects);
    }

    public static final PotionType WATER = new PotionType("Water", "minecraft:water", 0, PotionEffectDefinition.EMPTY);

    public static final PotionType MUNDANE = new PotionType("Mundane", "minecraft:mundane", 1, PotionEffectDefinition.EMPTY);

    public static final PotionType MUNDANE_LONG = new PotionType("Long Mundane", "minecraft:long_mundane", 2, PotionEffectDefinition.EMPTY);

    public static final PotionType THICK = new PotionType("Thick", "minecraft:thick", 3, PotionEffectDefinition.EMPTY);

    public static final PotionType AWKWARD = new PotionType("Awkward", "minecraft:awkward", 4, PotionEffectDefinition.EMPTY);

    public static final PotionType NIGHT_VISION = new PotionType("Night Vision", "minecraft:nightvision", 5, PotionEffectDefinition.NIGHT_VISION);

    public static final PotionType NIGHT_VISION_LONG = new PotionType("Long Night Vision", "minecraft:long_nightvision", 6, PotionEffectDefinition.NIGHT_VISION_LONG);

    public static final PotionType INVISIBILITY = new PotionType("Invisibility", "minecraft:invisibility", 7, PotionEffectDefinition.INVISIBILITY);

    public static final PotionType INVISIBILITY_LONG = new PotionType("Long Invisibility", "minecraft:long_invisibility", 8, PotionEffectDefinition.INVISIBILITY_LONG);

    public static final PotionType LEAPING = new PotionType("Leaping", "minecraft:leaping", 9, PotionEffectDefinition.LEAPING);

    public static final PotionType LEAPING_LONG = new PotionType("Long Leaping", "minecraft:long_leaping", 10, PotionEffectDefinition.LEAPING_LONG);

    public static final PotionType LEAPING_STRONG = new PotionType("Strong Leaping", "minecraft:strong_leaping", 11, 2, PotionEffectDefinition.LEAPING_STRONG);

    public static final PotionType FIRE_RESISTANCE = new PotionType("Fire Resistance", "minecraft:fire_resistance", 12, PotionEffectDefinition.FIRE_RESISTANCE);

    public static final PotionType FIRE_RESISTANCE_LONG = new PotionType("Long Fire Resistance", "minecraft:long_fire_resistance", 13, PotionEffectDefinition.FIRE_RESISTANCE_LONG);

    public static final PotionType SWIFTNESS = new PotionType("Swiftness", "minecraft:swiftness", 14, PotionEffectDefinition.SWIFTNESS);

    public static final PotionType SWIFTNESS_LONG = new PotionType("Long Swiftness", "minecraft:long_swiftness", 15, PotionEffectDefinition.SWIFTNESS_LONG);

    public static final PotionType SWIFTNESS_STRONG = new PotionType("Strong Swiftness", "minecraft:strong_swiftness", 16, 2, PotionEffectDefinition.SWIFTNESS_STRONG);

    public static final PotionType SLOWNESS = new PotionType("Slowness", "minecraft:slowness", 17, PotionEffectDefinition.SLOWNESS);

    public static final PotionType SLOWNESS_LONG = new PotionType("Long Slowness", "minecraft:long_slowness", 18, PotionEffectDefinition.SLOWNESS_LONG);

    public static final PotionType WATER_BREATHING = new PotionType("Water Breathing", "minecraft:water_breathing", 19, PotionEffectDefinition.WATER_BREATHING);

    public static final PotionType WATER_BREATHING_LONG = new PotionType("Long Water Breathing", "minecraft:long_water_breathing", 20, PotionEffectDefinition.WATER_BREATHING_LONG);

    public static final PotionType HEALING = new PotionType("Healing", "minecraft:healing", 21, PotionEffectDefinition.HEALING);

    public static final PotionType HEALING_STRONG = new PotionType("Strong Healing", "minecraft:strong_healing", 22, 2, PotionEffectDefinition.HEALING_STRONG);

    public static final PotionType HARMING = new PotionType("Harming", "minecraft:harming", 23, PotionEffectDefinition.HARMING);

    public static final PotionType HARMING_STRONG = new PotionType("Strong Harming", "minecraft:strong_harming", 24, 2, PotionEffectDefinition.HARMING_STRONG);

    public static final PotionType POISON = new PotionType("Poison", "minecraft:poison", 25, PotionEffectDefinition.POISON);

    public static final PotionType POISON_LONG = new PotionType("Long Poison", "minecraft:long_poison", 26, PotionEffectDefinition.POISON_LONG);

    public static final PotionType POISON_STRONG = new PotionType("Strong Poison", "minecraft:strong_poison", 27, 2, PotionEffectDefinition.POISON_STRONG);

    public static final PotionType REGENERATION = new PotionType("Regeneration", "minecraft:regeneration", 28, PotionEffectDefinition.REGENERATION);

    public static final PotionType REGENERATION_LONG = new PotionType("Long Regeneration", "minecraft:long_regeneration", 29, PotionEffectDefinition.REGENERATION_LONG);

    public static final PotionType REGENERATION_STRONG = new PotionType("Strong Regeneration", "minecraft:strong_regeneration", 30, 2, PotionEffectDefinition.REGENERATION_STRONG);

    public static final PotionType STRENGTH = new PotionType("Strength", "minecraft:strength", 31, PotionEffectDefinition.STRENGTH);

    public static final PotionType STRENGTH_LONG = new PotionType("Long Strength", "minecraft:long_strength", 32, PotionEffectDefinition.STRENGTH_LONG);

    public static final PotionType STRENGTH_STRONG = new PotionType("Strong Strength", "minecraft:strong_strength", 33, 2, PotionEffectDefinition.STRENGTH_STRONG);

    public static final PotionType WEAKNESS = new PotionType("Weakness", "minecraft:weakness", 34, PotionEffectDefinition.WEAKNESS);

    public static final PotionType WEAKNESS_LONG = new PotionType("Long Weakness", "minecraft:long_weakness", 35, PotionEffectDefinition.WEAKNESS_LONG);

    public static final PotionType WITHER = new PotionType("Wither", "minecraft:strong_wither", 36, 2, PotionEffectDefinition.DECAY);

    public static final PotionType TURTLE_MASTER = new PotionType("Turtle Master", "minecraft:turtle_master", 37, PotionEffectDefinition.TURTLE_MASTER);

    public static final PotionType TURTLE_MASTER_LONG = new PotionType("Long Turtle Master", "minecraft:long_turtle_master", 38, PotionEffectDefinition.TURTLE_MASTER_LONG);

    public static final PotionType TURTLE_MASTER_STRONG = new PotionType("Strong Turtle Master", "minecraft:strong_turtle_master", 39, 2, PotionEffectDefinition.TURTLE_MASTER_STRONG);

    public static final PotionType SLOW_FALLING = new PotionType("Slow Falling", "minecraft:slow_falling", 40, PotionEffectDefinition.SLOW_FALLING);

    public static final PotionType SLOW_FALLING_LONG = new PotionType("Long Slow Falling", "minecraft:long_slow_falling", 41, PotionEffectDefinition.SLOW_FALLING_LONG);

    public static final PotionType SLOWNESS_STRONG = new PotionType("Strong Slowness", "minecraft:strong_slowness", 42, 2, PotionEffectDefinition.SLOWNESS_STRONG);

    public static final PotionType WIND_CHARGED = new PotionType("Wind Charged", "minecraft:wind_charged", 43, PotionEffectDefinition.EMPTY);

    public static final PotionType WEAVING = new PotionType("Weaving", "minecraft:weaving", 44, PotionEffectDefinition.EMPTY);

    public static final PotionType OOZING = new PotionType("Oozing", "minecraft:oozing", 45, PotionEffectDefinition.EMPTY);

    public static final PotionType INFESTED = new PotionType("Infested", "minecraft:infested", 46, PotionEffectDefinition.EMPTY);

    public List<Effect> getEffects(PotionApplicationMode mode) {
        return effects.getEffects(mode);
    }

    public void applyEffects(Entity entity, PotionApplicationMode mode, double health) {
        PotionApplyEvent event = new PotionApplyEvent(this, this.getEffects(mode), entity);
        Server.getInstance().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        for (Effect effect : event.getApplyEffects()) {
            int duration = (int) ((mode == PotionApplicationMode.SPLASH ? health : 1.0) * (double) effect.getDuration() + 0.5);
            effect.setDuration(duration);
            entity.addEffect(effect);
        }
    }

    public String getRomanLevel() {
        int currentLevel = this.level;
        if (currentLevel == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder(4);
        if (currentLevel < 0) {
            sb.append('-');
            currentLevel *= -1;
        }

        appendRoman(sb, currentLevel);
        return sb.toString();
    }

    private static void appendRoman(StringBuilder sb, int num) {
        String[] romans = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};
        int[] ints = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};

        for (int i = ints.length - 1; i >= 0; i--) {
            int times = num / ints[i];
            num %= ints[i];

            sb.append(romans[i].repeat(times));
        }
    }

    public static PotionType get(String stringId) {
        return Registries.POTION.get(stringId);
    }

    public static PotionType get(int id) {
        return Registries.POTION.get(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PotionType type) {
            return type.stringId.equals(this.stringId) && type.id == this.id;
        }
        return false;
    }
}
