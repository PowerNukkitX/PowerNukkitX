package cn.nukkit.energy;

import org.jetbrains.annotations.NotNull;

/**
 * 定义一个能源种类
 * Define an energy type.
 *
 * @author superice666
 */
public interface EnergyType {
    /**
     * @return The name of this energy type.
     */
    @NotNull
    String getName();

    /**
     * When converting from this energy type to another energy type, the amount of energy is converted firstly to
     * the "base" energy type, and then converted to the target energy type. <br>
     * If both two EnergyType returns a positive number, then they can convert to each other.
     *
     * @return The ratio used when convent this type of energy to another type of energy.
     */
    default double getBaseRatio() {
        return Double.NaN;
    }

    /**
     * @return Whether this energy type use base ratio.
     */
    default boolean canConvertToBase() {
        return !Double.isNaN(getBaseRatio());
    }

    /**
     * @param type The energy type to convert to.
     * @return If this energy type can convert to the target energy type.
     */
    default boolean canConvertTo(@NotNull EnergyType type) {
        return canConvertToBase() && type.canConvertToBase();
    }

    /**
     * Convert the amount of energy to the target energy type.
     *
     * @param type   The energy type to convert to.
     * @param amount The amount of energy to convert.
     * @return The amount of energy converted to the target energy type.
     */
    default double convertTo(@NotNull EnergyType type, double amount) {
        if (canConvertTo(type)) {
            return amount * this.getBaseRatio() / type.getBaseRatio();
        } else {
            throw new IllegalArgumentException("Cannot convert to " + type.getName() + " from " + this.getName());
        }
    }
}
