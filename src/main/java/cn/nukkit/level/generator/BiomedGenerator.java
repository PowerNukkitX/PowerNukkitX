package cn.nukkit.level.generator;

import cn.nukkit.level.Level;
import cn.nukkit.level.generator.biome.BiomePicker;

public interface BiomedGenerator {

    BiomePicker createBiomePicker(Level level);

}
