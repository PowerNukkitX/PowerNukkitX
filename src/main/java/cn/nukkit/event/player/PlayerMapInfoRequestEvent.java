package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

/**
 * @author CreeperFace
 * @since 18.3.2017
 */
public class PlayerMapInfoRequestEvent extends PlayerEvent implements Cancellable {

    private Item item;

    public PlayerMapInfoRequestEvent(Player p, Item item) {
        this.player = p;
        this.item = item;
    }

    public Item getMap() {
        return item;
    }
}
