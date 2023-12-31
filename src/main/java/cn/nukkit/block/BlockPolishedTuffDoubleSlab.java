package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedTuffDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_TUFF_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedTuffDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedTuffDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}