package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.generator.stages.FinishedStage;
import cn.nukkit.level.generator.stages.LightPopulationStage;
import cn.nukkit.level.generator.stages.VoidGenerateStage;
import cn.nukkit.registry.Registries;

import java.util.Map;

/**
 * @author KeksDev
 */
public class Void extends Generator {
    public Void(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(VoidGenerateStage.NAME));
        if (Server.getInstance().getSettings().chunkSettings().lightUpdates()) {
            builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        }
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public String getName() {
        return "void";
    }
}
