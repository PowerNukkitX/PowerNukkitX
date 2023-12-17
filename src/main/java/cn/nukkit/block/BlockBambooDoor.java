package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorBamboo;


public class BlockBambooDoor extends BlockDoorWood {
    public BlockBambooDoor() {
        this(0);
    }

    public BlockBambooDoor(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_DOOR;
    }

    @Override
    public String getName() {
        return "Bamboo Door";
    }

    @Override
    public Item toItem() {
        return new ItemDoorBamboo();
    }
}