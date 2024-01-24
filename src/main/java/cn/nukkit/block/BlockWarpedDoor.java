package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWarpedDoor;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Warped Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemWarpedDoor();
    }
}