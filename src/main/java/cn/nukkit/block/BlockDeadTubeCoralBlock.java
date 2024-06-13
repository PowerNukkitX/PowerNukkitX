package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadTubeCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_TUBE_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadTubeCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeadTubeCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}