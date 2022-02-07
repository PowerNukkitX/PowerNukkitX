package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 * @since 2021-06-15
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockStairsCopperCutOxidizedWaxed extends BlockStairsCopperCutOxidized {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockStairsCopperCutOxidizedWaxed() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockStairsCopperCutOxidizedWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_CUT_COPPER_STAIRS;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public boolean isWaxed() {
        return true;
    }
}
