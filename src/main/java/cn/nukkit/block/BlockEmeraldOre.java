package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockEmeraldOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:emerald_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEmeraldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEmeraldOre(BlockState blockstate) {
        super(blockstate);
    }
}