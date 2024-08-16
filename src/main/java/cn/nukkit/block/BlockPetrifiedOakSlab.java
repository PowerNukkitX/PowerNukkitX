package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPetrifiedOakSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(PETRIFIED_OAK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockPetrifiedOakSlab(BlockState blockState) {
        super(blockState, PETRIFIED_OAK_DOUBLE_SLAB);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Petrified Oak";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TYPE_NONE;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}
