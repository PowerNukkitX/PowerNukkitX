package cn.nukkit.level.generator.feature;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockSweetBerryBush;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;

public abstract class ObjectGeneratorFeature extends GenerateFeature {

    public abstract ObjectGenerator getGenerator(NukkitRandom random);

    public int getMin() {
        return 5;
    }

    public int getMax() {
        return 6;
    }

    public boolean isSupportValid(Block block) {
        return BlockSweetBerryBush.isSupportValid(block);
    }

    public boolean canSpawnHere(BiomeDefinition definition) {
        return true;
    }

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        NukkitRandom random = new NukkitRandom(Level.chunkHash(chunkX, chunkZ) * level.getSeed());
        int amount = NukkitMath.randomRange(random, getMin(), getMax());
        Vector3 v = new Vector3();
        BlockManager manager = new BlockManager(level);
        for (int i = 0; i < amount; ++i) {

            int x = random.nextInt(15);
            int z = random.nextInt(15);
            int y = chunk.getHeightMap(x , z);
            if (y < level.getMinHeight()) {
                continue;
            }
            BlockManager object = new BlockManager(level);
            v.setComponents(x + (chunkX << 4), y, z + (chunkZ << 4));
            if(!canSpawnHere(Registries.BIOME.get(level.getBiomeId(v.getFloorX(), v.getFloorY(), v.getFloorZ())))) continue;
            if(isSupportValid(level.getBlock(v))) {
                getGenerator(random.identical()).generate(object, random, v.add(0, 1, 0));
                for(Block block : object.getBlocks()) {
                    if(block.getChunk().isGenerated()) {
                        manager.setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                    } else {
                        IChunk nextChunk = block.getChunk();
                        long chunkHash = Level.chunkHash(nextChunk.getX(), nextChunk.getZ());
                        ((Normal) context.getGenerator()).getChunkPlacementQueue(chunkHash).setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                    }
                }
            }

        }
        manager.applySubChunkUpdate(manager.getBlocks());
    }
}
