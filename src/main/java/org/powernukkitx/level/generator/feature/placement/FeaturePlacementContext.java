package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Mutable execution context for one origin-level feature graph.
 *
 * @author Curse
 */
public final class FeaturePlacementContext {

    private final ChunkGenerateContext generationContext;
    private final RandomSourceProvider random;
    private final BlockManager root;

    private int x;
    private int y;
    private int z;

    /**
     * Creates a placement context at the supplied input position.
     */
    public FeaturePlacementContext(
            ChunkGenerateContext generationContext,
            RandomSourceProvider random,
            BlockManager root,
            int x,
            int y,
            int z
    ) {
        this.generationContext = Preconditions.checkNotNull(generationContext, "generationContext");
        this.random = Preconditions.checkNotNull(random, "random");
        this.root = Preconditions.checkNotNull(root, "root");
        this.setPosition(x, y, z);
    }

    /**
     * Returns the owning chunk-generation context.
     */
    public ChunkGenerateContext getGenerationContext() {
        return this.generationContext;
    }

    /**
     * Returns the random source shared by this feature graph.
     */
    public RandomSourceProvider getRandom() {
        return this.random;
    }

    /**
     * Returns the shared block writer for this feature graph.
     */
    public BlockManager getRoot() {
        return this.root;
    }

    /**
     * Returns the current X position.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the current Y position.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Returns the current Z position.
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Replaces the current input/output position.
     */
    public FeaturePlacementContext setPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
}
