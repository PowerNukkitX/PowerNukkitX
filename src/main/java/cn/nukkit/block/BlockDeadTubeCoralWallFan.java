package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadTubeCoralWallFan extends BlockDeadCoralWallFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_TUBE_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadTubeCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeadTubeCoralWallFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Tube Coral";
    }
}