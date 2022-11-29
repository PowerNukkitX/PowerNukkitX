package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.40-r4")
public class ItemDiscFragment5 extends Item {

    public ItemDiscFragment5() {
        this(0, 1);
    }

    public ItemDiscFragment5(Integer meta) {
        this(meta, 1);
    }

    public ItemDiscFragment5(Integer meta, int count) {
        super(DISC_FRAGMENT_5, 0, count, "Disc Fragment 5");
    }
}
