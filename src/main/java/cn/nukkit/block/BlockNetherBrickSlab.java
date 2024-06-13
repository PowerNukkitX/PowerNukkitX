package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockNetherBrickSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherBrickSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockNetherBrickSlab(BlockState blockstate) {
        super(blockstate);
    }
}