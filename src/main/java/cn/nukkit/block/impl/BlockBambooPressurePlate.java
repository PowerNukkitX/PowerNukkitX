package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooPressurePlate extends BlockPressurePlateWood {
    public BlockBambooPressurePlate(int meta) {
        super(meta);
    }

    public BlockBambooPressurePlate() {
        this(0);
    }

    public int getId() {
        return BAMBOO_PRESSURE_PLATE;
    }

    public String getName() {
        return "Bamboo Pressure Plate";
    }
}
