package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemRedFlower extends Item {

    public ItemRedFlower() {
        super(RED_FLOWER);
        this.block = Block.get(BlockID.POPPY);
    }
}