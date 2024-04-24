package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Acacia Door Block";
    }
}