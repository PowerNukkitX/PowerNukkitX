package org.powernukkitx.level.generator.populator.normal;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.OverworldBiomePicker;
import org.powernukkitx.level.generator.object.structures.StructureHelper;
import org.powernukkitx.level.generator.object.structures.jigsaw.ancientcity.AncientCityStructure;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.generator.object.structures.utils.StructureAabbVolumes;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.level.generator.populator.PopulatorStructure;
import org.powernukkitx.level.generator.populator.placement.StructureRandomSpreadPlacement;
import org.powernukkitx.level.generator.populator.placement.StructurePlacement;
import org.powernukkitx.math.BlockVector3;

import java.util.ArrayList;
import java.util.List;

public class AncientCityPopulator extends Populator implements PopulatorStructure {
    public static final String NAME = "normal_ancient_city";
    private static final String STRUCTURE_TYPE = "minecraft:ancient_city";
    private static final int START_SEARCH_RADIUS = 9;
    private static final int PLACEMENT_BIOME_Y = -27;
    private static final int GENERATION_Y = -51;

    public static final StructureRandomSpreadPlacement PLACEMENT = new StructureRandomSpreadPlacement(
            StructurePlacement.PlacementSettings.builder()
                    .salt(0x01327220L)
                    .minDistance(8)
                    .maxDistance(24)
                    .biomeSampleY(PLACEMENT_BIOME_Y)
                    .isBiomeValid(biome -> biome == BiomeID.DEEP_DARK)
                    .build(), StructureRandomSpreadPlacement.SpreadType.TRIANGULAR) {
        @Override
        protected boolean isValidBiome(BiomePicker<?> biomePicker, int chunkX, int chunkZ) {
            return biomePicker instanceof OverworldBiomePicker overworldBiomePicker
                    && isDeepDarkPlacementBiome(overworldBiomePicker, chunkX, chunkZ);
        }
    };

    private static boolean isDeepDarkPlacementBiome(OverworldBiomePicker biomePicker, int chunkX, int chunkZ) {
        int x = (chunkX << 4) + 7;
        int z = (chunkZ << 4) + 7;
        int surfaceY = biomePicker.predictSurfaceHeight(x, z);
        return biomePicker.pickRaw(x, PLACEMENT_BIOME_Y, z)
                .correct(PLACEMENT_BIOME_Y - surfaceY)
                .getBiomeId() == BiomeID.DEEP_DARK;
    }

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
        long seed = level.getSeed();
        List<StructureAabbVolumes.DynamicStructure> aabbStructures = new ArrayList<>();

        for (int startChunkX = chunkX - START_SEARCH_RADIUS; startChunkX <= chunkX + START_SEARCH_RADIUS; startChunkX++) {
            for (int startChunkZ = chunkZ - START_SEARCH_RADIUS; startChunkZ <= chunkZ + START_SEARCH_RADIUS; startChunkZ++) {
                if (!PLACEMENT.canGenerate(seed, random, startChunkX, startChunkZ, level.getBiomePicker())) {
                    continue;
                }

                final int originChunkX = startChunkX;
                final int originChunkZ = startChunkZ;
                List<BoundingBox> pieceBounds = context.getGenerator().getStructureBoundsCache().getOrCreate(
                        AncientCityStructure.class,
                        originChunkX,
                        originChunkZ,
                        () -> createPieceBounds(level, originChunkX, originChunkZ)
                );
                if (pieceBounds.size() > 0) {
                    aabbStructures.add(StructureAabbVolumes.DynamicStructure.fromPieces(pieceBounds));
                }

                if (originChunkX == chunkX && originChunkZ == chunkZ) {
                    placeStructure(level, originChunkX, originChunkZ);
                }
            }
        }

        StructureAabbVolumes.replaceDynamic(chunk, STRUCTURE_TYPE, aabbStructures);
    }

    private static List<BoundingBox> createPieceBounds(Level level, int chunkX, int chunkZ) {
        var random = PLACEMENT.createPostSpreadRandom(level.getSeed(), chunkX, chunkZ);
        StructureHelper helper = new StructureHelper(level, new BlockVector3(chunkX << 4, GENERATION_Y, chunkZ << 4));
        return ANCIENT_CITY.collectPieceBounds(helper, random);
    }

    private static void placeStructure(Level level, int chunkX, int chunkZ) {
        var random = PLACEMENT.createPostSpreadRandom(level.getSeed(), chunkX, chunkZ);
        StructureHelper helper = new StructureHelper(level, new BlockVector3(chunkX << 4, GENERATION_Y, chunkZ << 4));
        ANCIENT_CITY.place(helper, random);
    }

    @Override
    public String name() {
        return NAME;
    }
}
