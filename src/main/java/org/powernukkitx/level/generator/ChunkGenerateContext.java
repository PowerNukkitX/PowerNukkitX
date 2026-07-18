package org.powernukkitx.level.generator;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import lombok.Getter;

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
