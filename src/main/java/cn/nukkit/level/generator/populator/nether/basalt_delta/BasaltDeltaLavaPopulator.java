package cn.nukkit.level.generator.populator.nether.basalt_delta;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBasalt;
import cn.nukkit.block.BlockBlackstone;
import cn.nukkit.block.BlockGravel;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLava;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;

import java.util.ArrayList;

public class BasaltDeltaLavaPopulator extends Populator {

    public static final String NAME = "nether_basalt_delta_lava";

    private static final BlockState GRAVEL = BlockGravel.PROPERTIES.getDefaultState();
    private static final BlockState BASALT = BlockBasalt.PROPERTIES.getDefaultState();
    private static final BlockState BLACKSTONE = BlockBlackstone.PROPERTIES.getDefaultState();
    private static final BlockState MAGMA = BlockMagma.PROPERTIES.getDefaultState();
    private static final BlockState LAVA = BlockLava.PROPERTIES.getDefaultState();

    private SimplexNoise surfaceNoise;
    private SimplexNoise surfaceSecNoise;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        int amount = random.nextInt(64) + 64;
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        if(surfaceNoise == null) surfaceNoise = new SimplexNoise(random, -6, new float[]{1f, 1f, 1f});
        if(surfaceSecNoise == null) surfaceSecNoise = new SimplexNoise(random, -6, new float[]{1f, 0, 1f, 1f});
        BlockManager object = new BlockManager(level);
        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            if(level.getBiomeId(x, 0, z) != BiomeID.BASALT_DELTAS) continue;
            ArrayList<Integer> ys = this.getHighestWorkableBlocks(object, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                object.setBlockStateAt(x, y, z, BlockID.FLOWING_LAVA);
            }
        }
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                if(chunk.getBiomeId(x, 0, z) != BiomeID.BASALT_DELTAS) continue;
                for (int y = 1; y < 127; ++y) {
                    Block block = object.getBlockIfCachedOrLoaded(x + baseX, y, z + baseZ, GRAVEL);
                    if(block.getId().equals(BlockID.GRAVEL)) {
                        float sec = surfaceSecNoise.getValue(x + baseX, y, z + baseZ);
                        BlockState STATE = sec < -0.9f ? BLACKSTONE : (sec < 0.8f ? BASALT : MAGMA);
                        if(surfaceNoise.getValue(x + baseX ,y ,z + baseZ) > 0f) {
                            object.setBlockStateAt(x + baseX, y, z + baseZ, STATE);
                        } else {
                            boolean air = false;
                            for (BlockFace face : BlockFace.getHorizontals()) {
                                BlockVector3 side = block.asBlockVector3().getSide(face);
                                if (object.getBlockIfCachedOrLoaded(side.x, side.y, side.z).isAir()) {
                                    air = true;
                                }
                            }
                            if (air) {
                                object.setBlockStateAt(x + baseX, y, z + baseZ, STATE);
                            } else object.setBlockStateAt(x + baseX, y, z + baseZ, LAVA);
                        }
                    }
                }
            }
        }
        queueObject(chunk, object);
    }

    private ArrayList<Integer> getHighestWorkableBlocks(BlockManager level, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 128; y > 0; --y) {
            String b = level.getBlockIdIfCachedOrLoaded(x, y, z);
            if ((b == Block.BASALT || b == Block.BLACKSTONE) &&
                    level.getBlockIfCachedOrLoaded(x, y + 1, z).isAir() &&
                    !level.getBlockIfCachedOrLoaded(x + 1, y, z).isAir() &&
                    !level.getBlockIfCachedOrLoaded(x - 1, y, z).isAir() &&
                    !level.getBlockIfCachedOrLoaded(x, y, z + 1).isAir() &&
                    !level.getBlockIfCachedOrLoaded(x, y, z - 1).isAir()
            ) {
                blockYs.add(y);
            }
        }
        return blockYs;
    }

    @Override
    public String name() {
        return NAME;
    }
}
