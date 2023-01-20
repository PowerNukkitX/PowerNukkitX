package cn.nukkit.network.protocol.types;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public enum TeleportationCause {
    UNKNOWN,
    PROJECTILE,
    CHORUS_FRUIT,
    COMMAND,
    BEHAVIOR;

    private static final InternalLogger log = InternalLoggerFactory.getInstance(TeleportationCause.class);

    private static final TeleportationCause[] VALUES = values();

    public static TeleportationCause byId(int id) {
        if (id >= 0 && id < VALUES.length) {
            return VALUES[id];
        }
        log.debug("Unknown teleportation cause ID: {}", id);
        return UNKNOWN;
    }
}
