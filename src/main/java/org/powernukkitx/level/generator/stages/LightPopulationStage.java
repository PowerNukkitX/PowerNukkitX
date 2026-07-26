package org.powernukkitx.level.generator.stages;

import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;

public class LightPopulationStage extends GenerateStage {
    public static final String NAME = "light_population";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        final IChunk chunk = context.getChunk();
        if (chunk == null) {
            return;
        }
        chunk.recalculateHeightMap();
        chunk.populateSkyLight();
        chunk.setLightPopulated();
    }
}
