package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * 注意做好sign和standing_sign方块的映射关系，物品通过this.block指定，方块通过toItem指定
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
