package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockSlabCopperCutExposed extends BlockSlabCopperCut {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCutExposed() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCutExposed(int meta) {
        super(meta, EXPOSED_DOUBLE_CUT_COPPER_SLAB);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    protected BlockSlabCopperCutExposed(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return EXPOSED_CUT_COPPER_SLAB;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }

}
