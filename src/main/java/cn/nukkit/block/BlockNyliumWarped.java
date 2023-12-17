package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;


public class BlockNyliumWarped extends BlockNylium {


    public BlockNyliumWarped() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Warped Nylium";
    }

    @Override
    public int getId() {
        return WARPED_NYLIUM;
    }

}
