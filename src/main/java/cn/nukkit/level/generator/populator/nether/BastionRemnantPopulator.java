package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.bastion.BastionStructure;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockVector3;

public class BastionRemnantPopulator extends Populator {

    public static final String NAME = "nether_bastion_remnant";

    private static final BastionStructure BASTION = new BastionStructure();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if (!chunk.isNether()) {
            return;
        }

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        long seed = level.getSeed();

        if (!NetherComplexPlacement.isNetherComplexStart(seed, chunkX, chunkZ, random)) {
            return;
        }
        if (!NetherComplexPlacement.shouldGenerateBastion(seed, chunkX, chunkZ, random)) {
            return;
        }

        int biome = chunk.getBiomeId(7, 33, 7);
        if (biome == BiomeID.BASALT_DELTAS) {
            return;
        }

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int originY = 33;
        StructureHelper helper = new StructureHelper(level, new BlockVector3(originX, originY, originZ));
        BASTION.place(helper, random.fork());
    }

    @Override
    public String name() {
        return NAME;
    }
}
