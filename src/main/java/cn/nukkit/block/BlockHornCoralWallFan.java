package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHornCoralWallFan extends BlockCoralWallFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(HORN_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHornCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockHornCoralWallFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Horn Coral";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadHornCoralWallFan();
    }
}