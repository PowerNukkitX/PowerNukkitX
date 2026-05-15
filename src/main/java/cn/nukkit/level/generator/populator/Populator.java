package cn.nukkit.level.generator.populator;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.Xoroshiro128;
import lombok.Setter;

import java.util.HashMap;

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
