package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBambooDoor;
import org.jetbrains.annotations.NotNull;


public class BlockBambooDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Bamboo Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemBambooDoor();
    }
}