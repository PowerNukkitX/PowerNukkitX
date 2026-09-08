package org.powernukkitx.level.redstone.circuit.components;

public abstract class BaseCircuitComponent {
    private int strength;
    private int oldStrength;
    private int direction;
    private boolean consumePowerAnyDirection;

    public int getStrength() {
        return this.strength;
    }

    public int getOldStrength() {
        return this.oldStrength;
    }

    public void setStrength(int strength) {
        this.oldStrength = this.strength;
        this.strength = Math.max(0, Math.min(15, strength)
        );
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean canConsumePowerAnyDirection() {
        return this.consumePowerAnyDirection;
    }

    public void setConsumePowerAnyDirection(boolean consumePowerAnyDirection) {
        this.consumePowerAnyDirection = consumePowerAnyDirection;
    }

    public abstract CircuitComponentType getCircuitComponentType();
}
