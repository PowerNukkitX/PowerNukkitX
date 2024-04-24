package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadFireCoralFan extends BlockCoralFanDead {
     public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_FIRE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockDeadFireCoralFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockDeadFireCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}