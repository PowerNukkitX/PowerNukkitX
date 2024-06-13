package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrickSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrickSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBrickSlab(BlockState blockstate) {
        super(blockstate);
    }
}