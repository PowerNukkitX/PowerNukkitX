package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockSlab;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.ItemTool;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooMosaicSlab extends BlockSlab {
    public BlockBambooMosaicSlab() {
        this(0);
    }

    public BlockBambooMosaicSlab(int meta) {
        super(meta, BAMBOO_MOSAIC_DOUBLE_SLAB);
    }

    public int getId() {
        return BAMBOO_MOSAIC_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Bamboo Mosaic";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId();
    }

    @Override
    public BlockProperties getProperties() {
        return SIMPLE_SLAB_PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
