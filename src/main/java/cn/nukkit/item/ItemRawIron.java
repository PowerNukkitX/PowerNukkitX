package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;

/**
 * @author joserobjr
 * @since 2021-06-12
 */
@PowerNukkitOnly
public class ItemRawIron extends Item {
    public ItemRawIron() {
        this(0, 1);
    }

    public ItemRawIron(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemRawIron(Integer meta, int count) {
        super(RAW_IRON, 0, count, "Raw Iron");
    }
}
