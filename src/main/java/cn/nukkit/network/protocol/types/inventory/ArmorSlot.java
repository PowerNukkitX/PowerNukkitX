package cn.nukkit.network.protocol.types.inventory;

/**
 * @author Kaooot
 */

public enum ArmorSlot {
    HEAD,
    TORSO,
    LEGS,
    FEET,
    BODY;

    private static final ArmorSlot[] VALUES = values();

    public int getId() {
        return this.ordinal() * 2;
    }

    public static ArmorSlot from(int id) {
        int ordinal = id / 2;

        if (ordinal >= 0 && ordinal < VALUES.length) {
            return VALUES[ordinal];
        }

        throw new UnsupportedOperationException("Detected unknown ArmorSlot ID: " + id + ", ordinal: " + ordinal);
    }
}