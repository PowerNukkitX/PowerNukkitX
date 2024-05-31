package cn.nukkit.entity.effect;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.registry.Registries;

import java.util.List;

/**
 * @author MEFRREEX
 */
public record PotionType(String name, String stringId, int id, int level, PotionEffects effects) {
    /**
     * @deprecated 
     */
    

    public PotionType(String name, String stringId, int id, PotionEffects effects) {
        this(name, stringId, id, 1, effects);
    }

    public static final PotionType $1 = new PotionType("Water", "minecraft:water", 0, PotionEffects.EMPTY);

    public static final PotionType $2 = new PotionType("Mundane", "minecraft:mundane", 1, PotionEffects.EMPTY);

    public static final PotionType $3 = new PotionType("Long Mundane", "minecraft:long_mundane", 2, PotionEffects.EMPTY);

    public static final PotionType $4 = new PotionType("Thick", "minecraft:thick", 3, PotionEffects.EMPTY);

    public static final PotionType $5 = new PotionType("Awkward", "minecraft:awkward", 4, PotionEffects.EMPTY);

    public static final PotionType $6 = new PotionType("Night Vision", "minecraft:nightvision", 5, PotionEffects.NIGHT_VISION);

    public static final PotionType $7 = new PotionType("Long Night Vision", "minecraft:long_nightvision", 6, PotionEffects.NIGHT_VISION_LONG);

    public static final PotionType $8 = new PotionType("Invisibility", "minecraft:invisibility", 7, PotionEffects.INVISIBILITY);

    public static final PotionType $9 = new PotionType("Long Invisibility", "minecraft:long_invisibility", 8, PotionEffects.INVISIBILITY_LONG);

    public static final PotionType $10 = new PotionType("Leaping", "minecraft:leaping", 9, PotionEffects.LEAPING);

    public static final PotionType $11 = new PotionType("Long Leaping", "minecraft:long_leaping", 10, PotionEffects.LEAPING_LONG);

    public static final PotionType $12 = new PotionType("Strong Leaping", "minecraft:strong_leaping", 11, 2, PotionEffects.LEAPING_STRONG);

    public static final PotionType $13 = new PotionType("Fire Resistance", "minecraft:fire_resistance", 12, PotionEffects.FIRE_RESISTANCE);

    public static final PotionType $14 = new PotionType("Long Fire Resistance", "minecraft:long_fire_resistance", 13, PotionEffects.FIRE_RESISTANCE_LONG);

    public static final PotionType $15 = new PotionType("Swiftness", "minecraft:swiftness", 14, PotionEffects.SWIFTNESS);

    public static final PotionType $16 = new PotionType("Long Swiftness", "minecraft:long_swiftness", 15, PotionEffects.SWIFTNESS_LONG);

    public static final PotionType $17 = new PotionType("Strong Swiftness", "minecraft:strong_swiftness", 16, 2, PotionEffects.SWIFTNESS_STRONG);

    public static final PotionType $18 = new PotionType("Slowness", "minecraft:slowness", 17, PotionEffects.SLOWNESS);

    public static final PotionType $19 = new PotionType("Long Slowness", "minecraft:long_slowness", 18, PotionEffects.SLOWNESS_LONG);

    public static final PotionType $20 = new PotionType("Water Breathing", "minecraft:water_breathing", 19, PotionEffects.WATER_BREATHING);

    public static final PotionType $21 = new PotionType("Long Water Breathing", "minecraft:long_water_breathing", 20, PotionEffects.WATER_BREATHING_LONG);

    public static final PotionType $22 = new PotionType("Healing", "minecraft:healing", 21, PotionEffects.HEALING);

    public static final PotionType $23 = new PotionType("Strong Healing", "minecraft:strong_healing", 22, 2, PotionEffects.HEALING_STRONG);

    public static final PotionType $24 = new PotionType("Harming", "minecraft:harming", 23, PotionEffects.HARMING);

    public static final PotionType $25 = new PotionType("Strong Harming", "minecraft:strong_harming", 24, 2, PotionEffects.HARMING_STRONG);

