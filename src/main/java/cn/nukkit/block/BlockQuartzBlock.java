package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockQuartzBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:quartz_block", CommonBlockProperties.CHISEL_TYPE, CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockQuartzBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockQuartzBlock(BlockState blockstate) {
        super(blockstate);
    }
}