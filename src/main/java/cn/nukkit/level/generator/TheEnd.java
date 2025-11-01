package cn.nukkit.level.generator;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.biome.TheEndBiomePicker;
import cn.nukkit.level.generator.biome.result.TheEndBiomeResult;
import cn.nukkit.level.generator.stages.BiomeMapStage;
import cn.nukkit.level.generator.stages.ChunkPlacementQueueStage;
import cn.nukkit.level.generator.stages.LightPopulationStage;
import cn.nukkit.level.generator.stages.end.TheEndPopulatorStage;
import cn.nukkit.level.generator.stages.end.TheEndTerrainStage;
import cn.nukkit.level.generator.stages.flat.FinishedStage;
import cn.nukkit.registry.Registries;

import java.util.Map;

public class TheEnd extends BiomedGenerator {

    public TheEnd(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(BiomeMapStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(TheEndTerrainStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(TheEndPopulatorStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(ChunkPlacementQueueStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public BiomePicker<TheEndBiomeResult> createBiomePicker(Level level) {
        return new TheEndBiomePicker();
    }

    @Override
    public String getName() {
        return "the_end";
    }
}
