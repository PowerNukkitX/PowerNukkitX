package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSandstoneSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(SANDSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSandstoneSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSandstoneSlab(BlockState blockstate) {
        super(blockstate);
    }
}