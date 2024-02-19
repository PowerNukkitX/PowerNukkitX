package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockPlaceEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Block blockReplace;
    protected final Block blockAgainst;
    protected final Item item;
    protected final Player player;

    public BlockPlaceEvent(Player player, Block blockPlace, Block blockReplace, Block blockAgainst, Item item) {
        super(blockPlace);
        this.blockReplace = blockReplace;
        this.blockAgainst = blockAgainst;
        this.item = item;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    public Block getBlockReplace() {
        return blockReplace;
    }

    public Block getBlockAgainst() {
        return blockAgainst;
    }
}
