package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockResinBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(RESIN_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockResinBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockResinBricks(BlockState blockstate) {
        super(blockstate);
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

}