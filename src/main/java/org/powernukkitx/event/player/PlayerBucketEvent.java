package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;

abstract class PlayerBucketEvent extends PlayerEvent implements Cancellable {

    private final Block blockClicked;

    private final BlockFace blockFace;

    private final Block liquid;

    private final Item bucket;

    private Item item;


    public PlayerBucketEvent(Player who, Block blockClicked, BlockFace blockFace, Block liquid, Item bucket, Item itemInHand) {
        this.player = who;
        this.blockClicked = blockClicked;
        this.blockFace = blockFace;
        this.liquid = liquid;
        this.item = itemInHand;
        this.bucket = bucket;
    }

    /**
     * Returns the bucket used in this event
     * @return bucket
     */
    public Item getBucket() {
        return this.bucket;
    }

    /**
     * Returns the item in hand after the event
     * @return item
     */
    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Block getBlockClicked() {
        return this.blockClicked;
    }

    public Block getLiquid() {
        return liquid;
    }

    public BlockFace getBlockFace() {
        return this.blockFace;
    }
}
