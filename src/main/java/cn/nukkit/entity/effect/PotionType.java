package cn.nukkit.entity.effect;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.potion.PotionApplyEvent;

import java.util.*;

/**
 * @author MEFRREEX
 */
public enum PotionType {
    WATER("Water", "minecraft:water"),
    MUNDANE("Mundane", "minecraft:mundane"),
    MUNDANE_LONG("Long Mundane", "minecraft:long_mundane") ,
    THICK("Thick", "minecraft:thick"),
    AWKWARD("Awkward", "minecraft:awkward"),
    NIGHT_VISION("Night Vision", "minecraft:nightvision") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectNightVision()
                            .setDuration(splash ? 2700 : 3600));
        }
    },
    NIGHT_VISION_LONG("Long Night Vision", "minecraft:long_nightvision") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectNightVision()
                            .setDuration(splash ? 7200 : 9600)
            );
        }
    },
    INVISIBILITY("Invisibility", "minecraft:invisibility") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectInvisibility()
                            .setDuration(splash ? 2700 : 3600)
            );
        }
    },
    INVISIBILITY_LONG("Long Invisibility", "minecraft:long_invisibility") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectInvisibility()
                            .setDuration(splash ? 7200 : 9600)
            );
        }
    },
    LEAPING("Leaping", "minecraft:leaping") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectJumpBoost()
                            .setDuration(splash ? 2700 : 3600)
            );
        }
    },
    LEAPING_LONG("Long Leaping", "minecraft:long_leaping") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectJumpBoost()
                            .setDuration(splash ? 7200 : 9600)
            );
        }
    },
    LEAPING_STRONG("Strong Leaping", "minecraft:strong_leaping", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectJumpBoost()
                            .setDuration(splash ? 1340 : 1800)
                            .setAmplifier(1)
            );
        }
    },
    FIRE_RESISTANCE("Fire Resistance", "minecraft:fire_resistance") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectFireResistance()
                            .setDuration(splash ? 2700 : 3600)
            );
        }
    },
    FIRE_RESISTANCE_LONG("Long Fire Resistance", "minecraft:long_fire_resistance") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectFireResistance()
                            .setDuration(splash ? 7200 : 9600)
            );
        }
    },
    SWIFTNESS("Swiftness", "minecraft:swiftness") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSpeed()
                            .setDuration(splash ? 2700 : 3600)
            );
        }
    },
    SWIFTNESS_LONG("Long Swiftness", "minecraft:long_swiftness") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSpeed()
                            .setDuration(splash ? 7200 : 9600)
            );
        }
    },
    SWIFTNESS_STRONG("Strong Swiftness", "minecraft:strong_swiftness", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSpeed()
                            .setDuration(splash ? 1340 : 1800)
                            .setAmplifier(1)
            );
        }
    },
    SLOWNESS("Slowness", "minecraft:slowness") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSlowness()
                            .setDuration(splash ? 1340 : 1800)
            );
        }
    },
    SLOWNESS_LONG("Long Slowness", "minecraft:long_slowness") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSlowness()
                            .setDuration(splash ? 3600 : 4800)
            );
        }
    },
    WATER_BREATHING("Water Breathing", "minecraft:water_breathing") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectWaterBreathing()
                            .setDuration(splash ? 2700 : 3600)
            );
        }
    },
    WATER_BREATHING_LONG("Long Water Breathing", "minecraft:long_water_breathing") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectWaterBreathing()
                            .setDuration(splash ? 7200 : 9600)
            );
        }
    },
    HEALING("Healing", "minecraft:healing") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(new EffectInstantHealth());
        }
    },
    HEALING_STRONG("Strong Healing", "minecraft:strong_healing", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectInstantHealth()
                            .setAmplifier(1)
            );
        }
    },
    HARMING("Harming", "minecraft:harming") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(new EffectInstantDamage());
        }
    },
    HARMING_STRONG("Strong Harming", "minecraft:strong_harming", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectInstantDamage()
                            .setAmplifier(1)
            );
        }
    },
    POISON("Poison", "minecraft:poison") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectPoison()
                            .setDuration(splash ? 660 : 900)
            );
        }
    },
    POISON_LONG("Long Poison", "minecraft:long_poison") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectPoison()
                            .setDuration(splash ? 1800 : 2400)
            );
        }
    },
    POISON_STRONG("Strong Poison", "minecraft:strong_poison", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectPoison()
                            .setDuration(splash ? 320 : 440)
                            .setAmplifier(1)
            );
        }
    },
    REGENERATION("Regeneration", "minecraft:regeneration") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectRegeneration()
                            .setDuration(splash ? 660 : 900)
            );
        }
    },
    REGENERATION_LONG("Long Regeneration", "minecraft:long_regeneration") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectRegeneration()
                            .setDuration(splash ? 1800 : 2400)
            );
        }
    },
    REGENERATION_STRONG("Strong Regeneration", "minecraft:strong_regeneration", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectRegeneration()
                            .setDuration(splash ? 320 : 440)
                            .setAmplifier(1)
            );
        }
    },
    STRENGTH("Strength", "minecraft:strength") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectStrength()
                            .setDuration(splash ? 2700 : 3600)
            );
        }
    },
    STRENGTH_LONG("Long Strength", "minecraft:long_strength") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectStrength()
                            .setDuration(splash ? 7200 : 9600)
            );
        }
    },
    STRENGTH_STRONG("Strong Strength", "minecraft:strong_strength", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectStrength()
                            .setDuration(splash ? 1340 : 1800)
                            .setAmplifier(1)
            );
        }
    },
    WEAKNESS("Weakness", "minecraft:weakness") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectWeakness()
                            .setDuration(splash ? 1340 : 1800)
            );
        }
    },
    WEAKNESS_LONG("Long Weakness", "minecraft:long_weakness") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectWeakness()
                            .setDuration(splash ? 3600 : 4800)
            );
        }
    },
    WITHER("Wither", "minecraft:strong_wither", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectWither()
                            .setDuration(splash ? 600 : 800)
                            .setAmplifier(1)
            );
        }
    },
    TURTLE_MASTER("Turtle Master", "minecraft:turtle_master") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSlowness()
                            .setDuration(splash ? 300 : 400)
                            .setAmplifier(3),
                    new EffectResistance()
                            .setDuration(splash ? 300 : 400)
                            .setAmplifier(2)
            );
        }
    },
    TURTLE_MASTER_LONG("Long Turtle Master", "minecraft:long_turtle_master") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSlowness()
                            .setDuration(splash ? 600 : 800)
                            .setAmplifier(3),
                    new EffectResistance()
                            .setDuration(splash ? 600 : 800)
                            .setAmplifier(2)
            );
        }
    },
    TURTLE_MASTER_STRONG("Strong Turtle Master", "minecraft:strong_turtle_master", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSlowness()
                            .setDuration(splash ? 300 : 400)
                            .setAmplifier(5),
                    new EffectResistance()
                            .setDuration(splash ? 300 : 400)
                            .setAmplifier(3)
            );
        }
    },
    SLOW_FALLING("Slow Falling", "minecraft:slow_falling") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSlowFalling()
                        .setDuration(splash ? 1340 : 1800)
            );
        }
    },
    SLOW_FALLING_LONG("Long Slow Falling", "minecraft:long_slow_falling") {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSlowFalling()
                            .setDuration(splash ? 3600 : 4800)
            );
        }
    },
    SLOWNESS_STRONG("Strong Slowness", "minecraft:strong_slowness", 2) {
        @Override
        public List<Effect> getEffects(boolean splash) {
            return List.of(
                    new EffectSlowness()
                            .setDuration(splash ? 300 : 400)
                            .setAmplifier(3)
            );
        }
    };

    private final String name;
    private final String id;
    private final int level;

    private static final Map<Integer, PotionType> BY_ORDINAL = new HashMap<>();
    private static final Map<String, PotionType> BY_ID = new HashMap<>();

    PotionType(String name, String id) {
        this(name, id, 1);
    }

    PotionType(String name, String id, int level) {
        this.name = name;
        this.id = id;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public List<Effect> getEffects(boolean splash) {
        return new ArrayList<>();
    }

    public void applyEffects(Entity entity, boolean splash, double health) {
        PotionApplyEvent event = new PotionApplyEvent(this, this.getEffects(splash), entity);
        Server.getInstance().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        for (Effect effect : event.getApplyEffects()) {
            int duration = (int) ((splash ? health : 1) * (double) effect.getDuration() + 0.5);

            effect.setDuration(duration);
            entity.addEffect(effect);
        }
    }

    public String getRomanLevel() {
        int currentLevel = getLevel();
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


    public static PotionType get(int ordinal) {
        return BY_ORDINAL.get(ordinal);
    }

    public static PotionType get(String id) {
        return BY_ID.get(id);
    }

    static {
        for (PotionType type : values()) {
            BY_ORDINAL.put(type.ordinal(), type);
            BY_ID.put(type.getId(), type);
        }
    }
}