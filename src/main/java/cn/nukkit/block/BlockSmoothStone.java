package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothStone extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_STONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothStone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothStone(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Smooth Stone";
    }
    
    @Override
    public double getHardness() {
        return 1.5;
    }
    
    @Override
    public double getResistance() {
        return 10;
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
    public boolean canHarvestWithHand() {
        return false;
    }

}
