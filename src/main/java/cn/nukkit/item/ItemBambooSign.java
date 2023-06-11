package cn.nukkit.item;

import cn.nukkit.block.BlockCherrySignPost;

/**
 * Author: Cool_Loong <br>
 * Date: 6/11/2023 <br>
 * Allay Project
 */
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
