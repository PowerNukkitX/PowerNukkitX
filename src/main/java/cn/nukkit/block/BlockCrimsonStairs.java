package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonStairs(BlockState blockstate) {
        super(blockstate);
    }
}