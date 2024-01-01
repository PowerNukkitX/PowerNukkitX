package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedCutCopperStairs extends BlockCutCopperStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_cut_copper_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCutCopperStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCutCopperStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}