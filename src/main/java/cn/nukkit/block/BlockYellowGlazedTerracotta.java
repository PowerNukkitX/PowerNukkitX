package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockYellowGlazedTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}