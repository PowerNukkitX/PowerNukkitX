package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadBrainCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_BRAIN_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadBrainCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeadBrainCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}