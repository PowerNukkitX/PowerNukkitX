package org.powernukkitx.level.generator;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.holder.EmptyObjectHolder;
import org.powernukkitx.level.generator.holder.ObjectHolder;
import org.powernukkitx.level.generator.object.structures.utils.StructureBoundsCache;
import org.powernukkitx.level.generator.object.structures.utils.StructureStartCache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public abstract class Generator implements BlockID {
    protected final Map<String, Object> options;
    protected final GenerateStage start;
    protected final GenerateStage end;
    protected final DimensionData dimensionData;
    protected final StructureStartCache structureStartCache = new StructureStartCache();
    protected final StructureBoundsCache structureBoundsCache = new StructureBoundsCache();
    protected Level level;

    public Generator(DimensionData dimensionData, Map<String, Object> options) {
        this.dimensionData = dimensionData;
        this.options = options;
        GenerateStage.Builder builder = new GenerateStage.Builder();
        stages(builder);
        this.start = builder.getStart();
        this.end = builder.getEnd();
    }

    public Map<String, Object> getSettings() {
        return this.options;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public abstract void stages(GenerateStage.Builder builder);

    public abstract String getName();

    @NotNull
    public DimensionData getDimensionData() {
        return dimensionData;
    }

    /**
     * Returns the runtime structure-start cache for this generator.
     */
    public StructureStartCache getStructureStartCache() {
        return this.structureStartCache;
    }

    /**
     * Returns the runtime structure-bounds cache for this generator.
     */
    public StructureBoundsCache getStructureBoundsCache() {
        return this.structureBoundsCache;
    }

    /**
     * Returns the ordered generation tasks provided by this generator.
     *
     * @return generation tasks
     */
    public List<ChunkGenerationTask> getGenerationTasks() {
        return List.of(
                new ChunkGenerationTask(
                        "Chunk Gen",
                        ChunkGenerationState.NEEDS_GENERATION,
                        ChunkGenerationState.GENERATING,
                        ChunkGenerationState.NEEDS_LIGHTING,
                        ChunkGenerationState.NEEDS_GENERATION,
                        start.name(),
                        end.name(),
                        ChunkGenerationDependency.NONE
                )
        );
    }

    /**
     * Returns the generation task associated with a stable state.
     *
     * @param state stable generation state
     * @return matching task, or {@code null} when none exists
     */
    public ChunkGenerationTask getGenerationTask(ChunkGenerationState state) {
        for (ChunkGenerationTask task : getGenerationTasks()) {
            if (task.stableState() == state) return task;
        }

        return null;
    }

    /**
     * Executes a generation task for a chunk.
     *
     * @param chunk target chunk
     * @param task generation task
     */
    public final void runGenerationTask(IChunk chunk, ChunkGenerationTask task) {
        Preconditions.checkNotNull(chunk);

        Preconditions.checkNotNull(task);

        final ChunkGenerateContext context = new ChunkGenerateContext(this, level, chunk);

        GenerateStage stage = findStage(task.firstStage());

        if (stage == null) {
            throw new IllegalStateException("Generation stage '" + task.firstStage() + "' does not exist in generator '" + getName() + "'");
        }

        while (stage != null) {
            stage.apply(context);
            if (stage.name().equals(task.lastStage())) return;

            stage = stage.getNextStage();
        }

        throw new IllegalStateException("Generation task '" + task.name() + "' reached the end of generator '" + getName() + "' before stage '" + task.lastStage() + "'");
    }

    private GenerateStage findStage(String name) {
        GenerateStage stage = start;

        while (stage != null) {
            if (stage.name().equals(name)) return stage;
            stage = stage.getNextStage();
        }

        return null;
    }

    public IChunk syncGenerate(IChunk chunk) {
        return this.syncGenerate(chunk, getEndName(chunk));
    }

    protected IChunk syncGenerate(IChunk chunk, String to) {
        Preconditions.checkNotNull(to);
        final ChunkGenerateContext context = new ChunkGenerateContext(this, level, chunk);
        final GenerateStage start = getStart(context);
        if (start == null) return context.getChunk();

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            start.apply(context);
        }, start.getExecutor());
        GenerateStage now = start;
        while ((now = now.getNextStage()) != null) {
            final GenerateStage finalNow = now;
            if (finalNow.name().equals(to)) {
                future = future.thenRunAsync(() -> finalNow.apply(context), now.getExecutor());
                break;
            }
            future = future.thenRunAsync(() -> finalNow.apply(context), finalNow.getExecutor());
        }
        future.join();
        return context.getChunk();
    }

    protected String getEndName(IChunk chunk) {
        return end.name();
    }

    protected GenerateStage getStart(ChunkGenerateContext context) {
        return start;
    }

    public final void asyncGenerate(IChunk chunk) {
        asyncGenerate(chunk, getEndName(chunk), (c) -> {
        });
    }

    public final void asyncGenerate(IChunk chunk, Consumer<ChunkGenerateContext> callback) {
        asyncGenerate(chunk, getEndName(chunk), callback);
    }

    public final void asyncGenerate(IChunk chunk, String to, Consumer<ChunkGenerateContext> callback) {
        Preconditions.checkNotNull(to);
        final ChunkGenerateContext context = new ChunkGenerateContext(this, level, chunk);
        final GenerateStage start = getStart(context);
        asyncGenerate0(context, start, to, () -> callback.accept(context));
    }


    protected final void asyncGenerate0(final ChunkGenerateContext context, final GenerateStage start, String to, final Runnable callback) {
        if (start == null || to == null) {
            callback.run();
            return;
        }
        if (to.equals(start.name())) {
            start.getExecutor().execute(() -> {
                start.apply(context);
                callback.run();
            });
            return;
        }
        start.getExecutor().execute(() -> {
            start.apply(context);
            asyncGenerate0(context, start.getNextStage(), to, callback);
        });

    }

    public ObjectHolder createObjectHolder(Level level) {
        return new EmptyObjectHolder();
    }
}
