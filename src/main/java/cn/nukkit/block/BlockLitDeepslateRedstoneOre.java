package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLitDeepslateRedstoneOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lit_deepslate_redstone_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitDeepslateRedstoneOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitDeepslateRedstoneOre(BlockState blockstate) {
        super(blockstate);
    }
}