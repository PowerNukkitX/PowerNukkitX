package org.powernukkitx.level.generator.stages;

import org.powernukkitx.level.format.ChunkState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;

public class VoidGenerateStage extends GenerateStage {
    public static final String NAME = "void_generate";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int minHeight = context.getGenerator().getDimensionData().getMinHeight();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setHeightMap(x, z, minHeight);
            }
        }
        chunk.setChunkState(ChunkState.POPULATED);
    }
}
