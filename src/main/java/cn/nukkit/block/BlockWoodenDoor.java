package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenDoor extends BlockDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(WOODEN_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Wood Door Block";
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}