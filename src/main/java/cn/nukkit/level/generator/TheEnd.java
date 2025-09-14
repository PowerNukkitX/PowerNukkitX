package cn.nukkit.level.generator;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.generator.stages.BiomeMapStage;
import cn.nukkit.level.generator.stages.ChunkPlacementQueueStage;
import cn.nukkit.level.generator.stages.end.TheEndPopulatorStage;
import cn.nukkit.level.generator.stages.end.TheEndTerrainStage;
import cn.nukkit.registry.Registries;

import java.util.Map;

public class TheEnd extends Generator {

    public TheEnd(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(BiomeMapStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(TheEndTerrainStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(TheEndPopulatorStage.NAME));
        builder.next(Registries.GENERATE_STAGE.get(ChunkPlacementQueueStage.NAME));
    }

    @Override
    public String getName() {
        return "the_end";
    }
}
