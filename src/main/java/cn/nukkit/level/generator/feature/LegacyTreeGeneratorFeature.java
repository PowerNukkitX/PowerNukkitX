package cn.nukkit.level.generator.feature;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSponge;
import cn.nukkit.block.BlockSweetBerryBush;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public abstract class LegacyTreeGeneratorFeature extends GenerateFeature {

    public abstract LegacyTreeGenerator getGenerator(RandomSourceProvider random);

    public int getMin() {
        return 5;
    }

    public int getMax() {
        return 6;
    }

    public String getRequiredTag() {
        return BiomeTags.OVERWORLD;
    }

    public boolean isSupportValid(Block block) {
        return BlockSweetBerryBush.isSupportValid(block);
    }

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
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
            if(!Registries.BIOME.get(level.getBiomeId(v.getFloorX(), v.getFloorY(), v.getFloorZ())).getTags().contains(getRequiredTag())) continue;
            if(isSupportValid(level.getBlock(v))) {
                LegacyTreeGenerator generator = getGenerator(random);
                if(generator == null) return;
                generator.placeObject(object, v.getFloorX(), v.getFloorY() + 1, v.getFloorZ(), random);
                for(Block block : object.getBlocks()) {
                    if(block.getChunk() != chunk) {
                        IChunk nextChunk = block.getChunk();
                        long chunkHash = Level.chunkHash(nextChunk.getX(), nextChunk.getZ());
                        getChunkPlacementQueue(chunkHash, level).setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                    }
                    if(block.getChunk().isGenerated()) {
                        manager.setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                    }
                }
            }
        }
        writeOutsideChunkStructureData(chunk);
        manager.applySubChunkUpdate();
    }
}
