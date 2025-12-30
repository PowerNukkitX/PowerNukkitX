package cn.nukkit.level.generator;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.biome.BiomePicker;

import java.util.Map;

public abstract class BiomedGenerator extends Generator {

    public BiomedGenerator(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    public abstract BiomePicker createBiomePicker(Level level);

}
