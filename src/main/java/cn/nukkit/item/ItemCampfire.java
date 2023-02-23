package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockCampfire;

@PowerNukkitOnly
public class ItemCampfire extends Item {

    @PowerNukkitOnly
    public ItemCampfire() {
        this(0, 1);
    }

    @PowerNukkitOnly
    public ItemCampfire(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemCampfire(Integer meta, int count) {
        super(CAMPFIRE, meta, count, "Campfire");
        this.block = new BlockCampfire();
    }
}
