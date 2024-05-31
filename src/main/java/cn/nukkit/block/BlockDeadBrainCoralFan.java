package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadBrainCoralFan extends BlockCoralFanDead {
     public static final BlockProperties $1 = new BlockProperties(DEAD_BRAIN_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockDeadBrainCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockDeadBrainCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}