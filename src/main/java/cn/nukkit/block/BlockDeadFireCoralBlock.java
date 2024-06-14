package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeadFireCoralBlock extends BlockCoralBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_FIRE_CORAL_BLOCK);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadFireCoralBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeadFireCoralBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}