package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothRedSandstoneStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:smooth_red_sandstone_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothRedSandstoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothRedSandstoneStairs(BlockState blockstate) {
        super(blockstate);
    }
}