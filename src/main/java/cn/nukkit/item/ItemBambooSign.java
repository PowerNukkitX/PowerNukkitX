package cn.nukkit.item;

import cn.nukkit.block.BlockCherrySignPost;


public class ItemBambooSign extends StringItemBase {
    public ItemBambooSign() {
        super("minecraft:bamboo_sign", "Bamboo Sign");
        this.block = new BlockCherrySignPost();
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
