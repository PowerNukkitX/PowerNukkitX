package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedSlab(BlockState blockstate) {
        super(blockstate);
    }
}