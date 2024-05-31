package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemComparator extends Item {
    /**
     * @deprecated 
     */
    
    public ItemComparator() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemComparator(Integer meta, int count) {
        super(COMPARATOR, meta, count, "Redstone Comparator");
        this.block = Block.get(BlockID.UNPOWERED_COMPARATOR);
    }
}