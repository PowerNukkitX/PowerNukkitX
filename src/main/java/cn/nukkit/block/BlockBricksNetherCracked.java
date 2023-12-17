package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 */


public class BlockBricksNetherCracked extends BlockBricksNether {


    public BlockBricksNetherCracked() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CRACKED_NETHER_BRICKS;
    }

    @Override
    public String getName() {
        return "Cracked Nether Bricks";
    }
}
