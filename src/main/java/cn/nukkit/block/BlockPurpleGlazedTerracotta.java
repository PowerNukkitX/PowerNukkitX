package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleGlazedTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}