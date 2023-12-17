package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 * @since 2021-06-15
 */


public class BlockStairsCopperCutOxidizedWaxed extends BlockStairsCopperCutOxidized {


    public BlockStairsCopperCutOxidizedWaxed() {
        this(0);
    }


    public BlockStairsCopperCutOxidizedWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_CUT_COPPER_STAIRS;
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}
