package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author joserobjr
 */


public class BlockBricksNetherChiseled extends BlockBricksNether {


    public BlockBricksNetherChiseled() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CHISELED_NETHER_BRICKS;
    }

    @Override
    public String getName() {
        return "Chiseled Nether Bricks";
    }
}
