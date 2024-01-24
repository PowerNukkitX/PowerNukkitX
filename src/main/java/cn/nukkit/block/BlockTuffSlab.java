package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTuffSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffSlab(BlockState blockstate) {
        super(blockstate);
    }
}