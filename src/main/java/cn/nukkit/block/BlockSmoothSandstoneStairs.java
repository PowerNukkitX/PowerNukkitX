package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothSandstoneStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:smooth_sandstone_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothSandstoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothSandstoneStairs(BlockState blockstate) {
        super(blockstate);
    }
}