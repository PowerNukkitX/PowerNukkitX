package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadBrainCoralWallFan extends BlockDeadCoralWallFan {
     public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_BRAIN_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockDeadBrainCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockDeadBrainCoralWallFan(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getName() {
        return "Brain Coral";
    }
}