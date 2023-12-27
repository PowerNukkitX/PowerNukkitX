package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLapisOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lapis_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLapisOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLapisOre(BlockState blockstate) {
        super(blockstate);
    }
}