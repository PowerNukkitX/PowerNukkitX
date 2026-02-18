package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;
import org.cloudburstmc.protocol.bedrock.packet.SetSpawnPositionPacket.Type;
import it.unimi.dsi.fastutil.Pair;

public class PlayerRespawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Pair<Position, Type> position;//Respawn Position

    public PlayerRespawnEvent(Player player, Pair<Position, Type> position) {
        this.player = player;
        this.position = position;
    }

    public Pair<Position, Type> getRespawnPosition() {
        return position;
    }

    public void setRespawnPosition(Pair<Position, Type> position) {
        this.position = position;
    }
}
