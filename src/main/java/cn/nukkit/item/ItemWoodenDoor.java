package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemWoodenDoor extends ItemDoor {
    public ItemWoodenDoor() {
        super(WOODEN_DOOR);
        this.block = Block.get(BlockID.WOODEN_DOOR);
    }
}
