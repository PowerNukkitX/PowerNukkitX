package org.powernukkitx.level.generator.populator.nether;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.object.structures.StructureHelper;
import org.powernukkitx.level.generator.object.structures.jigsaw.bastion.BastionStructure;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.ChunkVector2;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

public class BastionRemnantPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "nether_bastion_remnant";
    protected static final int MAX_DISTANCE = NetherComplexPlacement.REGION_SIZE_CHUNKS;
    protected static final int MIN_DISTANCE = NetherComplexPlacement.EDGE_EXCLUSION_CHUNKS;
    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(NetherComplexPlacement.PLACEMENT_SALT)
            .minDistance(MIN_DISTANCE)
            .maxDistance(MAX_DISTANCE)
            .build()) {
        @Override
        public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, int biome) {
            return biome != BiomeID.BASALT_DELTAS
                    && NetherComplexPlacement.isNetherComplexStart(levelSeed, chunkX, chunkZ)
                    && NetherComplexPlacement.shouldGenerateBastion(levelSeed, chunkX, chunkZ);
        }

        @Override
        public ChunkVector2 findNearestGenerationChunk(ChunkVector2 origin, RandomSourceProvider random, BiomePicker<?> biomePicker, int radius) {
            return NetherComplexPlacement.findNearestGenerationChunk(origin, random, biomePicker, radius, true);
        }
    };

    private static final BastionStructure BASTION = new BastionStructure();

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        if (!chunk.isNether()) {
            return;
        }

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        long seed = level.getSeed();
        int biome = chunk.getBiomeId(7, 33, 7);

        if (!PLACEMENT.canGenerate(seed, random, chunkX, chunkZ, biome)) {
            return;
        }

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int originY = 33;
        StructureHelper helper = new StructureHelper(level, new BlockVector3(originX, originY, originZ));
        var pieceBounds = BASTION.placeWithBounds(helper, NetherComplexPlacement.createPostSelectionRandom(seed, chunkX, chunkZ));
        StructureAabbVolumes.addDynamic(level, "minecraft:bastion_remnant", pieceBounds);
    }

    @Override
    public String name() {
        return NAME;
    }
}
