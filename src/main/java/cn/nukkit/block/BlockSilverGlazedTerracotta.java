package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSilverGlazedTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:silver_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSilverGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSilverGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}