package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.CORAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.CORAL_HANG_TYPE_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.DEAD_BIT;


public class BlockCoralFanHang2 extends BlockCoralFanHang {
    public static final BlockProperties PROPERTIES = new BlockProperties(CORAL_FAN_HANG2, CORAL_DIRECTION, CORAL_HANG_TYPE_BIT, DEAD_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoralFanHang2() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoralFanHang2(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block getDeadCoralFan() {
        if (getPropertyValue(CORAL_HANG_TYPE_BIT)) {
            return new BlockDeadFireCoralFan();
        } else {
            return new BlockDeadBubbleCoralFan();
        }
    }
}
