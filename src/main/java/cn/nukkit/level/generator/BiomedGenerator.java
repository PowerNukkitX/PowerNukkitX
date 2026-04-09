package cn.nukkit.level.generator;

import cn.nukkit.level.Dimension;
import cn.nukkit.level.generator.biome.BiomePicker;

public interface BiomedGenerator {

    BiomePicker createBiomePicker(Dimension level);

}
