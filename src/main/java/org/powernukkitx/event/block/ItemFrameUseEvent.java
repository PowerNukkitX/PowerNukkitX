package org.powernukkitx.event.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.blockentity.BlockEntityItemFrame;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


/**
 * The event that the item display box is used will be called when an item is placed, rotated, or dropped
 */


public class ItemFrameUseEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Player player;
    protected final Item item;
    protected final BlockEntityItemFrame itemFrame;
    protected final Action action;

    public ItemFrameUseEvent(@Nullable Player player, @NotNull Block block, @NotNull BlockEntityItemFrame itemFrame, @Nullable Item item, Action action) {
        super(block);
        this.player = player;
        this.itemFrame = itemFrame;
        this.item = item;
        this.action = action;
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public @Nullable Player getPlayer() {
        return player;
    }

    /**
     * Gets item frame.
     *
     * @return the item frame
     */
    @NotNull public BlockEntityItemFrame getItemFrame() {
        return itemFrame;
    }

    /**
     * Get the item in action, e.g. place the item in the item display box and get the item
     *
     * @return the item
     */
    public @Nullable Item getItem() {
        return item;
    }

    /**
     * Get operation type, drop, place, select
     *
     * @return the action
     */
    @NotNull public Action getAction() {
        return action;
    }

    public enum Action {
        DROP,
        PUT,
        ROTATION,
        REMOVE
    }
}
