package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStonebrick extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONEBRICK, CommonBlockProperties.STONE_BRICK_TYPE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStonebrick() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStonebrick(BlockState blockstate) {
        super(blockstate);
    }
}