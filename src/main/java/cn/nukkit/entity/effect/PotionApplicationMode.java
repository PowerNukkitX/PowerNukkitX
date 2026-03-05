package cn.nukkit.entity.effect;

/**
 * Describes how a potion effect is being delivered to an entity,
 * which determines the duration each effect lasts.
 * <p>
 * Each mode carries a multiplier relative to the base (drink) duration.
 * Proximity scaling for splash/lingering is applied separately at the call site.
 *
 * @author MEFRREEX
 */
public enum PotionApplicationMode {
    /** Drinking a regular potion: full duration. */
    DRINK(1.0),
    /** Splash potion: same base duration as drink; scaled by proximity at the call site. */
    SPLASH(1.0),
    /** Lingering potion area-effect cloud: 1/4 of drink duration. */
    LINGERING(0.25),
    /** Tipped arrow: 1/8 of drink duration. */
    ARROW(0.125);

    private final double multiplier;

    PotionApplicationMode(double multiplier) {
        this.multiplier = multiplier;
    }

    /** Scales {@code baseDuration} (drink ticks) by this mode's multiplier, truncating to int. */
    public int scaleDuration(int baseDuration) {
        return (int) (baseDuration * multiplier);
    }
}
