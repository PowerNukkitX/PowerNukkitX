package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.type.HangingSignItem;

public class ItemMangroveHangingSign extends Item implements HangingSignItem {
    public ItemMangroveHangingSign() {
        super(MANGROVE_HANGING_SIGN);
        this.block = Block.get(BlockID.MANGROVE_HANGING_SIGN);
    }
}