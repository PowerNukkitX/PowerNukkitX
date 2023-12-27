package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.ItemWoodenDoor;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenDoor extends BlockDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:wooden_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
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

    @Override
    public Item toItem() {
        return new ItemWoodenDoor();
    }
}