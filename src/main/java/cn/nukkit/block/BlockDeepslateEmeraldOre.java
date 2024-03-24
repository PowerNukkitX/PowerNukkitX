package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateEmeraldOre extends BlockEmeraldOre  {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_EMERALD_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
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