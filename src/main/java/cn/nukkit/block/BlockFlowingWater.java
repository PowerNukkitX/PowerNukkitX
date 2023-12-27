package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockFlowingWater extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:flowing_water", CommonBlockProperties.LIQUID_DEPTH);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFlowingWater() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFlowingWater(BlockState blockstate) {
        super(blockstate);
    }
}