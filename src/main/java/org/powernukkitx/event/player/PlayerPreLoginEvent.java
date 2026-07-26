package org.powernukkitx.event.player;

import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.Event;
import org.powernukkitx.event.HandlerList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.util.ChainValidationResult;

/**
 * Called directly when the client sends a LoginPacket to the server. The player object has not yet been created.
 * The client jwt data will be validated after this event is called.
 * Can be cancelled to disconnect the client with the specified kick message.
 *
 * @author Kaooot
 */
@Getter
@RequiredArgsConstructor
public class PlayerPreLoginEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ChainValidationResult.IdentityClaims identityClaims;

    @Setter
    private String kickMessage = "";

    public static HandlerList getHandlers() {
        return HANDLERS;
    }
}