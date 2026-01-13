package cn.nukkit.network.protocol.types.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Options used when transferring a player to a server
 * using a direct hostname and port connection.
 *
 * @author xRookieFight
 * @since 12/01/2026
 */
@Getter
@AllArgsConstructor
public class TransferPlayerIpPortOptions implements TransferPlayerOptions {

    private final String hostname;
    private final int port;
}