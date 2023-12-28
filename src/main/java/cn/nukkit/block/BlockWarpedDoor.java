package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorWarped;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
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
        return new ItemDoorWarped();
    }
}