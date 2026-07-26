package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.object.structures.StructureHelper;
import org.powernukkitx.level.generator.object.structures.jigsaw.ancientcity.AncientCityStructure;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;

public class AncientCityPopulator extends Populator implements PopulatorStructure {

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
        if(!shouldGenerateStructures(context)) return;

        IChunk chunk = context.getChunk();
        if (!chunk.isOverWorld()) {
            return;
        }

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        int biome = chunk.getBiomeId(7, GENERATION_Y, 7);
        RandomSourceProvider placementRandom = new Xoroshiro128(level.getSeed());
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
