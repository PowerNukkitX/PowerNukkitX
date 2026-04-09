package cn.nukkit.level.generator;

import cn.nukkit.level.Dimension;
import cn.nukkit.level.format.IChunk;
import lombok.Getter;

@Getter
public final class ChunkGenerateContext {
    private final Dimension level;
    private final Generator generator;
    private final IChunk chunk;

    public ChunkGenerateContext(Generator generator, Dimension level, IChunk chunk) {
        this.level = level;
        this.generator = generator;
        this.chunk = chunk;
    }
}
