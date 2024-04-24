package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTubeCoralFan extends BlockCoralFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUBE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTubeCoralFan() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockTubeCoralFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Tube Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadTubeCoralFan();
    }
}