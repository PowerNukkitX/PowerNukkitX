package cn.nukkit.network.protocol.types;

import java.util.EnumSet;

public enum EmoteFlag {

    SERVER(1 << 0),
    MUTE_ANNOUNCEMENT(1 << 1);

    private final int bit;

    EmoteFlag(int bit) {
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }

    public static EnumSet<EmoteFlag> fromByte(byte value) {
        EnumSet<EmoteFlag> set = EnumSet.noneOf(EmoteFlag.class);
        for (EmoteFlag flag : values()) {
            if ((value & flag.bit) != 0) {
                set.add(flag);
            }
        }
        return set;
    }

    public static byte toByte(EnumSet<EmoteFlag> flags) {
        int result = 0;
        for (EmoteFlag flag : flags) {
            result |= flag.bit;
        }
        return (byte) result;
    }
}