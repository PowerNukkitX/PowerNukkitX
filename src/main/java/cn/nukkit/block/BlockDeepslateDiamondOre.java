package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateDiamondOre extends BlockDiamondOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_DIAMOND_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateDiamondOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateDiamondOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Deepslate Diamond Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }
}