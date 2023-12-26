package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLapisOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lapis_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLapisOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLapisOre(BlockState blockstate) {
        super(blockstate);
    }
}