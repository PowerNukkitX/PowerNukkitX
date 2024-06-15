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
        return switch (state.getIdentifier()) {
            case BlockID.QUARTZ_SLAB -> "quartz";
            case BlockID.PETRIFIED_OAK_SLAB -> "wood";
            case BlockID.STONE_BRICK_SLAB -> "stone_brick";
            case BlockID.BRICK_SLAB -> "brick";
            case BlockID.SMOOTH_STONE_SLAB -> "smooth_stone";
            case BlockID.SANDSTONE_SLAB -> "sandstone";
            case BlockID.NETHER_BRICK_SLAB -> "nether_brick";
            case BlockID.COBBLESTONE_SLAB -> "cobblestone";
            default -> "unknown";
        };
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