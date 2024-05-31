package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.CORAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.CORAL_HANG_TYPE_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.DEAD_BIT;

public class BlockCoralFanHang3 extends BlockCoralFanHang {
    public static final BlockProperties $1 = new BlockProperties(CORAL_FAN_HANG3, CORAL_DIRECTION, CORAL_HANG_TYPE_BIT, DEAD_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCoralFanHang3() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCoralFanHang3(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadHornCoralFan();
    }
}
