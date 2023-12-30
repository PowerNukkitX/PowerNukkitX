package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}