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
    protected String disconnectMessage;

    public PlayerLoginFailEvent(PlayerSessionHolder playerSessionHolder, DisconnectFailReason disconnectFailReason) {
        this(playerSessionHolder, disconnectFailReason, null);
    }

    public PlayerLoginFailEvent(PlayerSessionHolder playerSessionHolder, DisconnectFailReason disconnectFailReason, String disconnectMessage) {
        this.playerSessionHolder = playerSessionHolder;
        this.disconnectFailReason = disconnectFailReason;
        this.disconnectMessage = disconnectMessage;
    }

    public DisconnectFailReason getDisconnectFailReason() {
        return disconnectFailReason;
    }

    public void setDisconnectFailReason(DisconnectFailReason disconnectFailReason) {
        this.disconnectFailReason = disconnectFailReason;
    }

    public String getDisconnectMessage() {
        return disconnectMessage;
    }

    public void setDisconnectMessage(String disconnectMessage) {
        this.disconnectMessage = disconnectMessage;
    }

    public PlayerSessionHolder getPlayerSessionHolder() {
        return playerSessionHolder;
    }
}
