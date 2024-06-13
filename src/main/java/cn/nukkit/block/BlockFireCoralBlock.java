package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockFireCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(FIRE_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFireCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockFireCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCoralBlock toDead() {
        return new BlockDeadFireCoralBlock();
    }
}