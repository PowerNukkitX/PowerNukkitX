package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateCoalOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_coal_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateCoalOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateCoalOre(BlockState blockstate) {
        super(blockstate);
    }
}