package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateIronOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_iron_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateIronOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateIronOre(BlockState blockstate) {
        super(blockstate);
    }
}