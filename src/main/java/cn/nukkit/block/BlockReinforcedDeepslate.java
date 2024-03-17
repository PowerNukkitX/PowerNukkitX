package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

public class BlockReinforcedDeepslate extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(REINFORCED_DEEPSLATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockReinforcedDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockReinforcedDeepslate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "ReinForced DeepSlate";
    }

    @Override
    public double getResistance() {
        return 1200.0;
    }
}
