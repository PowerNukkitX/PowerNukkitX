package cn.nukkit.item;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockAcaciaSignPost;

@PowerNukkitOnly
public class ItemAcaciaSign extends ItemSign {
    @PowerNukkitOnly
    public ItemAcaciaSign() {
        this(0, 1);
    }

    @PowerNukkitOnly
    public ItemAcaciaSign(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemAcaciaSign(Integer meta, int count) {
        super(ACACIA_SIGN, meta, count, "Acacia Sign", new BlockAcaciaSignPost());
    }
}
