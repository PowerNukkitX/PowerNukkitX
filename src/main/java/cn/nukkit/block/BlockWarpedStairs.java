package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedStairs(BlockState blockstate) {
        super(blockstate);
    }
}