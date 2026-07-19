package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;

/**
 * Make sure to keep the mapping between the sign and standing_sign blocks correct: the item is specified via this.block and the block via toItem.
 */
public abstract class ItemSign extends Item {
    protected ItemSign(String id) {
        super(id);
        if (id.equals(DARK_OAK_SIGN)) {
            this.block = Block.get(BlockID.DARKOAK_STANDING_SIGN);
        } else if (id.equals(OAK_SIGN)) {
            this.block = Block.get(BlockID.STANDING_SIGN);
        } else {
            this.block = Block.get(id.replace("_sign", "_standing_sign"));
        }
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
