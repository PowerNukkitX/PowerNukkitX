package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockSlabCopperCutWaxed extends BlockSlabCopperCut {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCutWaxed() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCutWaxed(int meta) {
        super(meta, WAXED_DOUBLE_CUT_COPPER_SLAB);
    }

    @Override
    public int getId() {
        return WAXED_CUT_COPPER_SLAB;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean isWaxed() {
        return true;
    }
}
