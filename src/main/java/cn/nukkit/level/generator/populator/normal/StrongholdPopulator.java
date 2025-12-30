package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.StrongholdPieces;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.level.generator.object.structures.utils.StructurePiece;
import cn.nukkit.level.generator.object.structures.utils.StructureStart;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class StrongholdPopulator extends Populator {

    public static final String NAME = "normal_stronghold";

    protected static final int SPACING = 32;
    protected static final int SEPARATION = 3;

    private final List<StrongholdStart> discoveredStarts = Lists.newArrayList();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        if (!chunk.isOverWorld()) return;

        if (chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)) {
            BlockManager object = new BlockManager(level);
            StrongholdStart start = new StrongholdStart(object, chunkX, chunkZ);
            start.generatePieces(object, chunkX, chunkZ);
            if (start.isValid()) {
                long seed = level.getSeed();
                random.setSeed(seed);
                BoundingBox boundingBox = start.getBoundingBox();
                for (int cx = boundingBox.x0 >> 4; cx <= boundingBox.x1 >> 4; cx++) {
                    for (int cz = boundingBox.z0 >> 4; cz <= boundingBox.z1 >> 4; cz++) {
                        int x = cx << 4;
                        int z = cz << 4;
                        start.postProcess(object, random, new BoundingBox(x, z, x + 15, z + 15), cx, cz);
                    }
                }
            }
            boolean generated = false;
            List<Long> chunks = new ArrayList<>();
            for(Block block : object.getBlocks()) {
                long hash = Level.chunkHash(block.getChunkX(), block.getChunkZ());
                if(!chunks.contains(hash)) {
                    chunks.add(hash);
                }
            }
            for(Long hash : chunks) {
                int cx = Level.getHashX(hash);
                int cz = Level.getHashZ(hash);
                IChunk chunk1 = level.getChunk(cx, cz);
                if(chunk1 == chunk) continue;
                if(!chunk1.isGenerated()) {
                    level.syncGenerateChunk(cx, cz);
                    generated = true;
                }
            }
            if(generated) {
                CompoundTag extra = chunk.getExtraData();
                int attempt = extra.getInt("strongholdGeneratioAttepmt") + 1;
                if(attempt < 5) {
                    chunk.getExtraData().putInt("strongholdGeneratioAttepmt", attempt);
                    apply(context);
                }
            }
            else queueObject(chunk, object);
        }
    }

    public class StrongholdStart extends StructureStart {

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
                    this.moveBelowSeaLevel(64, this.random, 10);
                } while (this.pieces.isEmpty() || start.portalRoomPiece == null);

                StrongholdPopulator.this.discoveredStarts.add(this);
            }
        }

        @Override //\\ StrongholdStart::getType(void) // 5
        public String getType() {
            return "Stronghold";
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
