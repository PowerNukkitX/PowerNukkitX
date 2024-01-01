package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedDoubleCutCopperSlab extends BlockOxidizedDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_oxidized_double_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getSingleSlabId() {
        return WAXED_OXIDIZED_CUT_COPPER_SLAB;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}