package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


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