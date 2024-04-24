package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadTubeCoralFan extends BlockCoralFanDead {
     public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_TUBE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockDeadTubeCoralFan() {
        super(PROPERTIES.getDefaultState());
    }


    public BlockDeadTubeCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}