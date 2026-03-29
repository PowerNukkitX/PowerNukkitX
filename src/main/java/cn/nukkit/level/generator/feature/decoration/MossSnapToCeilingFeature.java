package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockCaveVines;
import cn.nukkit.block.BlockCaveVinesBodyWithBerries;
import cn.nukkit.block.BlockCaveVinesHeadWithBerries;
import cn.nukkit.block.BlockMossBlock;
import cn.nukkit.block.BlockSporeBlossom;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.random.NukkitRandom;

import static cn.nukkit.block.BlockID.*;

public class MossSnapToCeilingFeature extends GenerateFeature {

    public static final String NAME = "minecraft:moss_ceiling_snap_to_ceiling_feature";

    private static final BlockState MOSS = BlockMossBlock.PROPERTIES.getDefaultState();
    private static final BlockState BERRY_BODY = BlockCaveVinesBodyWithBerries.PROPERTIES.getDefaultState();
    private static final BlockState BERRY_HEAD = BlockCaveVinesHeadWithBerries.PROPERTIES.getDefaultState();
    private static final BlockState VINE = BlockCaveVines.PROPERTIES.getDefaultState();
    private static final BlockState SPORE = BlockSporeBlossom.PROPERTIES.getDefaultState();

    private SimplexF noise;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        SimplexF noise = ((NormalObjectHolder) level.getGeneratorObjectHolder()).getFeatureHolder().getMossSnapToCeiling();
        StructureHelper manager = new StructureHelper(level, new BlockVector3(chunkX << 4, 0, chunkZ << 4));
        for(int x = 0; x < 16; x++) {
            int baseX = x + chunkX << 4;
            for(int z = 0; z < 16; z++) {
                int baseZ = z + chunkZ << 4;
                if(noise.noise2D(baseX * 0.25f, baseZ * 0.25f, true) > 0) {
                    for (int y = chunk.getHeightMap(x, z); y > level.getMinHeight(); y--) {
                        if (chunk.getSection(y >> 4).getBiomeId(x, y & 0x0f, z) == BiomeID.LUSH_CAVES) {
                            if (chunk.getBlockState(x, y, z) == BlockAir.STATE) {
                                for (int _y = 1; _y <= 2; _y++) {
                                    int yy = y + _y;
                                    BlockState state = chunk.getBlockState(x, yy, z);
                                    if (_y == 2) {
                                        switch (state.getIdentifier()) {
                                            case STONE,
                                                 DEEPSLATE -> {
                                                manager.setBlockStateAt(x, yy, z, MOSS);
                                                if (random.nextFloat() < 0.1f) {
                                                    float rnd = random.nextFloat();
                                                    if (rnd < 0.003f) {
                                                        manager.setBlockStateAt(x, yy - 1, z, SPORE);
                                                    } else {
                                                        int i = random.nextInt(0, 4);
                                                        int org = i;
                                                        while (i > 0) {
                                                            i--;
                                                            int yyy = yy - (org - i);
                                                            manager.setBlockStateAt(x, yyy, z, random.nextFloat() > 0.3 ? VINE : BERRY_BODY);
                                                        }
                                                        manager.setBlockStateAt(x, (yy - org) - 1, z, random.nextFloat() > 0.3 ? VINE : BERRY_HEAD);
                                                    }
                                                }
                                            }
                                        }
                                    }
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
