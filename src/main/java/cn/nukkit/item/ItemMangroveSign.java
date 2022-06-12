package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.block.BlockMangroveSignPost;

public class ItemMangroveSign extends ItemSign {
    @PowerNukkitXOnly
    public ItemMangroveSign() {
        this(0, 1);
    }

    @PowerNukkitXOnly
    public ItemMangroveSign(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitXOnly
    public ItemMangroveSign(Integer meta, int count) {
        super(MANGROVE_SIGN, meta, count, "Mangrove Sign", new BlockMangroveSignPost());
    }
}
