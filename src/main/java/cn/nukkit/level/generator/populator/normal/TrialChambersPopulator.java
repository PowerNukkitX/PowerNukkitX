package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.trialchambers.TrialChambersStructure;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockVector3;

public class TrialChambersPopulator extends Populator {

    public static final String NAME = "normal_trial_chambers";

    protected static final TrialChambersStructure TRIAL_CHAMBERS = new TrialChambersStructure();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if (!chunk.isOverWorld()) {
            return;
        }

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int originY = findGenerationY(level, originX + 7, originZ + 7);
        if (!TRIAL_CHAMBERS.canGenerateAt(new Location(originX, originY, originZ, level))) {
            return;
        }
        StructureHelper helper = new StructureHelper(level, new BlockVector3(originX, originY, originZ));
        TRIAL_CHAMBERS.place(helper, random.fork());
    }


    protected int findGenerationY(Level level, int x, int z) {
        int minY = level.getMinHeight() + 8;
        int terrainY = level.getHeightMap(x, z) - 20;
        return Math.max(minY, Math.min(-20, terrainY));
    }

    @Override
    public String name() {
        return NAME;
    }
}
