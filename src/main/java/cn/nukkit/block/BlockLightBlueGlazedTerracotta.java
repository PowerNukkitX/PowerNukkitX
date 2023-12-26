package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueGlazedTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_glazed_terracotta", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}