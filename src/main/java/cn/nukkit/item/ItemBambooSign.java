package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.impl.BlockCherrySignPost;

@PowerNukkitXOnly
@Since("1.20.0-r2")
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
