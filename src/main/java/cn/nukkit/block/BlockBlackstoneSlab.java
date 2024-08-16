package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockBlackstoneSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLACKSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackstoneSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackstoneSlab(BlockState blockstate) {
        super(blockstate, BLACKSTONE_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Blackstone";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}