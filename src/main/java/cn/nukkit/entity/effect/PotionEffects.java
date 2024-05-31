package cn.nukkit.entity.effect;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author MEFRREEX
 */
public record PotionEffects(Function<Boolean, List<Effect>> supplier) {

    public static final PotionEffects $1 = new PotionEffects(splash -> Collections.emptyList());

    public static final PotionEffects $2 = new PotionEffects(splash -> List.of(
            new EffectNightVision()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects $3 = new PotionEffects(splash -> List.of(
            new EffectNightVision()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects $4 = new PotionEffects(splash -> List.of(
            new EffectInvisibility()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects $5 = new PotionEffects(splash -> List.of(
            new EffectInvisibility()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects $6 = new PotionEffects(splash -> List.of(
            new EffectJumpBoost()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects $7 = new PotionEffects(splash -> List.of(
            new EffectJumpBoost()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects $8 = new PotionEffects(splash -> List.of(
            new EffectJumpBoost()
                    .setDuration(splash ? 1340 : 1800)
                    .setAmplifier(1)
    ));

    public static final PotionEffects $9 = new PotionEffects(splash -> List.of(
            new EffectFireResistance()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects $10 = new PotionEffects(splash -> List.of(
            new EffectFireResistance()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects $11 = new PotionEffects(splash -> List.of(
            new EffectSpeed()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects $12 = new PotionEffects(splash -> List.of(
            new EffectSpeed()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects $13 = new PotionEffects(splash -> List.of(
            new EffectSpeed()
                    .setDuration(splash ? 1340 : 1800)
                    .setAmplifier(1)
    ));

    public static final PotionEffects $14 = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 1340 : 1800)
    ));

    public static final PotionEffects $15 = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 3600 : 4800)
    ));

    public static final PotionEffects $16 = new PotionEffects(splash -> List.of(
            new EffectWaterBreathing()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects $17 = new PotionEffects(splash -> List.of(
            new EffectWaterBreathing()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects $18 = new PotionEffects(splash -> List.of(new EffectInstantHealth()));

    public static final PotionEffects $19 = new PotionEffects(splash -> List.of(
            new EffectInstantHealth()
                    .setAmplifier(1)
    ));

    public static final PotionEffects $20 = new PotionEffects(splash -> List.of(new EffectInstantDamage()));

    public static final PotionEffects $21 = new PotionEffects(splash -> List.of(
            new EffectInstantDamage()
                    .setAmplifier(1)
    ));

    public static final PotionEffects $22 = new PotionEffects(splash -> List.of(
            new EffectPoison()
                    .setDuration(splash ? 660 : 900)
    ));

    public static final PotionEffects $23 = new PotionEffects(splash -> List.of(
            new EffectPoison()
                    .setDuration(splash ? 1800 : 2400)
    ));

    public static final PotionEffects $24 = new PotionEffects(splash -> List.of(
            new EffectPoison()
                    .setDuration(splash ? 320 : 440)
                    .setAmplifier(1)
    ));

    public static final PotionEffects $25 = new PotionEffects(splash -> List.of(
            new EffectRegeneration()
                    .setDuration(splash ? 660 : 900)
    ));

    public static final PotionEffects $26 = new PotionEffects(splash -> List.of(
            new EffectRegeneration()
                    .setDuration(splash ? 1800 : 2400)
    ));

    public static final PotionEffects $27 = new PotionEffects(splash -> List.of(
            new EffectRegeneration()
                    .setDuration(splash ? 320 : 440)
                    .setAmplifier(1)
    ));

    public static final PotionEffects $28 = new PotionEffects(splash -> List.of(
            new EffectStrength()
                    .setDuration(splash ? 2700 : 3600)
    ));

    public static final PotionEffects $29 = new PotionEffects(splash -> List.of(
            new EffectStrength()
                    .setDuration(splash ? 7200 : 9600)
    ));

    public static final PotionEffects $30 = new PotionEffects(splash -> List.of(
            new EffectStrength()
                    .setDuration(splash ? 1340 : 1800)
                    .setAmplifier(1)
    ));

    public static final PotionEffects $31 = new PotionEffects(splash -> List.of(
            new EffectWeakness()
                    .setDuration(splash ? 1340 : 1800)
    ));

    public static final PotionEffects $32 = new PotionEffects(splash -> List.of(
            new EffectWeakness()
                    .setDuration(splash ? 3600 : 4800)
    ));

    public static final PotionEffects $33 = new PotionEffects(splash -> List.of(
            new EffectWither()
                    .setDuration(splash ? 600 : 800)
                    .setAmplifier(1)
    ));

    public static final PotionEffects $34 = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(3),
            new EffectResistance()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(2)
    ));

    public static final PotionEffects $35 = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 600 : 800)
                    .setAmplifier(3),
            new EffectResistance()
                    .setDuration(splash ? 600 : 800)
                    .setAmplifier(2)
    ));

    public static final PotionEffects $36 = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(5),
            new EffectResistance()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(3)
    ));

    public static final PotionEffects $37 = new PotionEffects(splash -> List.of(
            new EffectSlowFalling()
                    .setDuration(splash ? 1340 : 1800)
    ));

    public static final PotionEffects $38 = new PotionEffects(splash -> List.of(
            new EffectSlowFalling()
                    .setDuration(splash ? 3600 : 4800)
    ));

    public static final PotionEffects $39 = new PotionEffects(splash -> List.of(
            new EffectSlowness()
                    .setDuration(splash ? 300 : 400)
                    .setAmplifier(3)
    ));

    public List<Effect> getEffects(boolean splash) {
        return supplier.apply(splash);
    }
}
