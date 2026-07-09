package org.powernukkitx.level.generator.feature;

import org.powernukkitx.block.Supportable;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockSweetBerryBush;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.TreeGenerator;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.random.RandomSourceProvider;

public abstract class LegacyTreeGeneratorFeature extends GenerateFeature implements Supportable {

    public abstract TreeGenerator getGenerator(RandomSourceProvider random);

    public int getMin() {
        return 5;
    }

    public int getMax() {
        return 6;
    }

    public String getRequiredTag() {
        return BiomeTags.OVERWORLD;
    }

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        this.random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ name().hashCode());
        int amount = NukkitMath.randomRange(random, getMin(), getMax());
        Vector3 v = new Vector3();
        BlockManager manager = new BlockManager(level);
        for (int i = 0; i < amount; ++i) {
            int x = random.nextInt(15);
            int z = random.nextInt(15);
            int y = chunk.getHeightMap(x, z);
            if (y < level.getMinHeight()) {
                continue;
            }
            BlockManager object = new BlockManager(level);
            v.setComponents(x + (chunkX << 4), y, z + (chunkZ << 4));
            if (!Registries.BIOME.containsTag(getRequiredTag(), level.getBiomeId(v.getFloorX(), v.getFloorY(), v.getFloorZ()))) continue;
            if(isSupportDirt(level.getBlock(v))) {
                TreeGenerator generator = getGenerator(random);
                if(generator == null) return;
                generator.generate(object, random, new Vector3(v.getFloorX(), v.getFloorY() + 1, v.getFloorZ()));
                manager.merge(object);
            }
        }
        queueObject(chunk, manager);
    }
}
