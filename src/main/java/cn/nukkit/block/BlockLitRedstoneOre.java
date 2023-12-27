package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLitRedstoneOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lit_redstone_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }
}