package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBubbleCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(BUBBLE_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBubbleCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBubbleCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCoralBlock toDead() {
        return new BlockDeadBubbleCoralBlock();
    }
}