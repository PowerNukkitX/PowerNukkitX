package cn.nukkit.block;

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