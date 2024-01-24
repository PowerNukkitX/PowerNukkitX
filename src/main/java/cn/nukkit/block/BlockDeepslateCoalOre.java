package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateCoalOre extends BlockCoalOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_COAL_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateCoalOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateCoalOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Deeplsate Coal Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }
}