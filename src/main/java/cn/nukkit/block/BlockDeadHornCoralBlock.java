package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadHornCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_HORN_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadHornCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeadHornCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}