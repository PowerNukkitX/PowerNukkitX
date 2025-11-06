package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.ObjectJungleTemple;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.Vector3;

public class JungleTemplePopulator extends Populator {

    public static final String NAME = "normal_jungle_temple";

    protected static final ObjectJungleTemple JUNGLE_TEMPLE = new ObjectJungleTemple();

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

        if(JUNGLE_TEMPLE.canGenerateAt(new Location(x, y, z, level))) {
            BlockManager manager = new BlockManager(level);
            JUNGLE_TEMPLE.generate(manager, random, new Vector3(x, y, z));
            manager.generateChunks();
            queueObject(chunk, manager);
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
