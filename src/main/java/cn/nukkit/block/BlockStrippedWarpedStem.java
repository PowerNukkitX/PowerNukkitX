package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedWarpedStem extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_warped_stem", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedWarpedStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedWarpedStem(BlockState blockstate) {
        super(blockstate);
    }
}