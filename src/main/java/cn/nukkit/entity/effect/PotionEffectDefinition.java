package cn.nukkit.entity.effect;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Defines the effects (type, amplifier, duration) that a potion type grants,
 * differentiated by {@link PotionApplicationMode} (drink, splash, lingering, or arrow).
 * <p>
 * Duration rules (vanilla):
 * <ul>
 *   <li>SPLASH   = drink (proximity scaling applied at the call site)</li>
 *   <li>LINGERING = drink / 4</li>
 *   <li>ARROW    = drink / 8</li>
 * </ul>
 * Use {@link #of(int, IntFunction)} to define timed effects without repeating every variant;
 * the base (drink) duration is automatically scaled by {@link PotionApplicationMode#scaleDuration}.
 *
 * @author MEFRREEX
 */
public record PotionEffectDefinition(Function<PotionApplicationMode, List<Effect>> supplier) {

    public static final PotionEffectDefinition EMPTY = new PotionEffectDefinition(mode -> Collections.emptyList());

    /**
     * Creates a definition whose effect durations are derived from {@code baseDuration} (drink ticks).
     *
     * @param baseDuration drink-mode duration in ticks
     * @param effectFactory receives the scaled duration for the given mode and returns the effect list
     */
    public static PotionEffectDefinition of(int baseDuration, IntFunction<List<Effect>> effectFactory) {
        return new PotionEffectDefinition(mode -> effectFactory.apply(mode.scaleDuration(baseDuration)));
    }

    public static final PotionEffectDefinition NIGHT_VISION = of(3600, duration -> List.of(
            new EffectNightVision().setDuration(duration)
    ));

    public static final PotionEffectDefinition NIGHT_VISION_LONG = of(9600, duration -> List.of(
            new EffectNightVision().setDuration(duration)
    ));

    public static final PotionEffectDefinition INVISIBILITY = of(3600, duration -> List.of(
            new EffectInvisibility().setDuration(duration)
    ));

    public static final PotionEffectDefinition INVISIBILITY_LONG = of(9600, duration -> List.of(
            new EffectInvisibility().setDuration(duration)
    ));

    public static final PotionEffectDefinition LEAPING = of(3600, duration -> List.of(
            new EffectJumpBoost().setDuration(duration)
    ));

    public static final PotionEffectDefinition LEAPING_LONG = of(9600, duration -> List.of(
            new EffectJumpBoost().setDuration(duration)
    ));

    public static final PotionEffectDefinition LEAPING_STRONG = of(1800, duration -> List.of(
            new EffectJumpBoost().setDuration(duration).setAmplifier(1)
    ));

    public static final PotionEffectDefinition FIRE_RESISTANCE = of(3600, duration -> List.of(
            new EffectFireResistance().setDuration(duration)
    ));

    public static final PotionEffectDefinition FIRE_RESISTANCE_LONG = of(9600, duration -> List.of(
            new EffectFireResistance().setDuration(duration)
    ));

    public static final PotionEffectDefinition SWIFTNESS = of(3600, duration -> List.of(
            new EffectSpeed().setDuration(duration)
    ));

    public static final PotionEffectDefinition SWIFTNESS_LONG = of(9600, duration -> List.of(
            new EffectSpeed().setDuration(duration)
    ));

    public static final PotionEffectDefinition SWIFTNESS_STRONG = of(1800, duration -> List.of(
            new EffectSpeed().setDuration(duration).setAmplifier(1)
    ));

    public static final PotionEffectDefinition SLOWNESS = of(1800, duration -> List.of(
            new EffectSlowness().setDuration(duration)
    ));

    public static final PotionEffectDefinition SLOWNESS_LONG = of(4800, duration -> List.of(
            new EffectSlowness().setDuration(duration)
    ));

    public static final PotionEffectDefinition SLOWNESS_STRONG = of(400, duration -> List.of(
            new EffectSlowness().setDuration(duration).setAmplifier(3)
    ));

    public static final PotionEffectDefinition WATER_BREATHING = of(3600, duration -> List.of(
            new EffectWaterBreathing().setDuration(duration)
    ));

    public static final PotionEffectDefinition WATER_BREATHING_LONG = of(9600, duration -> List.of(
            new EffectWaterBreathing().setDuration(duration)
    ));

    // -------------------------------------------------------------------
    // Instant effects — duration irrelevant, amplifier varies by strength

    public static final PotionEffectDefinition HEALING = new PotionEffectDefinition(mode -> List.of(
            new EffectInstantHealth()
    ));

    public static final PotionEffectDefinition HEALING_STRONG = new PotionEffectDefinition(mode -> List.of(
            new EffectInstantHealth().setAmplifier(1)
    ));

    public static final PotionEffectDefinition HARMING = new PotionEffectDefinition(mode -> List.of(
            new EffectInstantDamage()
    ));

    public static final PotionEffectDefinition HARMING_STRONG = new PotionEffectDefinition(mode -> List.of(
            new EffectInstantDamage().setAmplifier(1)
    ));

    // -------------------------------------------------------------------

    public static final PotionEffectDefinition POISON = of(900, duration -> List.of(
            new EffectPoison().setDuration(duration)
    ));

    public static final PotionEffectDefinition POISON_LONG = of(2400, duration -> List.of(
            new EffectPoison().setDuration(duration)
    ));

    public static final PotionEffectDefinition POISON_STRONG = of(440, duration -> List.of(
            new EffectPoison().setDuration(duration).setAmplifier(1)
    ));

    public static final PotionEffectDefinition REGENERATION = of(900, duration -> List.of(
            new EffectRegeneration().setDuration(duration)
    ));

    public static final PotionEffectDefinition REGENERATION_LONG = of(2400, duration -> List.of(
            new EffectRegeneration().setDuration(duration)
    ));

    public static final PotionEffectDefinition REGENERATION_STRONG = of(440, duration -> List.of(
            new EffectRegeneration().setDuration(duration).setAmplifier(1)
    ));

    public static final PotionEffectDefinition STRENGTH = of(3600, duration -> List.of(
            new EffectStrength().setDuration(duration)
    ));

    public static final PotionEffectDefinition STRENGTH_LONG = of(9600, duration -> List.of(
            new EffectStrength().setDuration(duration)
    ));

    public static final PotionEffectDefinition STRENGTH_STRONG = of(1800, duration -> List.of(
            new EffectStrength().setDuration(duration).setAmplifier(1)
    ));

    public static final PotionEffectDefinition WEAKNESS = of(1800, duration -> List.of(
            new EffectWeakness().setDuration(duration)
    ));

    public static final PotionEffectDefinition WEAKNESS_LONG = of(4800, duration -> List.of(
            new EffectWeakness().setDuration(duration)
    ));

    public static final PotionEffectDefinition DECAY = of(800, duration -> List.of(
            new EffectWither().setDuration(duration).setAmplifier(1)
    ));

    public static final PotionEffectDefinition TURTLE_MASTER = of(400, duration -> List.of(
            new EffectSlowness().setDuration(duration).setAmplifier(3),
            new EffectResistance().setDuration(duration).setAmplifier(2)
    ));

    public static final PotionEffectDefinition TURTLE_MASTER_LONG = of(800, duration -> List.of(
            new EffectSlowness().setDuration(duration).setAmplifier(3),
            new EffectResistance().setDuration(duration).setAmplifier(2)
    ));

    public static final PotionEffectDefinition TURTLE_MASTER_STRONG = of(400, duration -> List.of(
            new EffectSlowness().setDuration(duration).setAmplifier(5),
            new EffectResistance().setDuration(duration).setAmplifier(3)
    ));

    public static final PotionEffectDefinition SLOW_FALLING = of(1800, duration -> List.of(
            new EffectSlowFalling().setDuration(duration)
    ));

    public static final PotionEffectDefinition SLOW_FALLING_LONG = of(4800, duration -> List.of(
            new EffectSlowFalling().setDuration(duration)
    ));

    // TODO: Implement effects first, then add these back in.
    /*public static final PotionEffectDefinition WIND_CHARGING = of(3600, duration -> List.of(
            new EffectWindCharged().setDuration(duration)
    ));

    public static final PotionEffectDefinition WEAVING = of(3600, duration -> List.of(
            new EffectWeaving().setDuration(duration)
    ));

    public static final PotionEffectDefinition OOZING = of(3600, duration -> List.of(
            new EffectOozing().setDuration(duration)
    ));

    public static final PotionEffectDefinition INFESTATION = of(3600, duration -> List.of(
            new EffectInfested().setDuration(duration)
    ));*/

    public List<Effect> getEffects(PotionApplicationMode mode) {
        return supplier.apply(mode);
    }
}