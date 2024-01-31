package cn.nukkit.network.connection;

import lombok.experimental.UtilityClass;
import org.cloudburstmc.netty.channel.raknet.RakDisconnectReason;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public class BedrockDisconnectReasons {
    public static final String DISCONNECTED = "disconnect.disconnected";
    public static final String CLOSED = "disconnect.closed";
    public static final String REMOVED = "disconnect.removed";
    public static final String TIMEOUT = "disconnect.timeout";
    public static final String UNKNOWN = "disconnect.lost";

    private static final Map<RakDisconnectReason, String> FROM_RAKNET = generateRakNetMappings();

    private static Map<RakDisconnectReason, String> generateRakNetMappings() {
        EnumMap<RakDisconnectReason, String> map = new EnumMap<>(RakDisconnectReason.class);
        map.put(RakDisconnectReason.CLOSED_BY_REMOTE_PEER, CLOSED);
        map.put(RakDisconnectReason.DISCONNECTED, DISCONNECTED);
        map.put(RakDisconnectReason.TIMED_OUT, TIMEOUT);
        map.put(RakDisconnectReason.BAD_PACKET, REMOVED);

        return Collections.unmodifiableMap(map);
    }

    public static String getReason(RakDisconnectReason reason) {
        return FROM_RAKNET.getOrDefault(reason, reason.name());
    }
}
