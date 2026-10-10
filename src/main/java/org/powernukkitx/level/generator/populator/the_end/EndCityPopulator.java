package org.powernukkitx.level.generator.populator.the_end;

import org.cloudburstmc.protocol.bedrock.data.payload.structure.Rotation;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.EndCityPieces;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructureRandomSpreadPlacement;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.utils.random.BedrockRandom;
import org.powernukkitx.utils.random.Xoroshiro128;

public class EndCityPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "the_end_end_city";
    private static final int SPACING = 20;
    private static final int SEPARATION = 11;
    private static final int PLACEMENT_SALT = 0x009E7F71;
    private static final StructurePlacement PLACEMENT = new StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings.builder()
            .salt(PLACEMENT_SALT)
            .minDistance(SEPARATION)
            .maxDistance(SPACING)
            .build(), StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();

        if ((long) chunkX * (long) chunkX + (long) chunkZ * (long) chunkZ <= 4096L) {
            return;
        }

        if (!PLACEMENT.canGenerate(level.getSeed(), random, chunkX, chunkZ, 0)) {
            return;
        }

        int rotationSeed = chunkZ * PLACEMENT_SALT + chunkX;
        Rotation rotation = Rotation.from(new BedrockRandom(rotationSeed).nextBoundedInt(4));
        BlockVector3 origin = getStartPosition(chunk, rotation);
        if (origin.getY() < 60) {
            return;
        }

        BlockManager manager = new BlockManager(level);
        Xoroshiro128 pieceRandom = new Xoroshiro128(level.getSeed());
        pieceRandom.setSeed((long) chunkX * pieceRandom.nextInt() ^ (long) chunkZ * pieceRandom.nextInt() ^ level.getSeed());

        EndCityPieces.PostPlacement postPlacement = EndCityPieces.place(manager, origin, rotation, pieceRandom);
        StructureAabbVolumes.addDynamic(level, "minecraft:end_city", postPlacement.pieceBounds());

        if (!postPlacement.chests().isEmpty()
            || !postPlacement.banners().isEmpty()
            || !postPlacement.itemFrames().isEmpty()
            || !postPlacement.brewingStands().isEmpty()
            || !postPlacement.shulkerMarkers().isEmpty()) {

                manager.addHook(() -> {
                    EndCityPieces.populatePlacedData(
                            level,
                            postPlacement.chests(),
                            postPlacement.banners(),
                            postPlacement.itemFrames(),
                            postPlacement.brewingStands(),
                            postPlacement.shulkerMarkers(),
                            new Xoroshiro128(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ)));
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
                minY = Math.min(minY, chunk.getHeightMap(sampleX + 7, sampleZ + 7) - 1);
            }
        }

        return new BlockVector3(startX, minY, startZ);
    }

    @Override
    public String name() {
        return NAME;
    }
}
