package cn.nukkit.block;

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