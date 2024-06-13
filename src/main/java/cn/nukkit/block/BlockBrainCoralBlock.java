package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrainCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(BRAIN_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrainCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBrainCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCoralBlock toDead() {
        return new BlockDeadBrainCoralBlock();
    }
}