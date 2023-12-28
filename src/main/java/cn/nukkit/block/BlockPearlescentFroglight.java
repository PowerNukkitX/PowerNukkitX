package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockPearlescentFroglight extends BlockFroglight {

    public static final BlockProperties PROPERTIES = new BlockProperties(PEARLESCENT_FROGLIGHT,
            PILLAR_AXIS);

    public BlockPearlescentFroglight() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPearlescentFroglight(BlockState blockState) {
        super(blockState);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Pearlescent Froglight";
    }
}
