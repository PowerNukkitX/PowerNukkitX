package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import cn.nukkit.item.ItemTool;

import java.util.Locale;

public abstract class BlockStoneBlockSlab extends BlockSlab {
    public BlockStoneBlockSlab(BlockState blockstate) {
        super(blockstate, getDoubleSlabState(getType(blockstate)));
    }

    public static BlockState getDoubleSlabState(String string) {
        StoneSlabType stoneSlabType = StoneSlabType.valueOf(string.toUpperCase(Locale.ENGLISH));
        return BlockDoubleStoneBlockSlab.PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE, stoneSlabType);
    }

    public static String getType(BlockState state) {
        if (state.equals(BlockQuartzSlab.PROPERTIES.getDefaultState())) {
            return "quartz";
        } else if (state.equals(BlockPetrifiedOakSlab.PROPERTIES.getDefaultState())) {
            return "wood";
        } else if (state.equals(BlockStoneBrickSlab.PROPERTIES.getDefaultState())) {
            return "stone_brick";
        } else if (state.equals(BlockBrickSlab.PROPERTIES.getDefaultState())) {
            return "brick";
        } else if (state.equals(BlockSmoothStoneSlab.PROPERTIES.getDefaultState())) {
            return "smooth_stone";
        } else if (state.equals(BlockSandstoneSlab.PROPERTIES.getDefaultState())) {
            return "sandstone";
        } else if (state.equals(BlockNetherBrickSlab.PROPERTIES.getDefaultState())) {
            return "nether_brick";
        } else if (state.equals(BlockCobblestoneSlab.PROPERTIES.getDefaultState())) {
            return "cobblestone";
        } else {
            return "unknown";
        }
    }

    @Override
    public String getSlabName() {
        return getSlabType().name();
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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId()) && getSlabType().equals(slab.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE));
    }

    public abstract StoneSlabType getSlabType();
}