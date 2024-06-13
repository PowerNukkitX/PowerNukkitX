package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothStoneSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_STONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothStoneSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSmoothStoneSlab(BlockState blockstate) {
        super(blockstate);
    }
}