package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

public class PlayerDropItemEvent extends PlayerEvent implements Cancellable {

    private final Item drop;

    public PlayerDropItemEvent(Player player, Item drop) {
        this.player = player;
        this.drop = drop;
    }

    public Item getItem() {
        return this.drop;
    }
}
