package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedGranite extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_granite");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedGranite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedGranite(BlockState blockstate) {
        super(blockstate);
    }
}