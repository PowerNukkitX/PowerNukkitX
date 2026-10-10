package org.powernukkitx.level.generator.feature;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.PlacementPredicate;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Executes a fixed number of chunk-local surface feature candidates.
 *
 * @author Curse
 */
public abstract class SurfaceCandidateFeature extends GenerateFeature {

    /**
     * Returns the number of surface candidate origins.
     */
    protected abstract int iterations();

    /**
     * Returns the origin-level feature graph.
     */
    protected abstract PlacementFeature placementFeature();

    /**
     * Returns the candidate biome/rule predicate.
     */
    protected abstract PlacementPredicate candidatePredicate();

    /**
     * Tests the rule-level scatter chance before candidate iteration begins.
     */
    protected boolean shouldApply(RandomSourceProvider random) {
        return true;
    }

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = context.getLevel();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int originX = chunkX << 4;
        int originZ = chunkZ << 4;

        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());
        if (!this.shouldApply(this.random)) {
            return;
        }

        FeaturePlacementContext placement = new FeaturePlacementContext(
                context,
                this.random,
                this.root,
                originX,
                level.getMinHeight(),
                originZ
        );

        PlacementFeature feature = this.placementFeature();
        PlacementPredicate predicate = this.candidatePredicate();

        for (int i = 0; i < this.iterations(); i++) {
            int localX = this.random.nextExclusiveInt(16);
            int localZ = this.random.nextExclusiveInt(16);
            int y = chunk.getHeightMap(localX, localZ);

            placement.setPosition(originX + localX, y, originZ + localZ);
            if (predicate.test(placement)) {
                feature.place(placement);
            }
        }
    }
}
