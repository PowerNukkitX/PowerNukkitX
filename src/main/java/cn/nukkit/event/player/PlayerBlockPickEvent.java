package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * @author CreeperFace
 */
public class PlayerBlockPickEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block blockClicked;
    private Item item;
    /**
     * @deprecated 
     */
    

    public PlayerBlockPickEvent(Player player, Block blockClicked, Item item) {
        this.blockClicked = blockClicked;
        this.item = item;
        this.player = player;
    }

    public Item getItem() {
        return item;
    }
    /**
     * @deprecated 
     */
    

    public void setItem(Item item) {
        this.item = item;
    }

    public Block getBlockClicked() {
        return blockClicked;
    }
}
