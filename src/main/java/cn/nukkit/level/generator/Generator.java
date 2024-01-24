package cn.nukkit.level.generator;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.registry.Registries;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public abstract class Generator implements BlockID {
    protected final Map<String, Object> options;
    protected final GenerateStage start;
    protected final GenerateStage end;
    protected final DimensionData dimensionData;
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
    public final DimensionData getDimensionData() {
        return dimensionData;
    }

    public final IChunk syncGenerate(IChunk chunk) {
        return this.syncGenerate(chunk, end);
    }

    public final IChunk syncGenerate(IChunk chunk, GenerateStage to) {
        final ChunkGenerateContext context = new ChunkGenerateContext(this, level, chunk);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            start.apply(context);
        }, start.getExecutor());
        GenerateStage now;
        while ((now = start.getNextStage()) != null) {
            if (now == to || now.name().equals(to.name())) {
                future = future.thenRunAsync(() -> start.getNextStage().apply(context), start.getExecutor());
                break;
            }
            future = future.thenRunAsync(() -> start.getNextStage().apply(context), start.getExecutor());
        }
        future.join();
        IChunk c = context.getChunk();
        level.setChunk(c.getX(), c.getZ(), c);
        return c;
    }

    public final void asyncGenerate(IChunk chunk) {
        asyncGenerate(chunk, end, (c) -> {
        });
    }

    public final void asyncGenerate(IChunk chunk, Consumer<ChunkGenerateContext> callback) {
        asyncGenerate(chunk, end, callback);
    }

    public final void asyncGenerate(IChunk chunk, GenerateStage to, Consumer<ChunkGenerateContext> callback) {
        final ChunkGenerateContext context = new ChunkGenerateContext(this, level, chunk);
        asyncGenerate0(context, start, to, () -> {
            IChunk c = context.getChunk();
            level.setChunk(c.getX(), c.getZ(), c);
            callback.accept(context);
        });
    }

    public final void asyncGenerate(IChunk chunk, String to, Consumer<ChunkGenerateContext> callback) {
        GenerateStage generateStage = Registries.GENERATE_STAGE.get(to);
        Preconditions.checkNotNull(generateStage);
        final ChunkGenerateContext context = new ChunkGenerateContext(this, level, chunk);
        asyncGenerate0(context, start, generateStage, () -> {
            IChunk c = context.getChunk();
            level.setChunk(c.getX(), c.getZ(), c);
            callback.accept(context);
        });
    }


    private void asyncGenerate0(final ChunkGenerateContext context, final GenerateStage start, final GenerateStage to, final Runnable callback) {
        if (start == null || to == null) return;
        if (to == start || to.name().equals(start.name())) {
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
}
