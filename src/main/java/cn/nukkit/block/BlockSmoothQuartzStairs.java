package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothQuartzStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:smooth_quartz_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothQuartzStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothQuartzStairs(BlockState blockstate) {
        super(blockstate);
    }
}