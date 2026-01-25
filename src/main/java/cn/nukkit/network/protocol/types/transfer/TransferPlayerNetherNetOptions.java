package cn.nukkit.network.protocol.types.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Options used when transferring
 * a player to a server via NetherNet.
 *
 * @author xRookieFight
 * @since 12/01/2026
 */
@Getter
@AllArgsConstructor
public class TransferPlayerNetherNetOptions implements TransferPlayerOptions {

    private final String netherNetId;
}