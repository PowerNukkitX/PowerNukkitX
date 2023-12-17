package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;


public class BlockBricksBlackstonePolishedCracked extends BlockBricksBlackstonePolished {


    public BlockBricksBlackstonePolishedCracked() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CRACKED_POLISHED_BLACKSTONE_BRICKS;
    }

    @Override
    public String getName() {
        return "Cracked Polished Blackstone Bricks";
    }
}
