package org.powernukkitx.level.generator.feature;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.PlacementPredicate;

/**
 * Executes fixed-count chunk-local feature candidates across a 3D volume.
 *
 * @author Curse
 */
public abstract class VolumeCandidateFeature extends GenerateFeature {

    /**
     * Returns the number of candidate origins.
     */
    protected abstract int iterations();

    /**
     * Returns the minimum sampled Y.
     */
    protected abstract int minY();

    /**
     * Returns the exclusive maximum sampled Y.
     */
    protected abstract int maxYExclusive();

    /**
     * Returns the origin-level feature graph.
     */
    protected abstract PlacementFeature placementFeature();

    /**
     * Returns the candidate biome/rule predicate.
     */
    protected abstract PlacementPredicate candidatePredicate();

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = context.getLevel();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int minY = this.minY();
        int yRange = this.maxYExclusive() - minY;

        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());

        FeaturePlacementContext placement = new FeaturePlacementContext(
                context,
                this.random,
                this.root,
                originX,
                minY,
                originZ
        );

        PlacementFeature feature = this.placementFeature();
        PlacementPredicate predicate = this.candidatePredicate();

        for (int i = 0; i < this.iterations(); i++) {
            int localX = this.random.nextExclusiveInt(16);
            int localZ = this.random.nextExclusiveInt(16);
            int y = minY + this.random.nextExclusiveInt(yRange);

            placement.setPosition(originX + localX, y, originZ + localZ);
            if (predicate.test(placement)) {
                feature.place(placement);
            }
        }
    }
}
