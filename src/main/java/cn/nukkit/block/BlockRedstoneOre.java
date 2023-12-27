package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedstoneOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:redstone_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }
}