package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.village.DesertVillageStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.village.PlainsVillageStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.village.SavannaVillageStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.village.SnowyVillageStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.village.TaigaVillageStructure;
import cn.nukkit.level.generator.object.structures.jigsaw.village.VillageStructure;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.BlockVector3;

public class VillagePopulator extends Populator {

    public static final String NAME = "normal_village";
    protected static final long SALT = 0x76696C6C616765L;

    protected static final int MIN_DISTANCE = 8;
    protected static final int MAX_DISTANCE = 32;

    protected static final VillageStructure PLAINS_VILLAGE = new PlainsVillageStructure();
    protected static final VillageStructure DESERT_VILLAGE = new DesertVillageStructure();
    protected static final VillageStructure SAVANNA_VILLAGE = new SavannaVillageStructure();
    protected static final VillageStructure TAIGA_VILLAGE = new TaigaVillageStructure();
    protected static final VillageStructure SNOWY_VILLAGE = new SnowyVillageStructure();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();

        if (!canGenerate(level.getSeed(), chunkX, chunkZ)) {
            return;
        }

        int biome = chunk.getBiomeId(7, chunk.getHeightMap(7, 7), 7);
        VillageStructure village = getVillageForBiome(biome);
        if (village == null) {
            return;
        }

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        int originY = findGenerationY(chunk, level);
        StructureHelper helper = new StructureHelper(level, new BlockVector3(originX, originY, originZ));
        village.place(helper, random.fork());
    }

    protected boolean canGenerate(long levelSeed, int chunkX, int chunkZ) {
        random.setSeed((levelSeed ^ SALT) + Level.chunkHash(chunkX, chunkZ));
        return (chunkX < 0 ? (chunkX - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkX / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX
                && (chunkZ < 0 ? (chunkZ - MAX_DISTANCE - 1) / MAX_DISTANCE : chunkZ / MAX_DISTANCE) * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ;
    }

    protected int findGenerationY(IChunk chunk, Level level) {
        int worldX = (chunk.getX() << 4) + 7;
        int worldZ = (chunk.getZ() << 4) + 7;
        int y = level.getHeightMap(worldX, worldZ);
        while (y > level.getMinHeight() && level.getBlock(worldX, y, worldZ).canBeReplaced()) {
            y--;
        }
        return y;
    }

    protected VillageStructure getVillageForBiome(int biome) {
        return switch (biome) {
            case BiomeID.DESERT -> DESERT_VILLAGE;
            case BiomeID.SAVANNA -> SAVANNA_VILLAGE;
            case BiomeID.TAIGA -> TAIGA_VILLAGE;
            case BiomeID.ICE_PLAINS,
                 BiomeID.COLD_TAIGA -> SNOWY_VILLAGE;
            case BiomeID.PLAINS,
                 BiomeID.SUNFLOWER_PLAINS,
                 BiomeID.MEADOW -> PLAINS_VILLAGE;
            default -> null;
        };
    }

    @Override
    public String name() {
        return NAME;
    }
}
