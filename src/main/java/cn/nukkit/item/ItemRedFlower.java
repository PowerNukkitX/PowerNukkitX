package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemRedFlower extends Item implements AliasItem {

    public ItemRedFlower() {
        super(RED_FLOWER);
        this.block = Block.get(BlockID.POPPY);
    }

    @Override
    public String getAliasIdentifier() {
        return BlockID.POPPY;
    }
}