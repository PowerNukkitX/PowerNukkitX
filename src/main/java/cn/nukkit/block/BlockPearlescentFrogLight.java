package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockPearlescentFrogLight extends BlockFrogLight {
    @Override
    public String getName() {
        return "Pearlescent FrogLight";
    }

    @Override
    public int getId() {
        return PEARLESCENT_FROGLIGHT;
    }
}
