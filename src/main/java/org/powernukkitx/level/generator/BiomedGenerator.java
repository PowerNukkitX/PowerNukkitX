package org.powernukkitx.level.generator;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.biome.BiomePicker;

public interface BiomedGenerator {

    BiomePicker createBiomePicker(Level level);

}
