package cn.nukkit.level.generator.populator.the_end;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectObsidianPillar;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;

public class ObsidianPillarPopulator extends Populator {

    public static final String NAME = "the_end_obsidian_pillar";

    private Vector2[] pillarPos;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        if(pillarPos == null) {
            pillarPos = new Vector2[10];
            for(int i = 0; i < 10; i++) {
                int x = (int) (42d * Math.cos(2d * (-Math.PI + (Math.PI / 10d) * i)));
                int z = (int) (42d * Math.sin(2d * (-Math.PI + (Math.PI / 10d) * i)));
                pillarPos[i] = new Vector2(x, z);
            }
        }
        BlockManager object = new BlockManager(level);
        for(int i = 0; i < pillarPos.length; i++) {
            Vector2 p = pillarPos[i];
            if(p.getFloorX() >> 4 == chunkX && p.getFloorY() >> 4 == chunkZ) {
                ObjectObsidianPillar pillar = new ObjectObsidianPillar();
                pillar.i = i;
                pillar.generate(object, null, new Vector3(p.x, level.getHeightMap(p.getFloorX(), p.getFloorY()), p.y));
            }
        }
        queueObject(chunk, object);
    }

    @Override
    public String name() {
        return NAME;
    }
}
