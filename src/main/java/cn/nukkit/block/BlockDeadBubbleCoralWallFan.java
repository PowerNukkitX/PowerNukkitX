package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadBubbleCoralWallFan extends BlockDeadCoralWallFan {
     public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_BUBBLE_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockDeadBubbleCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockDeadBubbleCoralWallFan(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getName() {
        return "Bubble Coral";
    }
}