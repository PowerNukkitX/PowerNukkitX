package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.PILLAR_AXIS;
public class BlockOchreFroglight extends BlockFroglight {

    public static final BlockProperties PROPERTIES = new BlockProperties(OCHRE_FROGLIGHT, PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOchreFroglight() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOchreFroglight(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Ochre Froglight";
    }
}
