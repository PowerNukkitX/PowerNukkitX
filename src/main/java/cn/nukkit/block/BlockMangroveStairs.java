package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveStairs(BlockState blockstate) {
        super(blockstate);
    }
}