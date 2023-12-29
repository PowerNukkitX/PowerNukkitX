package cn.nukkit.block;

import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockDoubleSlabCopperCutWeathered extends BlockDoubleSlabCopperCut {


    public BlockDoubleSlabCopperCutWeathered() {
        this(0);
    }


    public BlockDoubleSlabCopperCutWeathered(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public int getSingleSlabId() {
        return WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }

}
