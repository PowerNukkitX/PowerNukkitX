package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBrickDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_BRICK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}