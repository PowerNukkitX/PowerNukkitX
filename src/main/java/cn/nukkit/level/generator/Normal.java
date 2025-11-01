package cn.nukkit.level.generator;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.biome.OverworldBiomePicker;
import cn.nukkit.level.generator.biome.result.OverworldBiomeResult;
import cn.nukkit.level.generator.stages.LightPopulationStage;
import cn.nukkit.level.generator.stages.NormalChunkFeatureStage;
import cn.nukkit.level.generator.stages.flat.FinishedStage;
import cn.nukkit.level.generator.stages.BiomeMapStage;
import cn.nukkit.level.generator.stages.ChunkPlacementQueueStage;
import cn.nukkit.level.generator.stages.normal.NormalPopulatorStage;
import cn.nukkit.level.generator.stages.normal.NormalSurfaceDataStage;
import cn.nukkit.level.generator.stages.normal.NormalSurfaceOverwriteStage;
import cn.nukkit.level.generator.stages.normal.NormalTerrainStage;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.Map;

/**
 * @author Buddelbubi
 */
public class Normal extends BiomedGenerator {

    public Normal(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(BiomeMapStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NormalTerrainStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NormalSurfaceDataStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NormalSurfaceOverwriteStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(ChunkPlacementQueueStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NormalPopulatorStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(NormalChunkFeatureStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public BiomePicker<OverworldBiomeResult> createBiomePicker(Level level) {
        return new OverworldBiomePicker(new NukkitRandom(level.getSeed()));
    }

    @Override
    public String getName() {
        return "normal";
    }

}
