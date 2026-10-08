package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.structures.StructureHelper;
import org.powernukkitx.level.generator.object.structures.jigsaw.trialchambers.TrialChambersStructure;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructureRandomSpreadPlacement;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;

import java.util.ArrayList;
import java.util.List;

public class TrialChambersPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "normal_trial_chambers";

    public static final StructurePlacement PLACEMENT = new StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings.builder()
            .salt(94251327L)
            .minDistance(12)
            .maxDistance(34)
            .build(), StructureRandomSpreadPlacement.SpreadType.LINEAR);

    protected static final TrialChambersStructure TRIAL_CHAMBERS = new TrialChambersStructure();

    private static final String STRUCTURE_TYPE = "minecraft:trial_chambers";
    private static final int START_SEARCH_RADIUS = 8;
    private static final int MIN_START_Y = -40;
    private static final int MAX_START_Y = -20;

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        if (!chunk.isOverWorld()) {
            return;
        }

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        long seed = level.getSeed();
        List<StructureAabbVolumes.DynamicStructure> aabbStructures = new ArrayList<>();

        for (int startChunkX = chunkX - START_SEARCH_RADIUS; startChunkX <= chunkX + START_SEARCH_RADIUS; startChunkX++) {
            for (int startChunkZ = chunkZ - START_SEARCH_RADIUS; startChunkZ <= chunkZ + START_SEARCH_RADIUS; startChunkZ++) {
                if (!PLACEMENT.canGenerate(seed, random, startChunkX, startChunkZ, 0)) {
                    continue;
                }

                final int originChunkX = startChunkX;
                final int originChunkZ = startChunkZ;
                List<BoundingBox> pieceBounds = context.getGenerator().getStructureBoundsCache().getOrCreate(
                        TrialChambersStructure.class,
                        originChunkX,
                        originChunkZ,
                        () -> createPieceBounds(level, originChunkX, originChunkZ)
                );
                if (pieceBounds.size() > 0) {
                    aabbStructures.add(StructureAabbVolumes.DynamicStructure.fromPieces(pieceBounds));
                }

                if (originChunkX == chunkX && originChunkZ == chunkZ) {
                    placeStructure(level, originChunkX, originChunkZ);
                }
            }
        }

        StructureAabbVolumes.replaceDynamic(chunk, STRUCTURE_TYPE, aabbStructures);
    }

    private static List<BoundingBox> createPieceBounds(Level level, int chunkX, int chunkZ) {
        Xoroshiro128 random = createStructureRandom(level.getSeed(), chunkX, chunkZ);
        int originY = getGenerationY(random);
        StructureHelper helper = new StructureHelper(level, new BlockVector3(chunkX << 4, originY, chunkZ << 4));
        return TRIAL_CHAMBERS.collectPieceBounds(helper, random);
    }

    private static void placeStructure(Level level, int chunkX, int chunkZ) {
        Xoroshiro128 random = createStructureRandom(level.getSeed(), chunkX, chunkZ);
        int originY = getGenerationY(random);
        StructureHelper helper = new StructureHelper(level, new BlockVector3(chunkX << 4, originY, chunkZ << 4));
        TRIAL_CHAMBERS.place(helper, random);
    }

    private static Xoroshiro128 createStructureRandom(long seed, int chunkX, int chunkZ) {
        Xoroshiro128 random = new Xoroshiro128(seed);
        long xMultiplier = random.nextLong() | 1L;
        long zMultiplier = random.nextLong() | 1L;
        return random.setSeed(((long) chunkX * xMultiplier + (long) chunkZ * zMultiplier) ^ seed);
    }

    private static int getGenerationY(RandomSourceProvider random) {
        return MIN_START_Y + random.nextBoundedInt(MAX_START_Y - MIN_START_Y);
    }

    @Override
    public String name() {
        return NAME;
    }
}
