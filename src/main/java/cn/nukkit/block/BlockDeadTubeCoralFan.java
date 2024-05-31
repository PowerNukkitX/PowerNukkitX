package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadTubeCoralFan extends BlockCoralFanDead {
     public static final BlockProperties $1 = new BlockProperties(DEAD_TUBE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockDeadTubeCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    


    public BlockDeadTubeCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}