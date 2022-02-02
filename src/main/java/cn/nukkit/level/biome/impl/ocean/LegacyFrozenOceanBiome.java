package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

/**
 * @author DaPorkchop_ (Nukkit Project)
 * <p>
 * This biome does not generate naturally
 */
@PowerNukkitDifference(info = "Old FrozenOceanBiome renamed to LegacyFrozenOceanBiome", since = "1.4.0.0-PN")
public class LegacyFrozenOceanBiome extends OceanBiome {

    public LegacyFrozenOceanBiome() {
        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);
        
        this.setBaseHeight(-1f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Legacy Frozen Ocean";
    }

    @Override
    public boolean isFreezing() {
        return true;
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
