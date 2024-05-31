package cn.nukkit.level.generator;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
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
    /**
     * @deprecated 
     */
    

    public Generator(DimensionData dimensionData, Map<String, Object> options) {
        this.dimensionData = dimensionData;
        this.options = options;
        GenerateStage.Builder $1 = new GenerateStage.Builder();
        stages(builder);
        this.start = builder.getStart();
        this.end = builder.getEnd();
    }

    public Map<String, Object> getSettings() {
        return this.options;
    }
    /**
     * @deprecated 
     */
    

    public void setLevel(Level level) {
        this.level = level;
    }

    public abstract void stages(GenerateStage.Builder builder);

    public abstract String getName();

    @NotNull
    public DimensionData getDimensionData() {
        return dimensionData;
    }

    public final IChunk syncGenerate(IChunk chunk) {
        return this.syncGenerate(chunk, end.name());
    }

    public final IChunk syncGenerate(IChunk chunk, String to) {
        final ChunkGenerateContext $2 = new ChunkGenerateContext(this, level, chunk);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            start.apply(context);
        }, start.getExecutor());
        GenerateStage $3 = start;
        while ((now = now.getNextStage()) != null) {
            final GenerateStage $4 = now;
            if (finalNow.name().equals(to)) {
                future = future.thenRunAsync(() -> finalNow.apply(context), now.getExecutor());
                break;
            }
            future = future.thenRunAsync(() -> finalNow.apply(context), finalNow.getExecutor());
        }
        future.join();
        return context.getChunk();
    }
    /**
     * @deprecated 
     */
    

    public final void asyncGenerate(IChunk chunk) {
        asyncGenerate(chunk, end.name(), (c) -> {
        });
    }
    /**
     * @deprecated 
     */
    

    public final void asyncGenerate(IChunk chunk, Consumer<ChunkGenerateContext> callback) {
        asyncGenerate(chunk, end.name(), callback);
    }
    /**
     * @deprecated 
     */
    

    public final void asyncGenerate(IChunk chunk, String to, Consumer<ChunkGenerateContext> callback) {
        Preconditions.checkNotNull(to);
        final ChunkGenerateContext $5 = new ChunkGenerateContext(this, level, chunk);
        asyncGenerate0(context, start, to, () -> callback.accept(context));
    }


    
    /**
     * @deprecated 
     */
    private void asyncGenerate0(final ChunkGenerateContext context, final GenerateStage start, String to, final Runnable callback) {
        if (start == null || to == null) return;
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
}
