package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonStem extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_stem", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonStem(BlockState blockstate) {
        super(blockstate);
    }
}