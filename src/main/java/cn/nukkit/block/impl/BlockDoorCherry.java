package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorCherry;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockDoorCherry extends BlockDoorWood {
    public BlockDoorCherry() {
        this(0);
    }

    public BlockDoorCherry(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Door Block";
    }

    @Override
    public int getId() {
        return CHERRY_DOOR;
    }

    @Override
    public Item toItem() {
        return new ItemDoorCherry();
    }
}
