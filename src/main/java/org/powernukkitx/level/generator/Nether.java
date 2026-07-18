package org.powernukkitx.level.generator;

import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.NetherBiomePicker;
import org.powernukkitx.level.generator.biome.result.NetherBiomeResult;
import org.powernukkitx.level.generator.holder.ObjectHolder;
import org.powernukkitx.level.generator.holder.NetherObjectHolder;
import org.powernukkitx.level.generator.stages.BiomeMapStage;
import org.powernukkitx.level.generator.stages.GeneratedStage;
import org.powernukkitx.level.generator.stages.LightPopulationStage;
import org.powernukkitx.level.generator.stages.FinishedStage;
import org.powernukkitx.level.generator.stages.nether.NetherPopulatorStage;
import org.powernukkitx.level.generator.stages.nether.NetherTerrainStage;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.NukkitRandom;

import java.util.Map;

public class Nether extends PopulatedGenerator implements BiomedGenerator {

    public Nether(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(BiomeMapStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NetherTerrainStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(GeneratedStage.NAME));

        builder.next(Registries.GENERATE_STAGE.get(NetherPopulatorStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public BiomePicker<NetherBiomeResult> createBiomePicker(Level level) {
        return new NetherBiomePicker(new NukkitRandom(level.getSeed()));
    }

    @Override
    public ObjectHolder createObjectHolder(Level level) {
        return new NetherObjectHolder(new NukkitRandom(level.getSeed()));
    }

    @Override
    public String getName() {
        return "nether";
    }
}
