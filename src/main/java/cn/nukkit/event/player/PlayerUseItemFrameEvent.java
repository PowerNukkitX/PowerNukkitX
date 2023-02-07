package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

import org.jetbrains.annotations.NotNull;

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

    public PlayerUseItemFrameEvent(@NotNull Player player, @NotNull Block block, @NotNull BlockEntityItemFrame itemFrame, @Nullable Item item, Action action) {
        this.player = player;
        this.itemFrame = itemFrame;
        this.block = block;
        this.action = action;
        this.item = item;
    }

    @NotNull
    public BlockEntityItemFrame getItemFrame() {
        return itemFrame;
    }

    @Nullable
    public Item getItem() {
        return item;
    }

    @NotNull
    public Block getBlock() {
        return block;
    }

    @NotNull
    public Action getAction() {
        return action;
    }

    public enum Action {
        DROP,
        PUT,
        ROTATION
    }
}
