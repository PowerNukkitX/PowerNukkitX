package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadBubbleCoralFan extends BlockCoralFanDead {
     public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_BUBBLE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockDeadBubbleCoralFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockDeadBubbleCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}