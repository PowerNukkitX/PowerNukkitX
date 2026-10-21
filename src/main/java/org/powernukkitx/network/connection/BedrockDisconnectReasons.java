package org.powernukkitx.network.connection;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BedrockDisconnectReasons {
    public static final String DISCONNECTED = "disconnect.disconnected";
    public static final String CLOSED = "disconnect.closed";
    public static final String REMOVED = "disconnect.removed";
    public static final String TIMEOUT = "disconnect.timeout";
    public static final String UNKNOWN = "disconnect.lost";
}
