package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockChiseledQuartzBlock extends BlockQuartzBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_QUARTZ_BLOCK, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledQuartzBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledQuartzBlock(BlockState blockstate) {
        super(blockstate);
    }
}
