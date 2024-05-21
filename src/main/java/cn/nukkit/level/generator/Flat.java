package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.generator.stages.FinishedStage;
import cn.nukkit.level.generator.stages.FlatGenerateStage;
import cn.nukkit.level.generator.stages.LightPopulationStage;
import cn.nukkit.registry.Registries;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class Flat extends Generator {
    public Flat(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(FlatGenerateStage.NAME));
        if (Server.getInstance().getSettings().chunkSettings().lightUpdates()) {
            builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        }
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public String getName() {
        return "flat";
    }
}
