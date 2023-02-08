package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author Pub4Game
 * @since 03.07.2016
 */
@Deprecated
@DeprecationDetails(since = "1.19.60-r1", reason = "use PlayerUseItemFrameEvent")
public class ItemFrameDropItemEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Item item;
    private final BlockEntityItemFrame itemFrame;

    public ItemFrameDropItemEvent(@Nullable Player player, @NotNull Block block, @NotNull BlockEntityItemFrame itemFrame, @NotNull Item item) {
        super(block);
        this.player = player;
        this.itemFrame = itemFrame;
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public BlockEntityItemFrame getItemFrame() {
        return itemFrame;
    }

    @NotNull
    public Item getItem() {
        return item;
    }
}
