package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author GoodLucky777
 */
public class BlockTuff extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF);

    public BlockTuff() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockTuff(BlockState blockState) {
        super(blockState);
    }
    
    @Override
    public String getName() {
        return "Tuff";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
    
    @Override
    public double getResistance() {
        return 6;
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
