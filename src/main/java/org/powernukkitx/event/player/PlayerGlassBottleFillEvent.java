package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.item.Item;

public class PlayerGlassBottleFillEvent extends PlayerEvent implements Cancellable {
    protected final Item item;
    protected final Block target;

    public PlayerGlassBottleFillEvent(Player player, Block target, Item item) {
        this.player = player;
        this.target = target;
        this.item = item.clone();
    }

    public Item getItem() {
        return item;
    }

    public Block getBlock() {
        return target;
    }
}
