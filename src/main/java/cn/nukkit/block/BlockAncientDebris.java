package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockAncientDebris extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(ANCIENT_DEBRIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAncientDebris() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAncientDebris(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Ancient Derbris";
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }
    
    @Override
    public double getResistance() {
        return 1200;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 30;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
