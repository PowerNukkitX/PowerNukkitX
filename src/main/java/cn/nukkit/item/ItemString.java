package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemString extends Item {
    /**
     * @deprecated 
     */
    

    public ItemString() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemString(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemString(Integer meta, int count) {
        super(STRING, meta, count, "String");
        this.block = Block.get(BlockID.TRIP_WIRE);
    }
}
