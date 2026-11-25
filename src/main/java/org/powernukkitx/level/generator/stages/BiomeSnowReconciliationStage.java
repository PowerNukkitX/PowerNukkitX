package org.powernukkitx.level.generator.stages;

import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;

/**
 * @author Budelbubi (PowerNukkitX)
 * @since 2026/11/02
 */
public class BiomeSnowReconciliationStage extends GenerateStage {
    public static final String NAME = "biome_snow_reconciliation";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if (chunk == null) return;
        if (chunk.getProvider() instanceof LevelDBProvider provider) {
            provider.reconcileSnow(chunk);
        }
    }
}
