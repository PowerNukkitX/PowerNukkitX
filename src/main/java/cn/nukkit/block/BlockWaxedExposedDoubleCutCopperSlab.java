package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedDoubleCutCopperSlab extends BlockExposedDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_exposed_double_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getSingleSlabId() {
        return WAXED_EXPOSED_CUT_COPPER_SLAB;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}