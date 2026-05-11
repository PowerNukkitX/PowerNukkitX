package cn.nukkit.level.generator.populator.normal;

import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.ancientcity.AncientCityStructure;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.placement.StructurePlacement;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class AncientCityPopulator extends Populator {

    public static final String NAME = "normal_ancient_city";
    private static final int GENERATION_Y = -51;

    public static final StructurePlacement PLACEMENT = new StructurePlacement(StructurePlacement.PlacementSettings.builder()
            .salt(0x616E6369656E744CL)
            .minDistance(8)
            .maxDistance(24)
            .biomeSampleY(GENERATION_Y)
            .isBiomeValid(biome -> biome == BiomeID.DEEP_DARK)
            .build());

    protected static final AncientCityStructure ANCIENT_CITY = new AncientCityStructure();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if (!chunk.isOverWorld()) {
            return;
        }

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        int biome = chunk.getBiomeId(7, GENERATION_Y, 7);
        RandomSourceProvider placementRandom = RandomSourceProvider.create(level.getSeed());
        if (!PLACEMENT.canGenerate(level.getSeed(), placementRandom, chunkX, chunkZ, biome)) {
            return;
        }

        int originX = chunkX << 4;
        int originZ = chunkZ << 4;
        StructureHelper helper = new StructureHelper(level, new BlockVector3(originX, GENERATION_Y, originZ));
        ANCIENT_CITY.place(helper, random.fork());
        queueObject(chunk, helper);
    }

    @Override
    public String name() {
        return NAME;
    }
}
