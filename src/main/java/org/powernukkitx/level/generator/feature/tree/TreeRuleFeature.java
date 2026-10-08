package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;

/**
 * Executes a compiled tree feature plan for one chunk-level biome rule.
 *
 * @author Curse
 */
public abstract class TreeRuleFeature extends GenerateFeature {

    /**
     * Returns the compiled tree execution plan.
     */
    protected abstract TreeFeaturePlan treePlan();

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = context.getLevel();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int originX = chunkX << 4;
        int originZ = chunkZ << 4;

        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());

        TreeFeaturePlan plan = this.treePlan();
        int count = plan.countPolicy().sample(this.random);
        if (count <= 0) {
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

        for (int i = 0; i < count; i++) {
            PlacementFeature selectedFeature = plan.selector().select(this.random);

            if (!plan.candidatePolicy().select(placement, originX, originZ)) {
                continue;
            }

            int candidateX = placement.getX();
            int candidateY = placement.getY();
            int candidateZ = placement.getZ();

            if (!plan.candidatePredicate().test(placement)) {
                continue;
            }

            selectedFeature.place(placement);

            PlacementFeature postAttempt = plan.postAttempt();
            if (postAttempt != null) {
                placement.setPosition(candidateX, candidateY, candidateZ);
                postAttempt.place(placement);
            }
        }
    }
}
