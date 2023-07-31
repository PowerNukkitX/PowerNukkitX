package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockWoodStripped;
import cn.nukkit.block.property.value.WoodType;

@PowerNukkitOnly
public class BlockWoodStrippedOak extends BlockWoodStripped {
    @PowerNukkitOnly
    public BlockWoodStrippedOak() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockWoodStrippedOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_OAK_LOG;
    }

    @PowerNukkitOnly
    @Override
    public WoodType getWoodType() {
        return WoodType.OAK;
    }
}
