package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchStairs(BlockState blockstate) {
        super(blockstate);
    }
}