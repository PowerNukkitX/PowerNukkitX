package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateGoldOre extends BlockGoldOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_GOLD_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateGoldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateGoldOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Deepslate Gold Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }
}