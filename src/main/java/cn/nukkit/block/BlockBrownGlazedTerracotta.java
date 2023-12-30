package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}