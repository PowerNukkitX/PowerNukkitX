package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStoneStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stone_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneStairs(BlockState blockstate) {
        super(blockstate);
    }
}