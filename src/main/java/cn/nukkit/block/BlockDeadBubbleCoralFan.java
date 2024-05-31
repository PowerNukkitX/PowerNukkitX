package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadBubbleCoralFan extends BlockCoralFanDead {
     public static final BlockProperties $1 = new BlockProperties(DEAD_BUBBLE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockDeadBubbleCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockDeadBubbleCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}