package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGrayGlazedTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}