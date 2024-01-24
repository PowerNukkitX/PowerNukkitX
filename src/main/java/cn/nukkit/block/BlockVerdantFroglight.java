package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockVerdantFroglight extends BlockFroglight {

    public static final BlockProperties PROPERTIES = new BlockProperties(VERDANT_FROGLIGHT,
            PILLAR_AXIS);

    public BlockVerdantFroglight() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockVerdantFroglight(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Verdant Froglight";
    }
}
