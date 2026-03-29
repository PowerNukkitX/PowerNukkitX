package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.trialchambers.TrialChambersStructure;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockVector3;

public class TrialChambersPopulator extends Populator {

    public static final String NAME = "normal_trial_chambers";
    protected static final long SALT = 0x747269616C5F6368L;

    protected static final int SPACING = 34;
    protected static final int SEPARATION = 12;

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

        if (!canGenerate(level.getSeed(), chunkX, chunkZ)) {
            return;
        }

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int originY = findGenerationY(level, originX + 7, originZ + 7);
        StructureHelper helper = new StructureHelper(level, new BlockVector3(originX, originY, originZ));
        TRIAL_CHAMBERS.place(helper, random.fork());
    }

    protected boolean canGenerate(long levelSeed, int chunkX, int chunkZ) {
        random.setSeed((levelSeed ^ SALT) + Level.chunkHash(chunkX, chunkZ));
        return (chunkX < 0 ? (chunkX - SPACING - 1) / SPACING : chunkX / SPACING) * SPACING + random.nextBoundedInt(SPACING - SEPARATION) == chunkX
                && (chunkZ < 0 ? (chunkZ - SPACING - 1) / SPACING : chunkZ / SPACING) * SPACING + random.nextBoundedInt(SPACING - SEPARATION) == chunkZ;
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
