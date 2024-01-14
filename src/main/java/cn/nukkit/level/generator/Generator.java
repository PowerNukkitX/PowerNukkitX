package cn.nukkit.level.generator;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.LockSupport;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public abstract class Generator implements BlockID {
    protected final Map<String, Object> options;
    protected Level level;
    private final Map<GenerateStage, GenerateStage> stages;
    private GenerateStage start;
    private GenerateStage end;

    public Generator(Map<String, Object> options) {
        this.options = options;
        this.stages = new LinkedHashMap<>();
    }

    protected void addStage(GenerateStage generateStage) {
        if (start == null) {
            start = generateStage;
            end = generateStage;
            return;
        }
        stages.put(end, generateStage);
        end = generateStage;
    }

    public Map<String, Object> getSettings() {
        return this.options;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public abstract String getName();

    @NotNull
    public abstract DimensionData getDimensionData();

    public Future<Void> generate(IChunk chunk) {
        return generate(chunk, end);
    }

    public Future<Void> generate(IChunk chunk, GenerateStage to) {
        final ChunkGenerateContext context = new ChunkGenerateContext(this, level, chunk);
        FutureTask<Void> future = new FutureTask<>(() -> {
            final Thread thread = Thread.currentThread();
            generate0(context, start, to, () -> {
                IChunk c = context.getChunk();
                level.setChunk(c.getX(), c.getZ(), c);
                LockSupport.unpark(thread);
            });
            LockSupport.park();
        }, null);
        new Thread(future).start();
        return future;
    }

    private void generate0(final ChunkGenerateContext context, final GenerateStage generationStage, final GenerateStage to, final Runnable callback) {
        if (to == generationStage) {
            generationStage.getExecutor().execute(() -> {
                generationStage.apply(context);
                callback.run();
            });
            return;
        }
        generationStage.getExecutor().execute(() -> {
            generationStage.apply(context);
            generate0(context, stages.get(generationStage), to, callback);
        });
    }
}
