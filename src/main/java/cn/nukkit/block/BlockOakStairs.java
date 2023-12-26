package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOakStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oak_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakStairs(BlockState blockstate) {
        super(blockstate);
    }
}