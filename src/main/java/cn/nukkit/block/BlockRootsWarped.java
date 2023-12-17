package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;


public class BlockRootsWarped extends BlockRoots implements BlockFlowerPot.FlowerPotBlock {


    public BlockRootsWarped() {
        // Does nothing
    }

    @Override
    public int getId() {
        return WARPED_ROOTS;
    }

    @Override
    public String getName() {
        return "Warped Roots";
    }

}
