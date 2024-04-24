package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrainCoralFan extends BlockCoralFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(BRAIN_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrainCoralFan() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBrainCoralFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Brain Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadBrainCoralFan();
    }
}