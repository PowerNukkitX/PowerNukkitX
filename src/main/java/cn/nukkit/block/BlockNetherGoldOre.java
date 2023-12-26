package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockNetherGoldOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:nether_gold_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherGoldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherGoldOre(BlockState blockstate) {
        super(blockstate);
    }
}