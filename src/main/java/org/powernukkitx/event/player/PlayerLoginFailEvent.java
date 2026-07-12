package org.powernukkitx.event.player;

import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.powernukkitx.event.Event;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.network.process.PlayerSessionHolder;

public class PlayerLoginFailEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final PlayerSessionHolder playerSessionHolder;
    protected DisconnectFailReason disconnectFailReason;

    public PlayerLoginFailEvent(PlayerSessionHolder playerSessionHolder, DisconnectFailReason disconnectFailReason) {
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
