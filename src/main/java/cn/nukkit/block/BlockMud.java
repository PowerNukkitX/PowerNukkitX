package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMud extends BlockSolid implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(MUD);

    public BlockMud() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMud(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Mud";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }
}
