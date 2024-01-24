package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSugarCane extends Item {

    public ItemSugarCane() {
        this(0, 1);
    }

    public ItemSugarCane(Integer meta) {
        this(meta, 1);
    }

    public ItemSugarCane(Integer meta, int count) {
        super(SUGAR_CANE, 0, count);
        this.block = Block.get(BlockID.REEDS);
    }
}
