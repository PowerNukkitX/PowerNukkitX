package org.powernukkitx.event.player;

import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.Event;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.network.process.PlayerSessionHolder;

public class PlayerLoginSessionFailEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final PlayerSessionHolder playerSessionHolder;
    protected DisconnectFailReason disconnectFailReason;

    public PlayerLoginSessionFailEvent(PlayerSessionHolder playerSessionHolder, DisconnectFailReason disconnectFailReason) {
        this.playerSessionHolder = playerSessionHolder;
        this.disconnectFailReason = disconnectFailReason;
    }

    public DisconnectFailReason getDisconnectFailReason() {
        return disconnectFailReason;
    }

    public PlayerSessionHolder getPlayerSessionHolder() {
        return playerSessionHolder;
    }

    public void setDisconnectFailReason(DisconnectFailReason disconnectFailReason) {
        this.disconnectFailReason = disconnectFailReason;
    }
}
