package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockQuartzOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:quartz_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockQuartzOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockQuartzOre(BlockState blockstate) {
        super(blockstate);
    }
}