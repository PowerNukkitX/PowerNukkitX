package cn.nukkit.block;

import cn.nukkit.api.Since;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorBamboo;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooDoor extends BlockDoorWood {
    public BlockBambooDoor() {
        this(0);
    }

    public BlockBambooDoor(int meta) {
        super(meta);
    }


    public int getId() {
        return BAMBOO_DOOR;
    }

    public String getName() {
        return "Bamboo Door";
    }

    @Override
    public Item toItem() {
        return new ItemDoorBamboo();
    }
}