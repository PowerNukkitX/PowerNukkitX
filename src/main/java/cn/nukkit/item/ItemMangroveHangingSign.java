package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemMangroveHangingSign extends ItemHangingSign {
    public ItemMangroveHangingSign() {
        super(MANGROVE_HANGING_SIGN);
        this.block = Block.get(BlockID.MANGROVE_HANGING_SIGN);
    }
}