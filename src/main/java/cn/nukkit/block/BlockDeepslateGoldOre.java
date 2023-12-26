package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateGoldOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_gold_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateGoldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateGoldOre(BlockState blockstate) {
        super(blockstate);
    }
}