package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadFireCoralFan extends BlockCoralFanDead {
     public static final BlockProperties $1 = new BlockProperties(DEAD_FIRE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockDeadFireCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockDeadFireCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}