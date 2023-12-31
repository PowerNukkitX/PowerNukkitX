package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.CORAL_HANG_TYPE_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.DEAD_BIT;


public class BlockCoralFanHang2 extends BlockCoralFanHang {
    public static final BlockProperties PROPERTIES = new BlockProperties(CORAL_FAN_HANG2,
            CommonBlockProperties.CORAL_FAN_DIRECTION,
            CORAL_HANG_TYPE_BIT,
            DEAD_BIT);

    public BlockCoralFanHang2() {
        this(PROPERTIES.getDefaultState());
    }


    public BlockCoralFanHang2(BlockState blockstate) {
        super(blockstate);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
