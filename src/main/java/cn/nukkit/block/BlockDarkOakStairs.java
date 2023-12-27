package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dark_oak_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakStairs(BlockState blockstate) {
        super(blockstate);
    }
}