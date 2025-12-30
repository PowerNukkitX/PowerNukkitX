package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


/**
 * 物品展示框被使用的事件，会在放置物品，旋转物品，掉落物品时调用
 * <p>
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
     * 获取使用物品展示框的玩家
     * Gets player.
     *
     * @return the player
     */
    public @Nullable Player getPlayer() {
        return player;
    }

    /**
     * 获取被使用的物品展示框
     * <p>
     * Gets item frame.
     *
     * @return the item frame
     */
    @NotNull public BlockEntityItemFrame getItemFrame() {
        return itemFrame;
    }

    /**
     * 获取操作中的物品，例如放置物品到物品展示框，获取该物品
     * <p>
     * Get the item in action, e.g. place the item in the item display box and get the item
     *
     * @return the item
     */
    public @Nullable Item getItem() {
        return item;
    }

    /**
     * 获取操作类型，掉落，放置，选择
     * <p>
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
