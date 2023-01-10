package cn.nukkit.level.biome.impl.cave;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.type.CaveBiome;

//todo: 等待实现
@PowerNukkitXOnly
@Since("1.19.40-r3")
public class DeepDarkBiome extends CaveBiome {
    @Override
    public String getName() {
        return "Deep Dark";
    }
}
