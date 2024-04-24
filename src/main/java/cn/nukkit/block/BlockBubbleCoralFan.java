package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBubbleCoralFan extends BlockCoralFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(BUBBLE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBubbleCoralFan() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBubbleCoralFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Bubble Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadBubbleCoralFan();
    }
}