package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.trialchambers.TrialChambersStructure;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.placement.StructurePlacement;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class TrialChambersPopulator extends Populator {

    public static final String NAME = "normal_trial_chambers";

    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(94251327L)
            .minDistance(12)
            .maxDistance(34)
            .build());

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
        int biome = chunk.getBiomeId(7, chunk.getHeightMap(7, 7), 7);
        RandomSourceProvider placementRandom = RandomSourceProvider.create(level.getSeed());
        if (!PLACEMENT.canGenerate(level.getSeed(), placementRandom, chunkX, chunkZ, biome)) {
            return;
        }

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int originY = findGenerationY(level, originX + 7, originZ + 7);
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
