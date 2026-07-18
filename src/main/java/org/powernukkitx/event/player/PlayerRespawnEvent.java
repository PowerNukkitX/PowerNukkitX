package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.Position;
import it.unimi.dsi.fastutil.Pair;

public class PlayerRespawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Pair<Position, Player.SpawnPointType> position;//Respawn Position

    public PlayerRespawnEvent(Player player, Pair<Position, Player.SpawnPointType> position) {
        this.player = player;
        this.position = position;
    }

    public Pair<Position, Player.SpawnPointType> getRespawnPosition() {
        return position;
    }

    public void setRespawnPosition(Pair<Position, Player.SpawnPointType> position) {
        this.position = position;
    }
}
