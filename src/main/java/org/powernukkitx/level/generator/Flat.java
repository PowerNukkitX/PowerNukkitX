package org.powernukkitx.level.generator;

import org.powernukkitx.Server;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.generator.stages.FinishedStage;
import org.powernukkitx.level.generator.stages.flat.FlatGenerateStage;
import org.powernukkitx.level.generator.stages.LightPopulationStage;
import org.powernukkitx.registry.Registries;

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
