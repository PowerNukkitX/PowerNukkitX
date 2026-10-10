package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.StrongholdPieces;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.object.structures.utils.StructurePiece;
import org.powernukkitx.level.generator.object.structures.utils.StructureStart;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StrongholdPlacement;

import java.util.ArrayList;
import java.util.List;

public class StrongholdPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "normal_stronghold";

    public static final StrongholdPlacement PLACEMENT = new StrongholdPlacement(VillagePopulator.PLACEMENT);

    private static final int START_SEARCH_RADIUS = 8;

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        if (!chunk.isOverWorld()) return;

        long seed = level.getSeed();
        BlockManager object = new BlockManager(level);
        BoundingBox chunkBounds = new BoundingBox(chunkX << 4, chunkZ << 4, (chunkX << 4) + 15, (chunkZ << 4) + 15);
        random.setSeed(seed);
        List<StructureAabbVolumes.DynamicStructure> aabbStructures = new ArrayList<>();

        for (int startChunkX = chunkX - START_SEARCH_RADIUS; startChunkX <= chunkX + START_SEARCH_RADIUS; startChunkX++) {
            for (int startChunkZ = chunkZ - START_SEARCH_RADIUS; startChunkZ <= chunkZ + START_SEARCH_RADIUS; startChunkZ++) {
                if (!PLACEMENT.canGenerate(seed, random, startChunkX, startChunkZ, level.getBiomePicker())) {
                    continue;
                }

                final int originChunkX = startChunkX;
                final int originChunkZ = startChunkZ;
                StrongholdStart start = context.getGenerator().getStructureStartCache().getOrCreate(
                        StrongholdStart.class,
                        originChunkX,
                        originChunkZ,
                        () -> createStart(level, originChunkX, originChunkZ)
                );
                if (start.isValid() && start.getBoundingBox().intersects(chunkBounds)) {
                    start.postProcessPieces(object, random, chunkBounds, chunkX, chunkZ);
                    aabbStructures.add(StructureAabbVolumes.DynamicStructure.fromStart(start));
                }
            }
        }

        StructureAabbVolumes.replaceDynamic(chunk, "minecraft:stronghold", aabbStructures);
        queueObject(chunk, object);
    }

    private static StrongholdStart createStart(Level level, int chunkX, int chunkZ) {
        BlockManager manager = new BlockManager(level);
        StrongholdStart start = new StrongholdStart(manager, chunkX, chunkZ);
        start.generatePieces(manager, chunkX, chunkZ);
        return start;
    }

    public static class StrongholdStart extends StructureStart {

        public StrongholdStart(BlockManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        @Override
        public void generatePieces(BlockManager level, int chunkX, int chunkZ) {
            synchronized (StrongholdPieces.getLock()) {
                int count = 0;
                long seed = level.getSeed();
                StrongholdPieces.StartPiece start;

                do {
                    this.pieces.clear();
                    this.boundingBox = BoundingBox.getUnknownBox();
                    this.random.setSeed(seed + count++);
                    this.random.setSeed((long) chunkX * this.random.nextInt() ^ (long) chunkZ * this.random.nextInt() ^ level.getSeed());
                    StrongholdPieces.resetPieces();

                    start = new StrongholdPieces.StartPiece(this.random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
                    this.pieces.add(start);
                    start.addChildren(start, this.pieces, this.random);

                    List<StructurePiece> children = start.pendingChildren;
                    while (!children.isEmpty()) {
                        children.remove(this.random.nextInt(children.size()))
                                .addChildren(start, this.pieces, this.random);
                    }

                    this.calculateBoundingBox();
                    this.moveBelowSeaLevel(this.random, 10);
                } while (this.pieces.isEmpty() || start.portalRoomPiece == null);
            }
        }

        @Override //\\ StrongholdStart::getType(void) // 5
        public String getType() {
            return "minecraft:stronghold";
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
