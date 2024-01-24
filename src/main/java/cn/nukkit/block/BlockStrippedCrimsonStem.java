package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedCrimsonStem extends BlockStemStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_CRIMSON_STEM, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedCrimsonStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedCrimsonStem(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Stripped Crimson Stem";
    }
}