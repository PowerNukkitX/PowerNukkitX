package cn.nukkit.block;

import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockSlabCopperCutWeathered extends BlockSlabCopperCut {


    public BlockSlabCopperCutWeathered() {
        this(0);
    }


    public BlockSlabCopperCutWeathered(int meta) {
        super(meta, WEATHERED_DOUBLE_CUT_COPPER_SLAB);
    }


    protected BlockSlabCopperCutWeathered(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return WEATHERED_CUT_COPPER_SLAB;
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }

}
