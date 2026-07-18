package org.powernukkitx.level.generator.feature;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.Supportable;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public abstract class ObjectGeneratorFeature extends GenerateFeature implements Supportable {

    public abstract ObjectGenerator getGenerator(RandomSourceProvider random);

    public int getMin() {
        return 5;
    }

    public int getMax() {
        return 6;
    }

    public boolean canSpawnHere(BiomeDefinitionData definition) {
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
            if(!canSpawnHere(Registries.BIOME.get(level.getBiomeId(v.getFloorX(), v.getFloorY(), v.getFloorZ())).second())) continue;
            while(checkBlock(level.getBlock(v))) {
                v.y--;
            }
            if(isSupportDirt(level.getBlock(v))) {
                getGenerator(random).generate(object, random, v.add(0, 1, 0));
            }
        }

        queueObject(chunk, object);
    }

    protected boolean checkBlock(Block bl) {
        return (bl.canBeReplaced() || !bl.isFullBlock()) && !(bl instanceof BlockLiquid) && bl.getY() > SEA_LEVEL;
    }
}
