package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadHornCoralFan extends BlockCoralFanDead {
     public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_HORN_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockDeadHornCoralFan() {
        super(PROPERTIES.getDefaultState());
    }


    public BlockDeadHornCoralFan(BlockState blockstate) {
         super(blockstate);
     }
}