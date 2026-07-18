package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.MineshaftPieces;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructurePiece;
import org.powernukkitx.level.generator.object.structures.utils.StructureStart;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.registry.Registries;

public class MineshaftPopulator extends Populator implements PopulatorStructure {

    public static final String NAME = "normal_mineshaft";

    protected static final int PROBABILITY = 4;

    @Override
    public void apply(ChunkGenerateContext context) {
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        long seed = level.getSeed();
        random.setSeed(seed);
        int r1 = random.nextInt();
        int r2 = random.nextInt();
        random.setSeed((long) chunkX * r1 ^ (long) chunkZ * r2 ^ seed);
        BlockManager manager = new BlockManager(level);
        if (random.nextBoundedInt(1000) < PROBABILITY) {
            //\\ MineshaftFeature::createStructureStart(Dimension &,BiomeSource &,Random &,ChunkPos const &)
            MineshaftStart start = new MineshaftStart(manager, chunkX, chunkZ);
            start.generatePieces(manager, chunkX, chunkZ);

            if (start.isValid()) {
                BoundingBox boundingBox = start.getBoundingBox();
                for (int cx = boundingBox.x0 >> 4; cx <= boundingBox.x1 >> 4; cx++) {
                    for (int cz = boundingBox.z0 >> 4; cz <= boundingBox.z1 >> 4; cz++) {
                        int x = cx << 4;
                        int z = cz << 4;
                        start.postProcess(manager, random, new BoundingBox(x, z, x + 15, z + 15), cx, cz);
                    }
                }
                for (Block block : manager.getBlocks()) {
                    level.getOrGenerateChunk(block.getChunkX(), block.getChunkZ());
                    if (block.isAir()) {
                        if (level.getBlock(block).getId() == Block.WATER) {
                            manager.setBlockStateAt(block, BlockWater.PROPERTIES.getDefaultState());
                        } else if (block.up().getId() == Block.WATER) {
                            var seaFloor = seaFloorBlockFor(level, block);
                            if (seaFloor != null) {
                                manager.setBlockStateAt(block, seaFloor);
                            }
                        }
                    }
                }
                queueObject(chunk, manager);
            }
        }
    }

    /** Resolve the biome's sea-floor block at this position, or null if the biome lacks surface-material data. */
    private static org.powernukkitx.block.BlockState seaFloorBlockFor(Level level, Block block) {
        var biome = Registries.BIOME.get(level.getBiomeId(block.getFloorX(), block.getFloorY(), block.getFloorZ())).second();
        var genData = biome == null ? null : biome.getChunkGenData();
        var surfaceBuilder = genData == null ? null : genData.getSurfaceBuilderData();
        var surfaceMaterial = surfaceBuilder == null ? null : surfaceBuilder.getSurfaceMaterial();
        if (surfaceMaterial == null || surfaceMaterial.getSeaFloorBlock() == null) {
            return null;
        }
        return Registries.BLOCKSTATE.get(surfaceMaterial.getSeaFloorBlock().getRuntimeId());
    }

    @Override
    public String name() {
        return NAME;
    }

    public static class MineshaftStart extends StructureStart {

        public MineshaftStart(BlockManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        @Override
        public void generatePieces(BlockManager level, int chunkX, int chunkZ) {
            IChunk chunk = level.getChunk(chunkX, chunkZ);
            if (chunk != null) {
                int biome = chunk.getBiomeId(7, chunk.getHeightMap(7, 7), 7);
                MineshaftPieces.Type type = isBadlandsBiome(biome) ? MineshaftPieces.Type.MESA : MineshaftPieces.Type.NORMAL;

                MineshaftPieces.MineshaftRoom start = new MineshaftPieces.MineshaftRoom(0, this.random, (chunkX << 4) + 2, (chunkZ << 4) + 2, type);
                this.pieces.add(start);
                start.addChildren(start, this.pieces, this.random);
                this.calculateBoundingBox();

                if (type == MineshaftPieces.Type.MESA) {
                    int offset = 64 - this.boundingBox.y1 + this.boundingBox.getYSpan() / 2 + 5;
                    this.boundingBox.move(0, offset, 0);
                    for (StructurePiece piece : this.pieces) {
                        piece.move(0, offset, 0);
                    }
                } else {
                    this.moveBelowSeaLevel(this.random, 10);
                }
            }
        }

        private static boolean isBadlandsBiome(int biome) {
            return biome >= BiomeID.MESA && biome <= BiomeID.MESA_PLATEAU
                    || biome >= BiomeID.MESA_BRYCE && biome <= BiomeID.MESA_PLATEAU_MUTATED;
        }

        @Override //\\ MineshaftStart::getType(void) // 3
        public String getType() {
            return "Mineshaft";
        }
    }
}
