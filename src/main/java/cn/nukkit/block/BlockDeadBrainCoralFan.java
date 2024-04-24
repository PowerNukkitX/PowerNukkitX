package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadBrainCoralFan extends BlockCoralFanDead {
     public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_BRAIN_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockDeadBrainCoralFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockDeadBrainCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}