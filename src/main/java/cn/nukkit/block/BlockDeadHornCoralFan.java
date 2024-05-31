package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadHornCoralFan extends BlockCoralFanDead {
     public static final BlockProperties $1 = new BlockProperties(DEAD_HORN_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockDeadHornCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    


    public BlockDeadHornCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}