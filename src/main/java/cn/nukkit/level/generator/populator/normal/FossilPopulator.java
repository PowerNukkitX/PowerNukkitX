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
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;

public class FossilPopulator extends Populator {

    public static final String NAME = "normal_fossil";
    private static final int RARITY = 64;
    private static final int MAX_EMPTY_CORNERS_ALLOWED = 4;

    private final static BlockState DIAMOND_ORE = BlockDeepslateDiamondOre.PROPERTIES.getDefaultState();
    private final static BlockState COAL_ORE = BlockCoalOre.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        int biome = chunk.getBiomeId(7, chunk.getHeightMap(7, 7), 7);
        BiomeDefinition definition = Registries.BIOME.get(biome);
        if (random.nextInt(RARITY) == 0 && isFossilBiome(definition)) {
            int y = Math.min(64, chunk.getHeightMap(0, 0));

            String id = chunk.getBlockState(0, y, 0).getIdentifier();
            while (id.equals(BlockID.WATER)) {
                id = chunk.getBlockState(0, --y, 0).getIdentifier();
            }
            BlockManager object = new BlockManager(level);
            BlockVector3 vec = new BlockVector3(chunkX << 4, Math.max(10, y - 15 - random.nextBoundedInt(10)), chunkZ << 4);
            String structure = "fossil/" + (random.nextBoolean() ? "skull" : "spine") + "_" + (random.nextInt(4) + 1);
            AbstractStructure structure1 = Registries.STRUCTURE.get(structure);
            structure1.preparePlace(new Position(vec.getX(), vec.getY(), vec.getZ(), level), object);
            if (countEmptyCorners(level, object) > MAX_EMPTY_CORNERS_ALLOWED) {
                return;
            }
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

    private boolean isFossilBiome(BiomeDefinition definition) {
        return definition.getTags().contains(BiomeTags.DESERT)
                || definition.getTags().contains(BiomeTags.SWAMP)
                || definition.getTags().contains(BiomeTags.MANGROVE_SWAMP);
    }

    private int countEmptyCorners(Level level, BlockManager object) {
        AxisAlignedBB bounds = object.getBounds();

        int minX = (int) bounds.getMinX();
        int minY = (int) bounds.getMinY();
        int minZ = (int) bounds.getMinZ();
        int maxX = (int) bounds.getMaxX();
        int maxY = (int) bounds.getMaxY();
        int maxZ = (int) bounds.getMaxZ();

        int count = 0;
        for (int x : new int[]{minX, maxX}) {
            for (int y : new int[]{minY, maxY}) {
                for (int z : new int[]{minZ, maxZ}) {
                    if (isEmptyCorner(level.getBlockIdAt(x, y, z))) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private boolean isEmptyCorner(String id) {
        return BlockID.AIR.equals(id)
                || BlockID.WATER.equals(id)
                || BlockID.LAVA.equals(id);
    }

    @Override
    public String name() {
        return NAME;
    }
}
