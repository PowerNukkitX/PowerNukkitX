package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCyanGlazedTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}