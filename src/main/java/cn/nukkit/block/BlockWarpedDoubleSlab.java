package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}