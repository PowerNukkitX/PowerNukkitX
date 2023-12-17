package cn.nukkit.item;

import cn.nukkit.block.BlockCherrySignPost;


public class ItemCherrySign extends StringItemBase {
    public ItemCherrySign() {
        super("minecraft:cherry_sign", "Cherry Sign");
        this.block = new BlockCherrySignPost();
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
