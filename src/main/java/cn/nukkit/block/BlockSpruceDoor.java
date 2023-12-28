package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorSpruce;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:spruce_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Spruce Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemDoorSpruce();
    }
}