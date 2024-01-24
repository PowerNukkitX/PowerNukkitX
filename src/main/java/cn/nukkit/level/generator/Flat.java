package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.level.DimensionData;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class Flat extends Generator {
    public Flat(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
        builder = builder.start(GenerateStages.FLAT_GENERATE);
        if (Server.getInstance().getConfig("chunk-ticking.light-updates", false)) {
            builder.next(GenerateStages.LIGHT_POPULATION);
        }
        builder.next(GenerateStages.FINISHED);
    }

    @Override
    public String getName() {
        return "flat";
    }
}
