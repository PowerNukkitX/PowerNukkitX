package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:spruce_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceStairs(BlockState blockstate) {
        super(blockstate);
    }
}