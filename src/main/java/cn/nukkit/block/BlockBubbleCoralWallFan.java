package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBubbleCoralWallFan extends BlockCoralWallFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(BUBBLE_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBubbleCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBubbleCoralWallFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Bubble Coral";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadBubbleCoralWallFan();
    }
}