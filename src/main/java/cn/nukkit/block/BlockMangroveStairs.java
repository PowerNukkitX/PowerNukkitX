package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mangrove Wood Stairs";
    }
}