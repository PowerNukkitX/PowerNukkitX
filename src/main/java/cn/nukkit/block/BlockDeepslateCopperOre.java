package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateCopperOre extends BlockCopperOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_COPPER_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateCopperOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateCopperOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Deepslate Copper Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }
}