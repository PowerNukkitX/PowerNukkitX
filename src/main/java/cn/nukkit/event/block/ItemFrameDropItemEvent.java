package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.Cancellable;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * ItemFrameUseEvent的子事件，仅代表物品展示框掉落物品的事件
 * <p>
 * The event that the item display box is used will be called when an item is dropped
 *
 * @author Pub4Game
 * @since 03.07.2016
 */
@Deprecated
@DeprecationDetails(since = "1.19.60-r1", reason = "use ItemFrameUseEvent")
public class ItemFrameDropItemEvent extends ItemFrameUseEvent implements Cancellable {

    public ItemFrameDropItemEvent(@Nullable Player player, @NotNull Block block, @NotNull BlockEntityItemFrame itemFrame, @NotNull Item item) {
        super(player, block, itemFrame, item, Action.DROP);
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
