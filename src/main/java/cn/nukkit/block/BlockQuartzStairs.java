package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockQuartzStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:quartz_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockQuartzStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockQuartzStairs(BlockState blockstate) {
        super(blockstate);
    }
}