package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * @author CreeperFace
 * @since 18.3.2017
 */
public class PlayerMapInfoRequestEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item item;
    /**
     * @deprecated 
     */
    

    public PlayerMapInfoRequestEvent(Player p, Item item) {
        this.player = p;
        this.item = item;
    }

    public Item getMap() {
        return item;
    }

}
