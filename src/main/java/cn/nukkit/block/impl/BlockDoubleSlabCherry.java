package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockDoubleSlabBase;
import cn.nukkit.block.BlockSlab;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockDoubleSlabCherry extends BlockDoubleSlabBase {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoubleSlabCherry() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected BlockDoubleSlabCherry(int meta) {
        super(meta);
    }

    @PowerNukkitOnly
    @Override
    public String getSlabName() {
        return "Double Cherry Slab";
    }

    @Override
    public int getId() {
        return DOUBLE_CHERRY_SLAB;
    }

    @NotNull @Override
    public BlockProperties getProperties() {
        return BlockSlab.SIMPLE_SLAB_PROPERTIES;
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

    @Override
    public int getSingleSlabId() {
        return CHERRY_SLAB;
    }
}
