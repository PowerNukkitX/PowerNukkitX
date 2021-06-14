package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockDoubleSlabCopperCutWeatheredWaxed extends BlockDoubleSlabCopperCutWeathered {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockDoubleSlabCopperCutWeatheredWaxed() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockDoubleSlabCopperCutWeatheredWaxed(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getSingleSlabId() {
        return WAXED_WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean isWaxed() {
        return true;
    }
}
