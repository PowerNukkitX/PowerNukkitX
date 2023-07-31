package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockRoots;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockRootsWarped extends BlockRoots implements BlockFlowerPot.FlowerPotBlock {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
