package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooMosaicDoubleSlab extends BlockDoubleSlabBase {
    public BlockBambooMosaicDoubleSlab() {
    }

    @Override
    public String getSlabName() {
        return "Bamboo Mosaic";
    }

    @Override
    public int getSingleSlabId() {
        return BAMBOO_MOSAIC_SLAB;
    }

    public int getId() {
        return BAMBOO_MOSAIC_DOUBLE_SLAB;
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
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}