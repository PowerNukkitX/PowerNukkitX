package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBrickSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBrickSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockStoneBrickSlab(BlockState blockstate) {
        super(blockstate);
    }
}