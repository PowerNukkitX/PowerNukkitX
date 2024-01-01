package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMudBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mud_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMudBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMudBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}