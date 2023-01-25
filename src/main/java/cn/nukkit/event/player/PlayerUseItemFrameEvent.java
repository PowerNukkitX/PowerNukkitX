package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerUseItemFrameEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockEntityItemFrame itemFrame;
    private final Block block;
    private final Item item;
    private final Action action;

    public PlayerUseItemFrameEvent(@Nullable Player player, @Nonnull Block block, @Nonnull BlockEntityItemFrame itemFrame, @Nullable Item item, Action action) {
        this.player = player;
        this.itemFrame = itemFrame;
        this.block = block;
        this.action = action;
        this.item = item;
    }

    @Nonnull
    public BlockEntityItemFrame getItemFrame() {
        return itemFrame;
    }

    @Nullable
    public Item getItem() {
        return item;
    }

    @Nonnull
    public Block getBlock() {
        return block;
    }

    @Nonnull
    public Action getAction() {
        return action;
    }

    public enum Action {
        DROP,
        PUT,
        ROTATION
    }
}
