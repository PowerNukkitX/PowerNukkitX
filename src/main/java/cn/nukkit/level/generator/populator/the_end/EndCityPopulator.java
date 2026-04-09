package cn.nukkit.level.generator.populator.the_end;

import cn.nukkit.level.Dimension;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.EndCityPieces;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.Rotation;
import cn.nukkit.utils.random.Xoroshiro128;

public class EndCityPopulator extends Populator {

    public static final String NAME = "the_end_end_city";
    private static final int SPACING = 20;
    private static final int SEPARATION = 11;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Dimension level = chunk.getLevel();

        if ((long) chunkX * (long) chunkX + (long) chunkZ * (long) chunkZ <= 4096L) {
            return;
        }

        random.setSeed(level.getSeed() ^ Dimension.chunkHash(chunkX, chunkZ));
        if (chunkX != (((chunkX < 0 ? chunkX - SPACING + 1 : chunkX) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)
                || chunkZ != (((chunkZ < 0 ? chunkZ - SPACING + 1 : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)) {
            return;
        }

        Rotation rotation = Rotation.from(random.nextInt(4));
        BlockVector3 origin = getStartPosition(chunk, rotation);
        if (origin.getY() < 60) {
            return;
        }

        BlockManager manager = new BlockManager(level);
        Xoroshiro128 pieceRandom = new Xoroshiro128(level.getSeed());
        pieceRandom.setSeed((long) chunkX * pieceRandom.nextInt() ^ (long) chunkZ * pieceRandom.nextInt() ^ level.getSeed());

        EndCityPieces.PostPlacement postPlacement = EndCityPieces.place(manager, origin, rotation, pieceRandom);

        if (!postPlacement.chests().isEmpty() || !postPlacement.banners().isEmpty() || !postPlacement.itemFrames().isEmpty() || !postPlacement.brewingStands().isEmpty() || !postPlacement.shulkerMarkers().isEmpty()) {
            manager.addHook(() -> {
                EndCityPieces.populatePlacedData(
                        level,
                        postPlacement.chests(),
                        postPlacement.banners(),
                        postPlacement.itemFrames(),
                        postPlacement.brewingStands(),
                        postPlacement.shulkerMarkers(),
                        new Xoroshiro128(level.getSeed() ^ Dimension.chunkHash(chunkX, chunkZ)));
            });
            queueObject(chunk, manager);

        }
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
                minY = Math.min(minY, chunk.getHeightMap(sampleX + 7, sampleZ + 7));
            }
        }

        return new BlockVector3(startX, minY, startZ);
    }

    @Override
    public String name() {
        return NAME;
    }
}
