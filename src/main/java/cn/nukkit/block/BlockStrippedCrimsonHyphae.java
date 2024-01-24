package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedCrimsonHyphae extends BlockStemStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_CRIMSON_HYPHAE, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedCrimsonHyphae() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedCrimsonHyphae(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedAcaciaLog.PROPERTIES.getDefaultState();
    }
}