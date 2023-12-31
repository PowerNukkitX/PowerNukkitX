package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.DEAD_BIT;


public class BlockCoralFanHang3 extends BlockCoralFanHang {
    public static final BlockProperties PROPERTIES = new BlockProperties(CORAL_FAN_HANG3,
            CommonBlockProperties.CORAL_FAN_DIRECTION,
            CommonBlockProperties.CORAL_HANG_TYPE_BIT,
            DEAD_BIT);

    public BlockCoralFanHang3() {
        this(PROPERTIES.getDefaultState());
    }


    public BlockCoralFanHang3(BlockState blockstate) {
        super(blockstate);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getType() {
        return BlockCoral.TYPE_HORN;
    }
}
