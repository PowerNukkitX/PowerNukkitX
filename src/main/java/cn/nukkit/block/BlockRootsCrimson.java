package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;


public class BlockRootsCrimson extends BlockRoots implements BlockFlowerPot.FlowerPotBlock {


    public BlockRootsCrimson() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CRIMSON_ROOTS;
    }

    @Override
    public String getName() {
        return "Crimson Roots";
    }

}
