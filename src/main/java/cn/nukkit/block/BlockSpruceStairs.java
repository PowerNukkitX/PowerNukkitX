package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Spruce Wood Stairs";
    }
}