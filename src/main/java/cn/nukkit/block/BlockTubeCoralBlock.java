package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockTubeCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUBE_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTubeCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockTubeCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCoralBlock toDead() {
        return new BlockDeadTubeCoralBlock();
    }
}