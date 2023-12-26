package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDetectorRail extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:detector_rail", CommonBlockProperties.RAIL_DATA_BIT, CommonBlockProperties.RAIL_DIRECTION_6);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDetectorRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDetectorRail(BlockState blockstate) {
        super(blockstate);
    }
}