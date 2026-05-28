package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.WoodlandMansionPieces;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.placement.StructurePlacement;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.Rotation;
import cn.nukkit.utils.random.Xoroshiro128;

public class WoodlandMansionPopulator extends Populator {

    public static final String NAME = "normal_woodland_mansion";

    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(10387319L)
            .minDistance(20)
            .maxDistance(80)
            .isBiomeValid(biome -> biome == BiomeID.ROOFED_FOREST || biome == BiomeID.ROOFED_FOREST_MUTATED)
            .build());

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        if (!chunk.isOverWorld()) {
            return;
        }

        int biome = chunk.getBiomeId(7, chunk.getHeightMap(7, 7), 7);
        if(!PLACEMENT.canGenerate(level.getSeed(), random, chunkX, chunkZ, biome)) {
            return;
        }

        Rotation rotation = Rotation.NONE;
        BlockVector3 origin = getStartPosition(chunk, rotation);

        BlockManager manager = new BlockManager(level);
        Xoroshiro128 pieceRandom = new Xoroshiro128(level.getSeed());
        pieceRandom.setSeed((long) chunkX * pieceRandom.nextInt() ^ (long) chunkZ * pieceRandom.nextInt() ^ level.getSeed());
        WoodlandMansionPieces.PostPlacement postPlacement = WoodlandMansionPieces.place(manager, origin, rotation, pieceRandom);
        if (!postPlacement.chests().isEmpty() || !postPlacement.mobSpawns().isEmpty() || !postPlacement.spiderSpawnerPositions().isEmpty()) {
            manager.addHook(() -> WoodlandMansionPieces.populatePlacedData(
                    level,
                    postPlacement.chests(),
                    postPlacement.mobSpawns(),
                    postPlacement.spiderSpawnerPositions(),
                    new Xoroshiro128(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ))
            ));
        }
        queueObject(chunk, manager);
    }

    private BlockVector3 getStartPosition(IChunk chunk, Rotation rotation) {
        int startX = (chunk.getX() << 4) + 8;
        int startZ = (chunk.getZ() << 4) + 8;
        int minY = Integer.MAX_VALUE;

        for (int dx = 0; dx < 5; dx++) {
            for (int dz = 0; dz < 5; dz++) {
                int sampleX = switch (rotation) {
                    case ROTATE_90 -> 4 - dz;
                    case ROTATE_180 -> 4 - dx;
                    case ROTATE_270 -> dz;
                    default -> dx;
                };
                int sampleZ = switch (rotation) {
                    case ROTATE_90 -> dx;
                    case ROTATE_180 -> 4 - dz;
                    case ROTATE_270 -> 4 - dx;
                    default -> dz;
                };
                minY = Math.min(minY, Math.max(chunk.getHeightMap(sampleX + 7, sampleZ + 7), 64));
            }
        }

        return new BlockVector3(startX, minY, startZ);
    }

    @Override
    public String name() {
        return NAME;
    }
}
