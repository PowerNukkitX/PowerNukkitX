package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
public class ItemRawCopper extends Item {
    public ItemRawCopper() {
        this(0, 1);
    }

    public ItemRawCopper(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemRawCopper(Integer meta, int count) {
        super(RAW_COPPER, 0, count, "Raw Copper");
    }
}
