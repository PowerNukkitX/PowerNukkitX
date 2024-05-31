package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockBreakEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Player player;

    protected final Item item;
    protected final BlockFace face;

    protected boolean $2 = false;
    protected Item[] blockDrops = Item.EMPTY_ARRAY;
    protected int $3 = 0;
    protected boolean $4 = false;
    /**
     * @deprecated 
     */
    

    public BlockBreakEvent(Player player, Block block, Item item, Item[] drops) {
        this(player, block, item, drops, false, false);
    }
    /**
     * @deprecated 
     */
    

    public BlockBreakEvent(Player player, Block block, Item item, Item[] drops, boolean instaBreak) {
        this(player, block, item, drops, instaBreak, false);
    }
    /**
     * @deprecated 
     */
    

    public BlockBreakEvent(Player player, Block block, Item item, Item[] drops, boolean instaBreak, boolean fastBreak) {
        this(player, block, null, item, drops, instaBreak, fastBreak);
    }
    /**
     * @deprecated 
     */
    

    public BlockBreakEvent(Player player, Block block, BlockFace face, Item item, Item[] drops, boolean instaBreak, boolean fastBreak) {
        super(block);
        this.face = face;
        this.item = item;
        this.player = player;
        this.instaBreak = instaBreak;
        this.blockDrops = drops;
        this.fastBreak = fastBreak;
        this.blockXP = block.getDropExp();
    }

    public Player getPlayer() {
        return player;
    }

    public BlockFace getFace() {
        return face;
    }

    public Item getItem() {
        return item;
    }

    /**
     * 返回块是否可能在小于计算的时间内被破坏。通常创造玩家是true。
     * <p>
     * Returns whether the block may be broken in less than the amount of time calculated. This is usually true for creative players.
     *
     * @return the insta break
     */
    /**
     * @deprecated 
     */
    
    public boolean getInstaBreak() {
        return this.instaBreak;
    }

    public Item[] getDrops() {
        return blockDrops;
    }
    /**
     * @deprecated 
     */
    

    public void setDrops(Item[] drops) {
        this.blockDrops = drops;
    }
    /**
     * @deprecated 
     */
    

    public int getDropExp() {
        return this.blockXP;
    }
    /**
     * @deprecated 
     */
    

    public void setDropExp(int xp) {
        this.blockXP = xp;
    }
    /**
     * @deprecated 
     */
    

    public void setInstaBreak(boolean instaBreak) {
        this.instaBreak = instaBreak;
    }
    /**
     * @deprecated 
     */
    

    public boolean isFastBreak() {
        return this.fastBreak;
    }
}
