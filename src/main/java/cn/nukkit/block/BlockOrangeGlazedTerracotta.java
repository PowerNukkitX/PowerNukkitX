package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeGlazedTerracotta extends Block {
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