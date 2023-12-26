package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateEmeraldOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_emerald_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateEmeraldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateEmeraldOre(BlockState blockstate) {
        super(blockstate);
    }
}