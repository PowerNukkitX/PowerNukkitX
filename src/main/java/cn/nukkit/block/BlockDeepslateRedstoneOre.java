package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateRedstoneOre extends BlockRedstoneOre {
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

    @Override
    public String getName() {
        return "Deepslate Redstone Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }
}