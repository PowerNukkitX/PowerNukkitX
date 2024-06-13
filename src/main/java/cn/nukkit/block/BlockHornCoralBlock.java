package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHornCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(HORN_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHornCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockHornCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCoralBlock toDead() {
        return new BlockDeadHornCoralBlock();
    }
}