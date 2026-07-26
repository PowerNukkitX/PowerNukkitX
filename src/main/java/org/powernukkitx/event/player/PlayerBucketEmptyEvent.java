package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;

public class PlayerBucketEmptyEvent extends PlayerBucketEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerBucketEmptyEvent(Player who, Block blockClicked, BlockFace blockFace, Block liquid, Item bucket, Item itemInHand) {
        super(who, blockClicked, blockFace, liquid, bucket, itemInHand);
    }

}
