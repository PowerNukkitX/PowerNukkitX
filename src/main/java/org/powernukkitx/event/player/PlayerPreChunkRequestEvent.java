package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.utils.EventException;
import lombok.Getter;

@Getter
public class PlayerPreChunkRequestEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int chunkX;
    private final int chunkZ;

    private final boolean forced;

    public PlayerPreChunkRequestEvent(Player player, int chunkX, int chunkZ, boolean forced) {
        this.player = player;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.forced = forced;
    }

    @Override
    public void setCancelled(boolean value) {
        if (forced && value) {
            throw new EventException("Event is not Cancellable when forced!");
        }
        super.setCancelled(value);
    }
}
