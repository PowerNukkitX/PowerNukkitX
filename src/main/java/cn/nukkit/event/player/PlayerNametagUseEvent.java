package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.ItemNameTag;
import cn.nukkit.level.Location;

public class PlayerNametagUseEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private ItemNameTag nameTag;

    public PlayerNametagUseEvent(Player player,ItemNameTag nameTag) {
        this.player = player;
        this.nameTag = nameTag;
    }

    @Override
    public void setCancelled() {
        super.setCancelled();
    }
}
