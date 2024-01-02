package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBrickSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBrickSlab(BlockState blockstate) {
        super(blockstate);
    }
}