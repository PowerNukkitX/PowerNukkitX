package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSculk;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.random.NukkitRandom;

public class SculkPatchFeature extends GenerateFeature {

    public static final String NAME = "minecraft:sculk_patch_feature";

    private static final BlockState SCULK = BlockSculk.PROPERTIES.getDefaultState();

    private SimplexF noise;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        if(noise == null) noise = new SimplexF(new NukkitRandom(chunk.getLevel().getSeed()), 20f, 1 / 99f, 1 / 100f);
        BlockManager manager = new BlockManager(level);
        for(int x = 0; x < 16; x++) {
            int baseX = ((chunk.getX() << 4) + x);
            for (int z = 0; z < 16; z++) {
                int baseZ = ((chunk.getZ() << 4) + z);
                for (int y = level.getMaxHeight(); y > level.getMinHeight(); y--) {
                    if(chunk.getSection(y >> 4).getBiomeId(x, y & 0x0f, z) == BiomeID.DEEP_DARK) {
                        Block block = manager.getBlockIfCachedOrLoaded(baseX, y, baseZ);
                        if (!block.isAir()) {
                            boolean air = false;
                            for (BlockFace face : BlockFace.values()) {
                                BlockVector3 side = new BlockVector3(baseX, y, baseZ).getSide(face);
                                if (manager.getBlockIfCachedOrLoaded(side.x, side.y, side.z).isAir()) {
                                    air = true;
                                    break;
                                }
                            }
                            if(air) {
                                float noiseV = noise.noise3D(baseX * 5.5f, y * 5.5f, baseZ * 5.5f, true);
                                if (noiseV < 0.2f) {
                                    manager.setBlockStateAt(baseX, y, baseZ, SCULK);
                                } else if(noiseV < 0.22f) {
                                  //  manager.setBlockStateAt(baseX, y, baseZ, SCULK_VEIN);
                                }
                            }

                        }
                    }
                }
            }
        }
        queueObject(chunk, manager);
    }

    @Override
    public String name() {
        return NAME;
    }
}
