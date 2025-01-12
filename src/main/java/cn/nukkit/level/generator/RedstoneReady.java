package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.generator.stages.FinishedStage;
import cn.nukkit.level.generator.stages.LightPopulationStage;
import cn.nukkit.level.generator.stages.RedstoneReadyGenerateStage;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;

import java.util.Map;

/**
 * @author KeksDev
 */
public class RedstoneReady extends Generator {
    public RedstoneReady(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder.start(Registries.GENERATE_STAGE.get(RedstoneReadyGenerateStage.NAME));
        if (Server.getInstance().getSettings().chunkSettings().lightUpdates()) {
            builder.next(Registries.GENERATE_STAGE.get(LightPopulationStage.NAME));
        }
        builder.next(Registries.GENERATE_STAGE.get(FinishedStage.NAME));
    }

    @Override
    public String getName() {
        return "redstoneready";
    }
}
