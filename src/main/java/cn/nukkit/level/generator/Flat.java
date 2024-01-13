package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class Flat extends Generator {


    public Flat(Map<String, Object> options) {
        super(options);
        addStage(GenerateStages.FLAT_GENERATE);
        if (Server.getInstance().getConfig("chunk-ticking.light-updates", false)) {
            addStage(GenerateStages.LIGHT_POPULATION);
        }
        addStage(GenerateStages.FINISHED);
    }

    @Override
    public String getName() {
        return "flat";
    }

    @Override
    public DimensionData getDimensionData() {
        return DimensionEnum.OVERWORLD.getDimensionData();
    }
}
