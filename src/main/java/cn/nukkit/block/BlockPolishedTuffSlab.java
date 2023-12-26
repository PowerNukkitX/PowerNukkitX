package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedTuffSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_tuff_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedTuffSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedTuffSlab(BlockState blockstate) {
        super(blockstate);
    }
}