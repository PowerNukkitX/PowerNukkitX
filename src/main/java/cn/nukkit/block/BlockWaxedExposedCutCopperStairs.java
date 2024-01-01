package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCutCopperStairs extends BlockExposedCutCopperStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_exposed_cut_copper_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCutCopperStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCutCopperStairs(BlockState blockstate) {
        super(blockstate);
    }



    @Override
    public boolean isWaxed() {
        return true;
    }
}