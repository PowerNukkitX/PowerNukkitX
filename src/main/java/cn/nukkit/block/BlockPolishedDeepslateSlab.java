package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDeepslateSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_deepslate_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDeepslateSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDeepslateSlab(BlockState blockstate) {
        super(blockstate);
    }
}