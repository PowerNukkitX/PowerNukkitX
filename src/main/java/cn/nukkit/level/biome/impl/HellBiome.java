package cn.nukkit.level.biome.impl;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.Biome;

@Deprecated
@PowerNukkitDifference(info = "新版下界不再使用此群系", since = "1.6.0.0")
public class HellBiome extends Biome {

    public HellBiome() {
    }

    @Override
    public String getName() {
        return "Hell";
    }

    @Override
    public boolean canRain() {
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDry() {
        return true;
    }
}
