package cn.nukkit.network.security;

import cn.nukkit.Server;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Records malicious-packet violations and short-blocks the offending IP.
 * <p>
 * A violation is any inbound payload the server has decided is hostile
 * (oversized JWT, unknown extraData keys, oversized skin image, etc.).
 * When {@link #flag} is called the IP is added to {@link cn.nukkit.network.Network}'s
 * block list for {@link #BLOCK_DURATION_MS} so the same client cannot
 * immediately retry. Tries to mirror the cheap "kick + 10s ip-block"
 * pattern used by other Bedrock servers to neutralize spam-bots that
 * reconnect on every kick.
 */
@Slf4j
public final class PacketSecurityTracker {
    /** How long the offending IP is blocked, in milliseconds. */
    public static final int BLOCK_DURATION_MS = 10_000;

    private PacketSecurityTracker() {}
    public static void flag(InetSocketAddress address, String reason) {
        if (address == null) {
            log.warn("Malicious packet flagged (no IP address). Reason: {}", reason);
            return;
        }
        InetAddress ip = address.getAddress();
        log.warn("Blocking IP {} for {} ms. Reason: {}", ip.getHostAddress(), BLOCK_DURATION_MS, reason);
        Server server = Server.getInstance();
        if (server != null && server.getNetwork() != null) {
            server.getNetwork().blockAddress(ip, BLOCK_DURATION_MS);
        }
    }
}