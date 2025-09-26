package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.ObjectDesertWell;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.Vector3;

public class DesertWellPopulator extends Populator {

    public static final String NAME = "normal_desert_well";

    protected static final ObjectDesertWell WELL = new ObjectDesertWell();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int x = (chunkX << 4) + random.nextBoundedInt(15);
        int z = (chunkZ << 4) + random.nextBoundedInt(15);
        int y = level.getHeightMap(x, z);

        if(WELL.canGenerateAt(new Location(x, y, z, level))) {
            BlockManager manager = new BlockManager(level);
            WELL.generate(manager, null, new Vector3(x, y, z));
            queueObject(chunk, manager);
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
