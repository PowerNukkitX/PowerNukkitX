package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteGlazedTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}