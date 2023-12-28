package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorBirch;
import org.jetbrains.annotations.NotNull;

public class BlockBirchDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Birch Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemDoorBirch();
    }
}