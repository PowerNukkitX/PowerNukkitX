package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;

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