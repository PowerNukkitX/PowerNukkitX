package org.powernukkitx.utils;

public final class ArmorUtils {
    private static final int MAX_ARMOR_POINTS = 30;
    private static final int MAX_TOUGHNESS_POINTS = 20;
    private static final double MAX_EFFECTIVE_ARMOR_POINTS = 20.0;
    private static final double ARMOR_REDUCTION_DIVISOR = 25.0;

    private ArmorUtils() {
    }

    public static float calculateDamageReduction(float damage, int armorPoints, int toughnessPoints) {
        int cappedArmorPoints = Math.max(0, Math.min(armorPoints, MAX_ARMOR_POINTS));
        int cappedToughnessPoints = Math.max(0, Math.min(toughnessPoints, MAX_TOUGHNESS_POINTS));

        double effectiveArmorPoints = Math.min(
                MAX_EFFECTIVE_ARMOR_POINTS,
                Math.max(
                        cappedArmorPoints / 5.0,
                        cappedArmorPoints - damage / (2.0 + cappedToughnessPoints / 4.0)
                )
        );

        return (float) (damage * effectiveArmorPoints / ARMOR_REDUCTION_DIVISOR);
    }
}
