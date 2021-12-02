package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockBirchSignPost;

@PowerNukkitOnly
public class ItemBirchSign extends ItemSign {
    @PowerNukkitOnly
    public ItemBirchSign() {
        this(0, 1);
    }

    @PowerNukkitOnly
    public ItemBirchSign(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemBirchSign(Integer meta, int count) {
        super(BIRCH_SIGN, meta, count, "Birch Sign", new BlockBirchSignPost());
    }
}
