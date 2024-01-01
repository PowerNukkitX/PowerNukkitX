package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Warped Wood Stairs";
    }
}