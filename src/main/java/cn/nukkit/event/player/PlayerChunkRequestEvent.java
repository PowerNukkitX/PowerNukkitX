package cn.nukkit.event.player;

import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.player.Player;

@PowerNukkitXDifference(info = "PlayerChunkRequestEvent can't be cancelled")
public class PlayerChunkRequestEvent extends PlayerEvent {

    private final int chunkX;
    private final int chunkZ;

    public PlayerChunkRequestEvent(Player player, int chunkX, int chunkZ) {
        this.player = player;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }
}
