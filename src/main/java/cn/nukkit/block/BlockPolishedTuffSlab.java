package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedTuffSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_TUFF_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedTuffSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedTuffSlab(BlockState blockstate) {
        super(blockstate);
    }
}