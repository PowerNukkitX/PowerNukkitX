package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateDiamondOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_diamond_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateDiamondOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateDiamondOre(BlockState blockstate) {
        super(blockstate);
    }
}