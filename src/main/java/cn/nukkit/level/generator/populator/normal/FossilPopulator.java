package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCoalOre;
import cn.nukkit.block.BlockDeepslateDiamondOre;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.structure.AbstractStructure;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;

public class FossilPopulator extends Populator {

    public static final String NAME = "normal_fossil";

    private final static BlockState DIAMOND_ORE = BlockDeepslateDiamondOre.PROPERTIES.getDefaultState();
    private final static BlockState COAL_ORE = BlockCoalOre.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int biome = chunk.getBiomeId(3, chunk.getHeightMap(3, 3), 3);
        BiomeDefinition definition = Registries.BIOME.get(biome);
        if (definition.getTags().contains(BiomeTags.DESERT) || definition.getTags().contains(BiomeTags.SWAMP) || definition.getTags().contains(BiomeTags.MANGROVE_SWAMP)
                && random.nextBoundedInt(64) == (0x1211dfa1 & 63)) { //salted
            int y = Math.min(64, chunk.getHeightMap(0, 0));

            String id = chunk.getBlockState(0, y, 0).getIdentifier();
            while (id == BlockID.WATER || id == BlockID.FLOWING_WATER) {
                id = chunk.getBlockState(0, --y, 0).getIdentifier();
            }
            BlockManager object = new BlockManager(level);
            BlockVector3 vec = new BlockVector3(chunkX << 4, Math.max(10, y - 15 - random.nextBoundedInt(10)), chunkZ << 4);
            String structure = "fossil/" + (random.nextBoolean() ? "skull" : "spine") + "_" + (random.nextInt(4) + 1);
            AbstractStructure structure1 = Registries.STRUCTURE.get(structure);
            structure1.preparePlace(new Position(vec.getX(), vec.getY(), vec.getZ(), level), object);
            for(Block block : object.getBlocks()) {
                int ran = random.nextInt(10);
                if(ran == 0) object.unsetBlockStateAt(block);
                if(ran == 1) {
                    object.setBlockStateAt(block, y < 0 ? DIAMOND_ORE : COAL_ORE);
                }
            }
            queueObject(chunk, object);
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
