package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.MineshaftPieces;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.generator.object.structures.utils.StructurePiece;
import cn.nukkit.level.generator.object.structures.utils.StructureStart;
import cn.nukkit.level.generator.populator.Populator;

public class PopulatorMineshaft extends Populator {

    public static final String NAME = "normal_mineshaft";

    protected static final int PROBABILITY = 4;

    static {
        MineshaftPieces.init();
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        BlockManager manager = new BlockManager(level);
        if (random.nextBoundedInt(1000) < PROBABILITY) {
            //\\ MineshaftFeature::createStructureStart(Dimension &,BiomeSource &,Random &,ChunkPos const &)
            MineshaftStart start = new MineshaftStart(manager, chunkX, chunkZ);
            start.generatePieces(manager, chunkX, chunkZ);

            if (start.isValid()) { //TODO: serialize nbt
                BoundingBox boundingBox = start.getBoundingBox();
                for (int cx = boundingBox.x0 >> 4; cx <= boundingBox.x1 >> 4; cx++) {
                    for (int cz = boundingBox.z0 >> 4; cz <= boundingBox.z1 >> 4; cz++) {
                        int x = cx << 4;
                        int z = cz << 4;
                        IChunk ck = level.getChunk(cx, cz);
                        if (ck == null) {
                            ck = chunk.getProvider().getChunk(cx, cz, true);
                        }
                        start.postProcess(manager, random, new BoundingBox(x, z, x + 15, z + 15), cx, cz);
                    }
                }
                queueObject(chunk, manager);
            }
        }
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
                MineshaftPieces.Type type = biome >= BiomeID.MESA && biome <= BiomeID.MESA_PLATEAU || biome >= BiomeID.MESA_BRYCE && biome <= BiomeID.MESA_PLATEAU_STONE ? MineshaftPieces.Type.MESA : MineshaftPieces.Type.NORMAL;

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
                    this.moveBelowSeaLevel(64, this.random, 10);
                }
            }
        }

        @Override //\\ MineshaftStart::getType(void) // 3
        public String getType() {
            return "Mineshaft";
        }
    }
}
