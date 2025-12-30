package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.NetherBridgePieces;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.generator.object.structures.utils.StructurePiece;
import cn.nukkit.level.generator.object.structures.utils.StructureStart;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.List;

public class NetherFortressPopulator extends Populator {

    public static final String NAME = "nether_nether_fortress";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int gx = chunkX >> 4;
        int gz = chunkZ >> 4;
        long seed = level.getSeed();
        random.setSeed(gx ^ gz << 4 ^ seed);
        random.nextInt();
        if (random.nextBoundedInt(3) == (0x51d8e999 & 3) // salted
                && chunkX == (gx << 4) + 4 + random.nextBoundedInt(8) && chunkZ == (gz << 4) + 4 + random.nextBoundedInt(8)) {
            random.setSeed(seed);
            int r1 = random.nextInt();
            int r2 = random.nextInt();
            BlockManager manager = new BlockManager(level);

            NetherFortressStart start = new NetherFortressStart(manager, chunkX, chunkZ);
            start.generatePieces(manager, chunkX, chunkZ);

            if (start.isValid()) {
                BoundingBox boundingBox = start.getBoundingBox();
                for (int cx = boundingBox.x0 >> 4; cx <= boundingBox.x1 >> 4; cx++) {
                    for (int cz = boundingBox.z0 >> 4; cz <= boundingBox.z1 >> 4; cz++) {
                        NukkitRandom rand = new NukkitRandom((long) cx * r1 ^ (long) cz * r2 ^ seed);
                        int x = cx << 4;
                        int z = cz << 4;
                        start.postProcess(manager, rand, new BoundingBox(x, z, x + 15, z + 15), cx, cz);
                    }
                }
                manager.generateChunks();
                queueObject(chunk, manager);
            }
        }
    }

    public static class NetherFortressStart extends StructureStart {

        public NetherFortressStart(BlockManager level, int chunkX, int chunkZ) {
            super(level, chunkX, chunkZ);
        }

        @Override
        public void generatePieces(BlockManager level, int chunkX, int chunkZ) {
            NetherBridgePieces.StartPiece start = new NetherBridgePieces.StartPiece(this.random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.pieces.add(start);
            start.addChildren(start, this.pieces, this.random);

            List<StructurePiece> pendingChildren = start.pendingChildren;
            while (!pendingChildren.isEmpty()) {
                pendingChildren.remove(this.random.nextBoundedInt(pendingChildren.size()-1))
                        .addChildren(start, this.pieces, this.random);
            }

            this.calculateBoundingBox();
            this.moveInsideHeights(this.random, 48, 70);
        }

        @Override
        public String getType() {
            return "Fortress";
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
