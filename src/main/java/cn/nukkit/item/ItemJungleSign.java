package cn.nukkit.item;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockJungleSignPost;

@PowerNukkitOnly
public class ItemJungleSign extends ItemSign {
    @PowerNukkitOnly
    public ItemJungleSign() {
        this(0, 1);
    }

    @PowerNukkitOnly
    public ItemJungleSign(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemJungleSign(Integer meta, int count) {
        super(JUNGLE_SIGN, meta, count, "Jungle Sign", new BlockJungleSignPost());
    }
}
