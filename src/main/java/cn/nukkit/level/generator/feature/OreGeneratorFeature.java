package cn.nukkit.level.generator.feature;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OreGeneratorFeature extends GenerateFeature {

    protected static final BlockState STONE = BlockStone.PROPERTIES.getDefaultState();
    protected static final BlockState DEEPSLATE = BlockDeepslate.PROPERTIES.getDefaultState();

    public abstract OrePopulation[] getPopulators();

    public boolean canBeReplaced(BlockState state) {
        return state == STONE || state == DEEPSLATE;
    }

    @Override
    public final void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        NukkitRandom random = new NukkitRandom(Level.chunkHash(chunkX, chunkZ) * level.getSeed());
        int sx = chunkX << 4;
        int ex = sx + 15;
        int sz = chunkZ << 4;
        int ez = sz + 15;
        BlockManager manager = new BlockManager(level);
        for(OrePopulation population : getPopulators()) {
            for (int i = 0; i < population.clustercount(); i++) {
                BlockManager object = new BlockManager(level);
                int x = NukkitMath.randomRange(random, sx, ex);
                int z = NukkitMath.randomRange(random, sz, ez);
                int y = population.minheight + random.nextBoundedInt((population.maxheight - population.minheight) + 1);
                BlockState original = level.getBlockStateAt(x, y, z);
                if (!canBeReplaced(original)) {
                    continue;
                }
                if (population.maxclustersize == 1) {
                    object.setBlockStateAt(x, y, z, population.state);
                } else {
                    spawn(population, object, random.identical(), x, y, z);
                }
                if(object.getBlocks().stream().noneMatch(block -> !block.getChunk().isGenerated())) {
                    for(Block block : object.getBlocks()) {
                        manager.setBlockStateAt(block.asBlockVector3(), block.getBlockState());
                    }
                }
            }
        }
        manager.applySubChunkUpdate(manager.getBlocks());
    }

    protected void spawn(OrePopulation population, BlockManager level, NukkitRandom rand, int cx, int cy, int cz) {

        int count = NukkitMath.randomRange(rand, 1, population.maxclustersize)+1;

        Map<Vector3, Double> scores = new HashMap<>();
        List<Vector3> candidates = new ArrayList<>();

        SimplexF rng = new SimplexF(rand, population.clustercount, population.clustercount);

        int maxSize = (int) Math.ceil(Math.cbrt(count)) + 2;

        for (int x = cx - maxSize; x <= cx + maxSize; x++) {
            for (int y = cy - maxSize; y <= cy + maxSize; y++) {
                for (int z = cz - maxSize; z <= cz + maxSize; z++) {
                    float dx = x - cx;
                    float dy = y - cy;
                    float dz = z - cz;
                    double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);

                    double noise = rng.getNoise3D(x, y, z);

                    double score = dist + noise * 1.5;
                    candidates.add(new Vector3(x, y, z).setComponents(x, y, z).setY(y));
                    scores.put(candidates.get(candidates.size()-1), score);
                }
            }
        }

        candidates.sort(Comparator.comparingDouble(v -> scores.get(v)));

        for (int i = 0; i < count && i < candidates.size(); i++) {
            Vector3 pos = candidates.get(i);
            level.setBlockStateAt(pos, population.state);
        }
    }

    public record OrePopulation(BlockState state, int clustercount, int maxclustersize, int minheight, int maxheight) {
    }

}
