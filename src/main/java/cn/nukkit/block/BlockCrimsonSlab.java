package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonSlab(BlockState blockstate) {
        super(blockstate, CRIMSON_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Crimson";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
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

    @Override
    public double getHardness(){
        return 3.5;
    }

}