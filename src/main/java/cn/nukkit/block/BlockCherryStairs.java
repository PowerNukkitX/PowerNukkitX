package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryStairs(BlockState blockstate) {
        super(blockstate);
    }
}