package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_block", CommonBlockProperties.BLOCK_LIGHT_LEVEL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock(BlockState blockstate) {
        super(blockstate);
    }
}