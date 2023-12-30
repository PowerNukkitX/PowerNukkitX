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

    @Override
    public String getName() {
        return "Deepslate Emerald Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }
}