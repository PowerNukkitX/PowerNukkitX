package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlackGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}