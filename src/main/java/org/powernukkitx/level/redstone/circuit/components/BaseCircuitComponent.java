package org.powernukkitx.level.redstone.circuit.components;

/**
 * Stores the state shared by all components in a redstone circuit.
 *
 * <p>The state includes the current and previous signal strengths, the
 * component direction, and whether power can be consumed from any direction.
 * Concrete components provide their role through {@link #getCircuitComponentType()}.</p>
 *
 * @author Curse
 */
public abstract class BaseCircuitComponent {
    private int strength;
    private int oldStrength;
    private int direction;
    private boolean consumePowerAnyDirection;

    /**
     * Returns the current redstone signal strength.
     *
     * @return the current strength in the range {@code 0..15}
     */
    public int getStrength() {
        return this.strength;
    }

    /**
     * Returns the strength recorded before the latest strength update.
     *
     * @return the previous signal strength
     */
    public int getOldStrength() {
        return this.oldStrength;
    }

    /**
     * Updates the signal strength while retaining its previous value.
     *
     * @param strength the new strength, clamped to {@code 0..15}
     */
    public void setStrength(int strength) {
        this.oldStrength = this.strength;
        this.strength = Math.max(0, Math.min(15, strength)
        );
    }

    /**
     * Returns the component direction value.
     *
     * @return the component direction
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * Sets the component direction value.
     *
     * @param direction the component direction
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Returns whether this component can consume power from any direction.
     *
     * @return {@code true} when power can be consumed from any direction
     */
    public boolean canConsumePowerAnyDirection() {
        return this.consumePowerAnyDirection;
    }

    /**
     * Sets whether this component can consume power from any direction.
     *
     * @param consumePowerAnyDirection whether all directions can provide power
     */
    public void setConsumePowerAnyDirection(boolean consumePowerAnyDirection) {
        this.consumePowerAnyDirection = consumePowerAnyDirection;
    }

    /**
     * Returns the role of this component in a circuit.
     *
     * @return the circuit component type
     */
    public abstract CircuitComponentType getCircuitComponentType();
}
