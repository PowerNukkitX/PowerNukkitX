package cn.nukkit.item;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockDarkOakSignPost;

@PowerNukkitOnly
public class ItemDarkOakSign extends ItemSign {
    @PowerNukkitOnly
    public ItemDarkOakSign() {
        this(0, 1);
    }

    @PowerNukkitOnly
    public ItemDarkOakSign(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemDarkOakSign(Integer meta, int count) {
        super(DARKOAK_SIGN, meta, count, "Dark Oak Sign", new BlockDarkOakSignPost());
    }
}
