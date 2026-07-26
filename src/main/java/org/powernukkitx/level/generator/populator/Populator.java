package org.powernukkitx.level.generator.populator;

import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.utils.random.Xoroshiro128;
import lombok.Setter;


public abstract class Populator {

    @Setter
    protected BlockManager root;

    protected final static ThreadLocal<Xoroshiro128> RANDOMS = ThreadLocal.withInitial(Xoroshiro128::new);

    protected final Xoroshiro128 random = RANDOMS.get();

    public abstract String name();

    public abstract void apply(ChunkGenerateContext context);

    protected void queueObject(IChunk chunk, BlockManager object) {
        root.merge(object);
    }
}
