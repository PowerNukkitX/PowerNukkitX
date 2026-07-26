package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;

public class PlayerBucketFillEvent extends PlayerBucketEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerBucketFillEvent(Player who, Block blockClicked, BlockFace blockFace, Block liquid, Item bucket, Item itemInHand) {
        super(who, blockClicked, blockFace, liquid, bucket, itemInHand);
    }

}
