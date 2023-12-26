package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlackGlazedTerracotta extends Block {
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