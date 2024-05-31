package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;

public class PlayerChunkRequestEvent extends PlayerEvent {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int chunkX;
    private final int chunkZ;
    /**
     * @deprecated 
     */
    

    public PlayerChunkRequestEvent(Player player, int chunkX, int chunkZ) {
        this.player = player;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }
    /**
     * @deprecated 
     */
    

    public int getChunkX() {
        return chunkX;
    }
    /**
     * @deprecated 
     */
    

    public int getChunkZ() {
        return chunkZ;
    }
}
