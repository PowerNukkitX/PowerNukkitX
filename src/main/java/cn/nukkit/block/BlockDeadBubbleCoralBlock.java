package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadBubbleCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_BUBBLE_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadBubbleCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeadBubbleCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}