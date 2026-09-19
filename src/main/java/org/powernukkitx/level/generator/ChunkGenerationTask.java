package org.powernukkitx.level.generator;

/**
 * Describes one chunk generation stage, including state transitions and dependency requirements.
 *
 * @param name value for this API
 * @param stableState value for this API
 * @param activeState value for this API
 * @param successState value for this API
 * @param failureState value for this API
 * @param firstStage value for this API
 * @param lastStage value for this API
 * @param dependency value for this API
 *
 * @author Curse
 */
public record ChunkGenerationTask(
        String name,
        ChunkGenerationState stableState,
        ChunkGenerationState activeState,
        ChunkGenerationState successState,
        ChunkGenerationState failureState,
        String firstStage,
        String lastStage,
        ChunkGenerationDependency dependency
) {
}
