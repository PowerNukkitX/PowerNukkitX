package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.ObjectDesertWell;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.math.Vector3;

public class DesertWellPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "normal_desert_well";

    protected static final ObjectDesertWell WELL = new ObjectDesertWell();

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

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
