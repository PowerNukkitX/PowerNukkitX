package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedTrapdoor extends BlockTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}