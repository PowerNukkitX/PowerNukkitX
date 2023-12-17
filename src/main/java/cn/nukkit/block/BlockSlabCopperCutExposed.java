package cn.nukkit.block;

import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockSlabCopperCutExposed extends BlockSlabCopperCut {


    public BlockSlabCopperCutExposed() {
        this(0);
    }


    public BlockSlabCopperCutExposed(int meta) {
        super(meta, EXPOSED_DOUBLE_CUT_COPPER_SLAB);
    }


    protected BlockSlabCopperCutExposed(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return EXPOSED_CUT_COPPER_SLAB;
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }

}
