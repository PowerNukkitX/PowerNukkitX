package cn.nukkit.level.generator.populator.nether.soulsand_valley;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDriedGhast;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.ArrayList;
import java.util.Objects;

import static cn.nukkit.level.generator.stages.nether.NetherTerrainStage.LAVA_LEVEL;

public class NetherFossilPopulator extends Populator {

    public static final String NAME = "nether_fossil";

    protected static final int MIN_DISTANCE = 2;
    protected static final int MAX_DISTANCE = 32;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        if(canGenerate(random, chunk)) {
            int x = (chunkX << 4) + 3;
            int z = (chunkZ << 4) + 3;
            BlockManager manager = new BlockManager(level);
            PNXStructure structure = (PNXStructure) Registries.STRUCTURE.get("nether_fossils/fossil_" + (random.nextInt(14) + 1));
            int height = Integer.MAX_VALUE;
            for(int bx = 0; bx < structure.getSizeX(); bx++) {
                for(int bz = 0; bz < structure.getSizeZ(); bz++) {
                    for(int i : getHighestWorkableBlocks(manager, x+ bx, z + bz)) {
                        if(i < height) height = i;
                    }
                }
            }
            if(height != Integer.MAX_VALUE) {
                Position pos = new Position(x, height, z);
                structure.preparePlace(pos, manager);
                if(random.nextInt(3) == 0) manager.setBlockStateAt(pos.subtract(1, 0, 1), BlockDriedGhast.PROPERTIES.getDefaultState());
                for(Block block : manager.getBlocks()) if(block.isAir()) manager.unsetBlockStateAt(block);
                queueObject(chunk, manager);
            }
        }
    }

    private ArrayList<Integer> getHighestWorkableBlocks(BlockManager level, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 128; y > 0; --y) {
            String b = level.getBlockIdAt(x, y, z);
            if ((Objects.equals(b, Block.SOUL_SAND) || Objects.equals(b, Block.SOUL_SOIL)) && level.getBlockAt(x, y + 1, z).canBeReplaced()) {
                blockYs.add(y + 1);
            }
        }
        return blockYs;
    }

    public boolean canGenerate(RandomSourceProvider random, IChunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        return ((chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX && (chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ) && chunk.getBiomeId(3, LAVA_LEVEL, 3) == BiomeID.SOULSAND_VALLEY;
    }

    @Override
    public String name() {
        return NAME;
    }
}
