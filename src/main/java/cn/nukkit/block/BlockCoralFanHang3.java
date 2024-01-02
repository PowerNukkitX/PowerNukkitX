package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;

public class BlockCoralFanHang3 extends BlockCoralFanHang {
    public static final BlockProperties PROPERTIES = new BlockProperties(CORAL_FAN_HANG3, CORAL_FAN_DIRECTION, CORAL_HANG_TYPE_BIT, DEAD_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoralFanHang3() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoralFanHang3(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getType() {
        return BlockCoral.TYPE_HORN;
    }
}
