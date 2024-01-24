package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedWarpedStem extends BlockStemStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_WARPED_STEM, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedWarpedStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedWarpedStem(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Stripped Warped Stem";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}