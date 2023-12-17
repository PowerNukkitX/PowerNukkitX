package cn.nukkit.item;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockCrimsonSignPost;


public class ItemCrimsonSign extends ItemSign {


    public ItemCrimsonSign() {
        this(0, 1);
    }


    public ItemCrimsonSign(Integer meta) {
        this(meta, 1);
    }


    public ItemCrimsonSign(Integer meta, int count) {
        super(CRIMSON_SIGN, meta, count, "Crimson Sign", new BlockCrimsonSignPost());
    }
}
