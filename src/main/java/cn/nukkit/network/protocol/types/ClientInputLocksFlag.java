package cn.nukkit.network.protocol.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum ClientInputLocksFlag {
    // Info from https://gist.github.com/wan-adrian/e919b46be3889d865801eb8883407587

    RESET(0),
    CAMERA(2),
    DISMOUNT(256),
    JUMP(64),
    LATERAL_MOVEMENT(16),
    MOUNT(128),
    MOVE_BACKWARD(1024),
    MOVE_FORWARD(512),
    MOVE_LEFT(2048),
    MOVE_RIGHT(4096),
    MOVEMENT(4),
    SNEAK(32);

    private final int id;

    public static ClientInputLocksFlag fromId(int id) {
        for (ClientInputLocksFlag flag : values()) {
            if (flag.id == id) return flag;
        }
        throw new IllegalArgumentException("Unknown flag id: " + id);
    }

    public static Set<ClientInputLocksFlag> fromBitSet(int bitset) {
        EnumSet<ClientInputLocksFlag> set = EnumSet.noneOf(ClientInputLocksFlag.class);
        for (ClientInputLocksFlag flag : values()) {
            if ((bitset & flag.id) != 0) {
                set.add(flag);
            }
        }
        return set;
    }

    public static int toBitSet(Set<ClientInputLocksFlag> flags) {
        int bitset = 0;
        for (ClientInputLocksFlag flag : flags) {
            bitset |= flag.id;
        }
        return bitset;

    }
}
