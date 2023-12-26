package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
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