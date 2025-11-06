package cn.nukkit.level.generator.feature;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockSweetBerryBush;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.feature.tree.BambooJungleTreeFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectGenerator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public abstract class ObjectGeneratorFeature extends GenerateFeature {

    public abstract ObjectGenerator getGenerator(RandomSourceProvider random);

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
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ)+ name().hashCode());
        int amount = NukkitMath.randomRange(random, getMin(), getMax());
        Vector3 v = new Vector3();
        BlockManager object = new BlockManager(level);
        for (int i = 0; i < amount; ++i) {
            int x = random.nextInt(15);
            int z = random.nextInt(15);
            int y = chunk.getHeightMap(x, z);
            if (y < level.getMinHeight()) {
                continue;
            }
            v.setComponents(x + (chunkX << 4), y, z + (chunkZ << 4));
            if(!canSpawnHere(Registries.BIOME.get(level.getBiomeId(v.getFloorX(), v.getFloorY(), v.getFloorZ())))) continue;
            while(checkBlock(level.getBlock(v))) {
                v.y--;
            }
            if(isSupportValid(level.getBlock(v))) {
                getGenerator(random).generate(object, random, v.add(0, 1, 0));
            }
        }

        queueObject(chunk, object);
    }

    protected boolean checkBlock(Block bl) {
        return (bl.canBeReplaced() || !bl.isFullBlock()) && !(bl instanceof BlockLiquid) && bl.getY() > SEA_LEVEL;
    }
}
