package cn.nukkit.potion;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.InvalidIdentifierException;
import cn.nukkit.utils.ServerException;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "FUTURE", info = "Implements equals() and hashcode() only in PowerNukkit")
@EqualsAndHashCode
public class Potion implements Cloneable {
    private static final Map<Identifier, Potion> potionsMap = new LinkedHashMap<>();
    public static final int NO_EFFECTS = 0;
    public static final int WATER = 0;
    public static final int MUNDANE = 1;
    public static final int MUNDANE_II = 2;
    public static final int THICK = 3;
    public static final int AWKWARD = 4;
    public static final int NIGHT_VISION = 5;
    public static final int NIGHT_VISION_LONG = 6;
    public static final int INVISIBLE = 7;
    public static final int INVISIBLE_LONG = 8;
    public static final int LEAPING = 9;
    public static final int LEAPING_LONG = 10;
    public static final int LEAPING_II = 11;
    public static final int FIRE_RESISTANCE = 12;
    public static final int FIRE_RESISTANCE_LONG = 13;
    public static final int SPEED = 14;
    public static final int SPEED_LONG = 15;
    public static final int SPEED_II = 16;
    public static final int SLOWNESS = 17;
    public static final int SLOWNESS_LONG = 18;
    public static final int WATER_BREATHING = 19;
    public static final int WATER_BREATHING_LONG = 20;
    public static final int INSTANT_HEALTH = 21;
    public static final int INSTANT_HEALTH_II = 22;
    public static final int HARMING = 23;
    public static final int HARMING_II = 24;
    public static final int POISON = 25;
    public static final int POISON_LONG = 26;
    public static final int POISON_II = 27;
    public static final int REGENERATION = 28;
    public static final int REGENERATION_LONG = 29;
    public static final int REGENERATION_II = 30;
    public static final int STRENGTH = 31;
    public static final int STRENGTH_LONG = 32;
    public static final int STRENGTH_II = 33;
    public static final int WEAKNESS = 34;
    public static final int WEAKNESS_LONG = 35;
    public static final int WITHER_II = 36;
    public static final int TURTLE_MASTER = 37;
    public static final int TURTLE_MASTER_LONG = 38;
    public static final int TURTLE_MASTER_II = 39;
    public static final int SLOW_FALLING = 40;
    public static final int SLOW_FALLING_LONG = 41;
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public static final int SLOWNESS_IV = 42;
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(since = "FUTURE", by = "PowerNukkit", reason =
            "Incorrect name, there is vanilla potion with slowness long 2, the result of potion with slowness 1 + glowstone is slowness 4", replaceWith = "SLOWNESS_IV")
    public static final int SLOWNESS_LONG_II = SLOWNESS_IV;

    protected static Potion[] potions;

