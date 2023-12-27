package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateRedstoneOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_redstone_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }
}