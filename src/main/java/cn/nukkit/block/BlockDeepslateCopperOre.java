package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateCopperOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_copper_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateCopperOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateCopperOre(BlockState blockstate) {
        super(blockstate);
    }
}