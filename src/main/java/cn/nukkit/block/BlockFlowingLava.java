package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockFlowingLava extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:flowing_lava", CommonBlockProperties.LIQUID_DEPTH);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFlowingLava() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFlowingLava(BlockState blockstate) {
        super(blockstate);
    }
}