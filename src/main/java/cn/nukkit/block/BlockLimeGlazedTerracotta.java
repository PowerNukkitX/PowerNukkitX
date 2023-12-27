package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLimeGlazedTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}