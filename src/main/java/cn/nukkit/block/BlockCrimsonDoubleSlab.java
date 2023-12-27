package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}