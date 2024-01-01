package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothSandstoneStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:smooth_sandstone_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothSandstoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothSandstoneStairs(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public String getName() {
        return "Smooth Sandstone Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}