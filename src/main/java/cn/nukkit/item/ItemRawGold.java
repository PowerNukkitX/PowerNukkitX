package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
public class ItemRawGold extends Item {
    public ItemRawGold() {
        this(0, 1);
    }

    public ItemRawGold(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemRawGold(Integer meta, int count) {
        super(RAW_GOLD, 0, count, "Raw Gold");
    }
}
