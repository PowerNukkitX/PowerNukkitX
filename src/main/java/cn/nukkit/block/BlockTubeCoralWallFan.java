package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTubeCoralWallFan extends BlockCoralWallFan {
     public static final BlockProperties PROPERTIES = new BlockProperties(TUBE_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockTubeCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockTubeCoralWallFan(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getName() {
        return "Tube Coral";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadTubeCoralWallFan();
    }
}