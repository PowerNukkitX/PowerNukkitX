package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadFireCoralWallFan extends BlockDeadCoralWallFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_FIRE_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadFireCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeadFireCoralWallFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Fire Coral";
    }
}