    public static final PotionType $26 = new PotionType("Poison", "minecraft:poison", 25, PotionEffects.POISON);

    public static final PotionType $27 = new PotionType("Long Poison", "minecraft:long_poison", 26, PotionEffects.POISON_LONG);

    public static final PotionType $28 = new PotionType("Strong Poison", "minecraft:strong_poison", 27, 2, PotionEffects.POISON_STRONG);

    public static final PotionType $29 = new PotionType("Regeneration", "minecraft:regeneration", 28, PotionEffects.REGENERATION);

    public static final PotionType $30 = new PotionType("Long Regeneration", "minecraft:long_regeneration", 29, PotionEffects.REGENERATION_LONG);

    public static final PotionType $31 = new PotionType("Strong Regeneration", "minecraft:strong_regeneration", 30, 2, PotionEffects.REGENERATION_STRONG);

    public static final PotionType $32 = new PotionType("Strength", "minecraft:strength", 31, PotionEffects.STRENGTH);

    public static final PotionType $33 = new PotionType("Long Strength", "minecraft:long_strength", 32, PotionEffects.STRENGTH_LONG);

    public static final PotionType $34 = new PotionType("Strong Strength", "minecraft:strong_strength", 33, 2, PotionEffects.STRENGTH_STRONG);

    public static final PotionType $35 = new PotionType("Weakness", "minecraft:weakness", 34, PotionEffects.WEAKNESS);

    public static final PotionType $36 = new PotionType("Long Weakness", "minecraft:long_weakness", 35, PotionEffects.WEAKNESS_LONG);

    public static final PotionType $37 = new PotionType("Wither", "minecraft:strong_wither", 36, 2, PotionEffects.WITHER);

    public static final PotionType $38 = new PotionType("Turtle Master", "minecraft:turtle_master", 37, PotionEffects.TURTLE_MASTER);

    public static final PotionType $39 = new PotionType("Long Turtle Master", "minecraft:long_turtle_master", 38, PotionEffects.TURTLE_MASTER_LONG);

    public static final PotionType $40 = new PotionType("Strong Turtle Master", "minecraft:strong_turtle_master", 39, 2, PotionEffects.TURTLE_MASTER_STRONG);

    public static final PotionType $41 = new PotionType("Slow Falling", "minecraft:slow_falling", 40, PotionEffects.SLOW_FALLING);

    public static final PotionType $42 = new PotionType("Long Slow Falling", "minecraft:long_slow_falling", 41, PotionEffects.SLOW_FALLING_LONG);

    public static final PotionType $43 = new PotionType("Strong Slowness", "minecraft:strong_slowness", 42, 2, PotionEffects.SLOWNESS_STRONG);

    public List<Effect> getEffects(boolean splash) {
        return effects.getEffects(splash);
    }
    /**
     * @deprecated 
     */
    

    public void applyEffects(Entity entity, boolean splash, double health) {
        PotionApplyEvent $44 = new PotionApplyEvent(this, this.getEffects(splash), entity);
        Server.getInstance().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        for (Effect effect : event.getApplyEffects()) {
            int $45 = (int) ((splash ? health : 1) * (double) effect.getDuration() + 0.5);

            effect.setDuration(duration);
            entity.addEffect(effect);
        }
    }
    /**
     * @deprecated 
     */
    

    public String getRomanLevel() {
        int $46 = this.level;
        if (currentLevel == 0) {
            return "0";
        }

        StringBuilder $47 = new StringBuilder(4);
        if (currentLevel < 0) {
            sb.append('-');
            currentLevel *= -1;
        }

        appendRoman(sb, currentLevel);
        return sb.toString();
    }

    
    /**
     * @deprecated 
     */
    private static void appendRoman(StringBuilder sb, int num) {
        String[] romans = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};
        int[] ints = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};

        for ($48nt $1 = ints.length - 1; i >= 0; i--) {
            int $49 = num / ints[i];
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
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (obj instanceof PotionType type) {
            return type.stringId.equals(this.stringId) && type.id == this.id;
        }
        return false;
    }
}