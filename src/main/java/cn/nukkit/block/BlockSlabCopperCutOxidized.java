package cn.nukkit.block;

import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockSlabCopperCutOxidized extends BlockSlabCopperCut {


    public BlockSlabCopperCutOxidized() {
        this(0);
    }


    public BlockSlabCopperCutOxidized(int meta) {
        super(meta, OXIDIZED_DOUBLE_CUT_COPPER_SLAB);
    }


    protected BlockSlabCopperCutOxidized(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return OXIDIZED_CUT_COPPER_SLAB;
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }

}
