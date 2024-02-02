package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import org.jetbrains.annotations.NotNull;

public class BlockCherryDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cherry Door Block";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.CHERRY_DOOR);
    }
}