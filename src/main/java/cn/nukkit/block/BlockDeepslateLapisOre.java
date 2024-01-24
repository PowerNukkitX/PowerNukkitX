package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateLapisOre extends BlockLapisOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_LAPIS_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateLapisOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateLapisOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Deepslate Lapis Ore";
    }

    @Override
    public double getHardness() {
        return 4.5;
    }
}