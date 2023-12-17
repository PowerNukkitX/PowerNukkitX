package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-06-14
 */


public class BlockDoubleSlabCopperCutOxidized extends BlockDoubleSlabCopperCut {


    public BlockDoubleSlabCopperCutOxidized() {
        this(0);
    }


    public BlockDoubleSlabCopperCutOxidized(int meta) {
        super(meta);
    }


    @Override
    public int getSingleSlabId() {
        return OXIDIZED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }

}
