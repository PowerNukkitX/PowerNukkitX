package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
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
