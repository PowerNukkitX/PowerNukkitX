package org.powernukkitx.utils.collection;


public interface AutoFreezable {
    FreezeStatus getFreezeStatus();

    int getTemperature();

    /**
     * this.temperature += temperature; <br/>
     * Includes a boiling point check, but no absolute zero check!
     * @param temperature the amount of temperature change
     */
    void warmer(int temperature);

    /**
     * this.temperature -= temperature; <br/>
     * Includes an absolute zero check, but no boiling point check!
     * @param temperature the amount of temperature change
     */
    void colder(int temperature);

    /**
     * Forcibly freezes the array.
     */
    void freeze();

    /**
     * Forcibly deep-freezes the array.
     */
    void deepFreeze();

    @ShouldThaw
    void thaw();

    enum FreezeStatus {
        NONE, FREEZING, FREEZE, DEEP_FREEZING, DEEP_FREEZE, THAWING
    }
}
