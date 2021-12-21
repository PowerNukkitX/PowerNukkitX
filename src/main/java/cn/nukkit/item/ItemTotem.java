package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;

public class ItemTotem extends Item {

    @PowerNukkitOnly
    public ItemTotem() {
        this(0, 1);
    }

    public ItemTotem(Integer meta) {
        this(meta, 1);
    }

    public ItemTotem(Integer meta, int count) {
        super(TOTEM, meta, count, "Totem of Undying");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