    public static void init() {
        potionsMap.put(Identifier.tryParse("minecraft:water"), new Potion(Potion.WATER));
        potionsMap.put(Identifier.tryParse("minecraft:mundane"), new Potion(Potion.MUNDANE));
        potionsMap.put(Identifier.tryParse("minecraft:long_mundane"), new Potion(Potion.MUNDANE_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:thick"), new Potion(Potion.THICK));
        potionsMap.put(Identifier.tryParse("minecraft:awkward"), new Potion(Potion.AWKWARD));
        potionsMap.put(Identifier.tryParse("minecraft:nightvision"), new Potion(Potion.NIGHT_VISION));
        potionsMap.put(Identifier.tryParse("minecraft:long_nightvision"), new Potion(Potion.NIGHT_VISION_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:invisibility"), new Potion(Potion.INVISIBLE));
        potionsMap.put(Identifier.tryParse("minecraft:long_invisibility"), new Potion(Potion.INVISIBLE_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:leaping"), new Potion(Potion.LEAPING));
        potionsMap.put(Identifier.tryParse("minecraft:long_leaping"), new Potion(Potion.LEAPING_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:strong_leaping"), new Potion(Potion.LEAPING_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:fire_resistance"), new Potion(Potion.FIRE_RESISTANCE));
        potionsMap.put(Identifier.tryParse("minecraft:long_fire_resistance"), new Potion(Potion.FIRE_RESISTANCE_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:swiftness"), new Potion(Potion.SPEED));
        potionsMap.put(Identifier.tryParse("minecraft:long_swiftness"), new Potion(Potion.SPEED_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:strong_swiftness"), new Potion(Potion.SPEED_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:slowness"), new Potion(Potion.SLOWNESS));
        potionsMap.put(Identifier.tryParse("minecraft:long_slowness"), new Potion(Potion.SLOWNESS_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:water_breathing"), new Potion(Potion.WATER_BREATHING));
        potionsMap.put(Identifier.tryParse("minecraft:long_water_breathing"), new Potion(Potion.WATER_BREATHING_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:healing"), new Potion(Potion.INSTANT_HEALTH));
        potionsMap.put(Identifier.tryParse("minecraft:strong_healing"), new Potion(Potion.INSTANT_HEALTH_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:harming"), new Potion(Potion.HARMING));
        potionsMap.put(Identifier.tryParse("minecraft:strong_harming"), new Potion(Potion.HARMING_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:poison"), new Potion(Potion.POISON));
        potionsMap.put(Identifier.tryParse("minecraft:long_poison"), new Potion(Potion.POISON_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:strong_poison"), new Potion(Potion.POISON_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:regeneration"), new Potion(Potion.REGENERATION));
        potionsMap.put(Identifier.tryParse("minecraft:long_regeneration"), new Potion(Potion.REGENERATION_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:strong_regeneration"), new Potion(Potion.REGENERATION_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:strength"), new Potion(Potion.STRENGTH));
        potionsMap.put(Identifier.tryParse("minecraft:long_strength"), new Potion(Potion.STRENGTH_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:strong_strength"), new Potion(Potion.STRENGTH_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:weakness"), new Potion(Potion.WEAKNESS));
        potionsMap.put(Identifier.tryParse("minecraft:long_weakness"), new Potion(Potion.WEAKNESS_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:strong_wither"), new Potion(Potion.WITHER_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:turtle_master"), new Potion(Potion.TURTLE_MASTER));
        potionsMap.put(Identifier.tryParse("minecraft:long_turtle_master"), new Potion(Potion.TURTLE_MASTER_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:strong_turtle_master"), new Potion(Potion.TURTLE_MASTER_II, 2));
        potionsMap.put(Identifier.tryParse("minecraft:slow_falling"), new Potion(Potion.SLOW_FALLING));
        potionsMap.put(Identifier.tryParse("minecraft:long_slow_falling"), new Potion(Potion.SLOW_FALLING_LONG));
        potionsMap.put(Identifier.tryParse("minecraft:strong_slowness"), new Potion(Potion.SLOWNESS_IV, 4));
        potions = potionsMap.values().toArray(new Potion[256]);
    }

    public static Potion getPotion(int id) {
        if (id >= 0 && id < potionsMap.size() && potions[id] != null) {
            return potions[id].clone();
        } else {
            throw new ServerException("Effect id: " + id + " not found");
        }
    }

    public static Potion getPotionByName(String name) {
        try {
            Potion potion = potionsMap.get(Identifier.tryParse(name));
            if (potion != null) {
                return potion;
            }
        } catch (InvalidIdentifierException ignore) {
        }

        try {
            byte id = Potion.class.getField(name.toUpperCase()).getByte(null);
            return getPotion(id);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected final int id;

    protected final int level;

    protected boolean splash = false;

    public Potion(int id) {
        this(id, 1);
    }

    public Potion(int id, int level) {
        this(id, level, false);
    }

    public Potion(int id, int level, boolean splash) {
        this.id = id;
        this.level = level;
        this.splash = splash;
    }

    public Effect getEffect() {
        return getEffect(this.getId(), this.isSplash());
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public boolean isSplash() {
        return splash;
    }

    public Potion setSplash(boolean splash) {
        this.splash = splash;
        return this;
    }

    public void applyPotion(Entity entity) {
        applyPotion(entity, 0.5);
    }

    public void applyPotion(Entity entity, double health) {
        if (!(entity instanceof EntityLiving)) {
            return;
        }

        Effect applyEffect = getEffect(this.getId(), this.isSplash());

        if (applyEffect == null) {
            return;
        }

        if (entity instanceof Player) {
            if (!((Player) entity).isSurvival() && !((Player) entity).isAdventure() && applyEffect.isBad()) {
                return;
            }
        }

        PotionApplyEvent event = new PotionApplyEvent(this, applyEffect, entity);

        entity.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        applyEffect = event.getApplyEffect();

        switch (this.getId()) {
            case INSTANT_HEALTH, INSTANT_HEALTH_II -> {
                if (entity.isUndead())
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, (float) (health * (double) (6 << (applyEffect.getAmplifier() + 1)))));
                else
                    entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
            }
            case HARMING, HARMING_II -> {
                if (entity.isUndead())
                    entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                else
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, (float) (health * (double) (6 << (applyEffect.getAmplifier() + 1)))));
            }
            default -> {
                int duration = (int) ((isSplash() ? health : 1) * (double) applyEffect.getDuration() + 0.5);
                applyEffect.setDuration(duration);
                entity.addEffect(applyEffect);
            }
        }
    }

    @Override
    public Potion clone() {
        try {
            return (Potion) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public static Effect getEffect(int potionType, boolean isSplash) {
        Effect effect;
        switch (potionType) {
            case NIGHT_VISION, NIGHT_VISION_LONG -> effect = Effect.getEffect(Effect.NIGHT_VISION);
            case INVISIBLE, INVISIBLE_LONG -> effect = Effect.getEffect(Effect.INVISIBILITY);
            case LEAPING, LEAPING_LONG, LEAPING_II -> effect = Effect.getEffect(Effect.JUMP_BOOST);
            case FIRE_RESISTANCE, FIRE_RESISTANCE_LONG -> effect = Effect.getEffect(Effect.FIRE_RESISTANCE);
            case SPEED, SPEED_LONG, SPEED_II -> effect = Effect.getEffect(Effect.SPEED);
            case SLOWNESS, SLOWNESS_LONG, SLOWNESS_IV -> effect = Effect.getEffect(Effect.SLOWNESS);
            case WATER_BREATHING, WATER_BREATHING_LONG -> effect = Effect.getEffect(Effect.WATER_BREATHING);
            case INSTANT_HEALTH, INSTANT_HEALTH_II -> {
                return Effect.getEffect(Effect.INSTANT_HEALTH);
            }
            case HARMING, HARMING_II -> {
                return Effect.getEffect(Effect.INSTANT_DAMAGE);
            }
            case POISON, POISON_LONG, POISON_II -> effect = Effect.getEffect(Effect.POISON);
            case REGENERATION, REGENERATION_LONG, REGENERATION_II -> effect = Effect.getEffect(Effect.REGENERATION);
            case STRENGTH, STRENGTH_LONG, STRENGTH_II -> effect = Effect.getEffect(Effect.STRENGTH);
            case WEAKNESS, WEAKNESS_LONG -> effect = Effect.getEffect(Effect.WEAKNESS);
            case WITHER_II -> effect = Effect.getEffect(Effect.WITHER);
            default -> {
                return null;
            }
        }

        if (getLevel(potionType) > 1) {
            effect.setAmplifier(1);
        }

        if (!isInstant(potionType)) {
            effect.setDuration(20 * getApplySeconds(potionType, isSplash));
        }

        return effect;
    }

    public static int getLevel(int potionType) {
        return switch (potionType) {
            case SLOWNESS_IV -> 4;
            case MUNDANE_II, LEAPING_II, SPEED_II, INSTANT_HEALTH_II, HARMING_II, POISON_II, REGENERATION_II, STRENGTH_II, WITHER_II, TURTLE_MASTER_II ->
                    2;
            default -> 1;
        };
    }

    public static boolean isInstant(int potionType) {
        return switch (potionType) {
            case INSTANT_HEALTH, INSTANT_HEALTH_II, HARMING, HARMING_II -> true;
            default -> false;
        };
    }

    public static int getApplySeconds(int potionType, boolean isSplash) {
        if (isSplash) {
            return switch (potionType) {
                case NIGHT_VISION, STRENGTH, WATER_BREATHING, SPEED, FIRE_RESISTANCE, LEAPING, INVISIBLE -> 135;
                case NIGHT_VISION_LONG, STRENGTH_LONG, WATER_BREATHING_LONG, SPEED_LONG, FIRE_RESISTANCE_LONG, LEAPING_LONG, INVISIBLE_LONG ->
                        360;
                case LEAPING_II, WEAKNESS, STRENGTH_II, SLOWNESS, SPEED_II -> 67;
                case SLOWNESS_LONG, WEAKNESS_LONG -> 180;
                case POISON, REGENERATION -> 33;
                case POISON_LONG, REGENERATION_LONG -> 90;
                case POISON_II, REGENERATION_II -> 16;
                case WITHER_II -> 30;
                case SLOWNESS_IV -> 15;
                default -> 0;
            };
        } else {
            return switch (potionType) {
                case NIGHT_VISION, STRENGTH, WATER_BREATHING, SPEED, FIRE_RESISTANCE, LEAPING, INVISIBLE -> 180;
                case NIGHT_VISION_LONG, STRENGTH_LONG, WATER_BREATHING_LONG, SPEED_LONG, FIRE_RESISTANCE_LONG, LEAPING_LONG, INVISIBLE_LONG ->
                        480;
                case LEAPING_II, WEAKNESS, STRENGTH_II, SLOWNESS -> 90;
                case SLOWNESS_LONG, WEAKNESS_LONG -> 240;
                case POISON, REGENERATION -> 45;
                case POISON_LONG, REGENERATION_LONG -> 120;
                case POISON_II, REGENERATION_II -> 22;
                case WITHER_II -> 30;
                case SLOWNESS_IV -> 20;
                case SPEED_II -> 90;
                default -> 0;
            };
        }
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @NotNull
    public String getPotionTypeName() {
        return switch (getId()) {
            case WATER -> "Water";
            case MUNDANE, MUNDANE_II -> "Mundane";
            case THICK -> "Thick";
            case AWKWARD -> "Awkward";
            case NIGHT_VISION_LONG, NIGHT_VISION -> "Night Vision";
            case INVISIBLE, INVISIBLE_LONG -> "Invisibility";
            case LEAPING_LONG, LEAPING_II, LEAPING -> "Leaping";
            case FIRE_RESISTANCE_LONG, FIRE_RESISTANCE -> "Fire Resistance";
            case SPEED, SPEED_LONG, SPEED_II -> "Swiftness";
            case SLOWNESS_LONG, SLOWNESS, SLOWNESS_IV -> "Slowness";
            case WATER_BREATHING_LONG, WATER_BREATHING -> "Water Breathing";
            case INSTANT_HEALTH, INSTANT_HEALTH_II -> "Healing";
            case HARMING, HARMING_II -> "Harming";
            case POISON, POISON_LONG, POISON_II -> "Poison";
            case REGENERATION, REGENERATION_LONG, REGENERATION_II -> "Regeneration";
            case STRENGTH, STRENGTH_LONG, STRENGTH_II -> "Strength";
            case WEAKNESS, WEAKNESS_LONG -> "Weakness";
            case WITHER_II -> "Decay";
            case TURTLE_MASTER, TURTLE_MASTER_LONG, TURTLE_MASTER_II -> "Turtle Master";
            case SLOW_FALLING, SLOW_FALLING_LONG -> "Slow Falling";
            default -> "";
        };
    }

    @PowerNukkitOnly
    @NotNull
    public String getName() {
        String name = getPotionTypeName();
        StringBuilder finalName = new StringBuilder(255).append("Potion");
        if (!name.isEmpty()) {
            int id = getId();
            if (id >= TURTLE_MASTER && id <= TURTLE_MASTER_II) {
                finalName.append(" of the ").append(name);
            } else if (id <= AWKWARD) {
                finalName.insert(0, name + " ");
            } else {
                finalName.append(" of ").append(name);
            }
        }

        int currentLevel = getLevel();
        if (currentLevel > 1) {
            finalName.append(' ');
            appendRoman(finalName, currentLevel);
        }
        return finalName.toString();
    }

    @PowerNukkitOnly
    @NotNull
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
        int times;
        String[] romans = new String[]{"I", "IV", "V", "IX", "X", "XL", "L",
                "XC", "C", "CD", "D", "CM", "M"};
        int[] ints = new int[]{1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500,
                900, 1000};
        for (int i = ints.length - 1; i >= 0; i--) {
            times = num / ints[i];
            num %= ints[i];
            while (times > 0) {
                sb.append(romans[i]);
                times--;
            }
        }
    }
}
