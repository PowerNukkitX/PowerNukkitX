package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemWoodenDoor extends Item {
    public ItemWoodenDoor() {
        super(WOODEN_DOOR, 0, 1);
        this.block = Block.get(BlockID.WOODEN_DOOR);
    }
}
