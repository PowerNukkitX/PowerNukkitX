package cn.nukkit.network.protocol.types.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Options used when transferring
 * a player to a server via WaterdogPE.
 *
 * @author xRookieFight
 * @since 13/01/2026
 */
@Getter
@AllArgsConstructor
public class TransferPlayerWaterdogOptions implements TransferPlayerOptions {

    private final String serverName;
}