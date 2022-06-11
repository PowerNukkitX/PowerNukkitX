package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockOchreFrogLight extends BlockFrogLight{
    @Override
    public String getName() {
        return "Ochre FrogLight";
    }

    @Override
    public int getId() {
        return OCHRE_FROGLIGHT;
    }
}
