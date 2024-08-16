package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadHornCoralWallFan extends BlockDeadCoralWallFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_HORN_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadHornCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeadHornCoralWallFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Horn Coral";
    }
}