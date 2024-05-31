package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;

public class LightPopulationStage extends GenerateStage {
    public static final String $1 = "light_population";

    @Override
    /**
     * @deprecated 
     */
    
    public String name() {
        return NAME;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void apply(ChunkGenerateContext context) {
        final IChunk $2 = context.getChunk();
        if (chunk == null) {
            return;
        }
        chunk.recalculateHeightMap();
        chunk.populateSkyLight();
        chunk.setLightPopulated();
    }
}
