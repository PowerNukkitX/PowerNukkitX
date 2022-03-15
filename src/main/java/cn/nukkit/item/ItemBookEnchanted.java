package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBookEnchanted extends Item {

    public ItemBookEnchanted() {
        this(0, 1);
    }

    public ItemBookEnchanted(Integer meta) {
        this(meta, 1);
    }

    public ItemBookEnchanted(Integer meta, int count) {
        super(ENCHANTED_BOOK, meta, count, "Enchanted Book");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public boolean applyEnchantments() {
        return false;
    }
}
