package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.impl.BlockKelp;

@PowerNukkitOnly
public class ItemKelp extends Item {

    @PowerNukkitOnly
    public ItemKelp() {
        this(0, 1);
    }

    @PowerNukkitOnly
    public ItemKelp(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemKelp(Integer meta, int count) {
        super(KELP, meta, count, "Kelp");
        this.block = new BlockKelp();
    }
}
