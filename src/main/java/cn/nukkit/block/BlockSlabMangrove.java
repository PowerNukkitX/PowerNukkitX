package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

public class BlockSlabMangrove extends BlockSlab {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockSlabMangrove() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockSlabMangrove(int meta) {
        super(meta, DOUBLE_MANGROVE_SLAB);
    }

    @Override
    public String getName() {
        return (isOnTop() ? "Upper " : "") + getSlabName() + " Wood Slab";
    }

    @Override
    public int getId() {
        return MANGROVE_SLAB;
    }

    @PowerNukkitOnly
    @Override
    public String getSlabName() {
        return "Mangrove";
    }

    @PowerNukkitOnly
    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return SIMPLE_SLAB_PROPERTIES;
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
    public BlockColor getColor() {
        return BlockColor.BROWNISH_RED;
    }
}
