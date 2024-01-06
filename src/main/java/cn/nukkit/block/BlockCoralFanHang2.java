package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;


public class BlockCoralFanHang2 extends BlockCoralFanHang {
    public static final BlockProperties PROPERTIES = new BlockProperties(CORAL_FAN_HANG2, CORAL_FAN_DIRECTION, CORAL_HANG_TYPE_BIT, DEAD_BIT);

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
    public int getType() {
        if (getPropertyValue(CORAL_HANG_TYPE_BIT)) {
            return BlockCoral.TYPE_FIRE;
        } else {
            return BlockCoral.TYPE_BUBBLE;
        }
    }
}
