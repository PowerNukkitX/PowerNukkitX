package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrainCoralWallFan extends BlockCoralWallFan {
     public static final BlockProperties PROPERTIES = new BlockProperties(BRAIN_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockBrainCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockBrainCoralWallFan(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getName() {
        return "Brain Coral";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadBrainCoralWallFan();
    }
}