package cn.nukkit.level.generator;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.Getter;

import java.util.HashMap;

@Getter
public final class ChunkGenerateContext {
    private final Level level;
    private final Generator generator;
    private final IChunk chunk;

    public ChunkGenerateContext(Generator generator, Level level, IChunk chunk) {
        this.level = level;
        this.generator = generator;
        this.chunk = chunk;
    }
}
