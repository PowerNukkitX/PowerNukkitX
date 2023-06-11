package cn.nukkit.item;

import cn.nukkit.block.BlockHangingSign;

/**
 * Author: Cool_Loong <br>
 * Date: 6/11/2023 <br>
 * Allay Project
 */
public class ItemBlockHangingSign extends ItemBlock {
    public ItemBlockHangingSign(BlockHangingSign block) {
        super(block);
    }

    public ItemBlockHangingSign(BlockHangingSign block, Integer meta) {
        super(block, meta, 1);
    }

    public ItemBlockHangingSign(BlockHangingSign block, Integer meta, int count) {
        super(block, meta, count);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
