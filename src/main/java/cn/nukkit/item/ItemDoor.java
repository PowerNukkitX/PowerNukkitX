package cn.nukkit.item;

import cn.nukkit.block.Block;

public abstract class ItemDoor extends Item {
    public ItemDoor(String id) {
        super(id);
        this.block = Block.get(id);
    }
}
