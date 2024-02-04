package cn.nukkit.entity.effect;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author MEFRREEX
 */
public record PotionEffects(Function<Boolean, List<Effect>> supplier) {

    public static final PotionEffects EMPTY = new PotionEffects(splash -> Collections.emptyList());

    public static final PotionEffects NIGHT_VISION = new PotionEffects(splash -> List.of(
            new EffectNightVision()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects NIGHT_VISION_LONG = new PotionEffects(splash -> List.of(
            new EffectNightVision()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects INVISIBILITY = new PotionEffects(splash -> List.of(
            new EffectInvisibility()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects INVISIBILITY_LONG = new PotionEffects(splash -> List.of(
            new EffectInvisibility()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects LEAPING = new PotionEffects(splash -> List.of(
            new EffectJumpBoost()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects LEAPING_LONG = new PotionEffects(splash -> List.of(
            new EffectJumpBoost()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects LEAPING_STRONG = new PotionEffects(splash -> List.of(
            new EffectJumpBoost()
                    .setDuration(splash ? 1340 : 1800)
                    .setAmplifier(1)
    ));

    public static final PotionEffects FIRE_RESISTANCE = new PotionEffects(splash -> List.of(
            new EffectFireResistance()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects FIRE_RESISTANCE_LONG = new PotionEffects(splash -> List.of(
            new EffectFireResistance()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects SWIFTNESS = new PotionEffects(splash -> List.of(
            new EffectSpeed()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects SWIFTNESS_LONG = new PotionEffects(splash -> List.of(
            new EffectSpeed()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects SWIFTNESS_STRONG = new PotionEffects(splash -> List.of(
            new EffectSpeed()
                    .setDuration(splash ? 1340 : 1800)
                    .setAmplifier(1)
    ));

    public static final PotionEffects SLOWNESS = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 1340 : 1800)
    ));

    public static final PotionEffects SLOWNESS_LONG = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 3600 : 4800)
    ));

    public static final PotionEffects WATER_BREATHING = new PotionEffects(splash -> List.of(
            new EffectWaterBreathing()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects WATER_BREATHING_LONG = new PotionEffects(splash -> List.of(
            new EffectWaterBreathing()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects HEALING = new PotionEffects(splash -> List.of(new EffectInstantHealth()));

    public static final PotionEffects HEALING_STRONG = new PotionEffects(splash -> List.of(
            new EffectInstantHealth()
                    .setAmplifier(1)
    ));

    public static final PotionEffects HARMING = new PotionEffects(splash -> List.of(new EffectInstantDamage()));

    public static final PotionEffects HARMING_STRONG = new PotionEffects(splash -> List.of(
            new EffectInstantDamage()
                    .setAmplifier(1)
    ));

    public static final PotionEffects POISON = new PotionEffects(splash -> List.of(
            new EffectPoison()
                    .setDuration(splash ? 660 : 900)
    ));

    public static final PotionEffects POISON_LONG = new PotionEffects(splash -> List.of(
            new EffectPoison()
                    .setDuration(splash ? 1800 : 2400)
    ));

    public static final PotionEffects POISON_STRONG = new PotionEffects(splash -> List.of(
            new EffectPoison()
                    .setDuration(splash ? 320 : 440)
                    .setAmplifier(1)
    ));

    public static final PotionEffects REGENERATION = new PotionEffects(splash -> List.of(
            new EffectRegeneration()
                    .setDuration(splash ? 660 : 900)
    ));

    public static final PotionEffects REGENERATION_LONG = new PotionEffects(splash -> List.of(
            new EffectRegeneration()
                    .setDuration(splash ? 1800 : 2400)
    ));

    public static final PotionEffects REGENERATION_STRONG = new PotionEffects(splash -> List.of(
            new EffectRegeneration()
                    .setDuration(splash ? 320 : 440)
                    .setAmplifier(1)
    ));

    public static final PotionEffects STRENGTH = new PotionEffects(splash -> List.of(
            new EffectStrength()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects STRENGTH_LONG = new PotionEffects(splash -> List.of(
            new EffectStrength()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects STRENGTH_STRONG = new PotionEffects(splash -> List.of(
            new EffectStrength()
                    .setDuration(splash ? 1340 : 1800)
                    .setAmplifier(1)
    ));

    public static final PotionEffects WEAKNESS = new PotionEffects(splash -> List.of(
            new EffectWeakness()
                    .setDuration(splash ? 1340 : 1800)
    ));

    public static final PotionEffects WEAKNESS_LONG = new PotionEffects(splash -> List.of(
            new EffectWeakness()
                    .setDuration(splash ? 3600 : 4800)
    ));

    public static final PotionEffects WITHER = new PotionEffects(splash -> List.of(
            new EffectWither()
                    .setDuration(splash ? 600 : 800)
                    .setAmplifier(1)
    ));

    public static final PotionEffects TURTLE_MASTER = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(3),
            new EffectResistance()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(2)
    ));

    public static final PotionEffects TURTLE_MASTER_LONG = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 600 : 800)
                    .setAmplifier(3),
            new EffectResistance()
                    .setDuration(splash ? 600 : 800)
                    .setAmplifier(2)
    ));

    public static final PotionEffects TURTLE_MASTER_STRONG = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(5),
            new EffectResistance()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(3)
    ));

    public static final PotionEffects SLOW_FALLING = new PotionEffects(splash -> List.of(
            new EffectSlowFalling()
                    .setDuration(splash ? 1340 : 1800)
    ));

    public static final PotionEffects SLOW_FALLING_LONG = new PotionEffects(splash -> List.of(
            new EffectSlowFalling()
                    .setDuration(splash ? 3600 : 4800)
    ));

    public static final PotionEffects SLOWNESS_STRONG = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(3)
    ));

    public List<Effect> getEffects(boolean splash) {
        return supplier.apply(splash);
    }
}
