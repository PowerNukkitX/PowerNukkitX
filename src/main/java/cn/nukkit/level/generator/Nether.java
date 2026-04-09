package cn.nukkit.level.generator;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.biome.NetherBiomePicker;
import cn.nukkit.level.generator.biome.result.NetherBiomeResult;
import cn.nukkit.level.generator.holder.ObjectHolder;
import cn.nukkit.level.generator.holder.NetherObjectHolder;
import cn.nukkit.level.generator.stages.BiomeMapStage;
import cn.nukkit.level.generator.stages.GeneratedStage;
import cn.nukkit.level.generator.stages.LightPopulationStage;
import cn.nukkit.level.generator.stages.FinishedStage;
import cn.nukkit.level.generator.stages.nether.NetherPopulatorStage;
import cn.nukkit.level.generator.stages.nether.NetherTerrainStage;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.Xoroshiro128;

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
    public BiomePicker<NetherBiomeResult> createBiomePicker(Dimension level) {
        return new NetherBiomePicker(new NukkitRandom(level.getSeed()));
    }

    @Override
    public ObjectHolder createObjectHolder(Dimension level) {
        return new NetherObjectHolder(new Xoroshiro128(level.getSeed()));
    }

    @Override
    public String getName() {
        return "nether";
    }
}
