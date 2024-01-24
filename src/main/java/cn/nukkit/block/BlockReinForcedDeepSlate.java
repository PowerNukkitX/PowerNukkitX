package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockReinForcedDeepSlate extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(REINFORCED_DEEPSLATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockReinForcedDeepSlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockReinForcedDeepSlate(BlockState blockstate) {
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
