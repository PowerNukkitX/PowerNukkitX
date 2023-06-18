package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.ItemTool;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooSlab extends BlockSlab {
    public BlockBambooSlab() {
        this(0);
    }

    public BlockBambooSlab(int meta) {
        super(meta, BAMBOO_DOUBLE_SLAB);
    }

    public int getId() {
        return BAMBOO_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Bamboo";
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