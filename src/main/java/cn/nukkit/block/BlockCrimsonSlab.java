package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonSlab(BlockState blockstate) {
        super(blockstate);
    }
}