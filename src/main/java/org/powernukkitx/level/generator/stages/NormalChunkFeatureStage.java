package org.powernukkitx.level.generator.stages;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.GenerateStage;
import org.powernukkitx.level.generator.feature.FeaturePlacementPass;
import org.powernukkitx.level.generator.feature.placement.vegetation.LeafLitterPlacementFeature;
import org.powernukkitx.level.generator.object.GenerationLiquidUpdateAccess;
import org.powernukkitx.level.generator.object.GeneratorRoot;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeConsolidatedFeatureData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionChunkGenData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Slf4j
public class NormalChunkFeatureStage extends GenerateStage {
    public static final String NAME = "feature";
    private static final int PREGENERATION_BIOME_CACHE_LIMIT = 8192;
    private static final Map<IChunk, IntOpenHashSet> PREGENERATION_BIOMES = new WeakHashMap<>();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        final IntOpenHashSet allBiomesInChunk = this.resolveBiomesInChunk(chunk);
        final List<FeaturePlacementPass> placementPasses = this.placementPasses();

        Map<FeaturePlacementPass, ArrayList<BiomeConsolidatedFeatureData>> featuresByPass =
                new EnumMap<>(FeaturePlacementPass.class);
        Map<FeaturePlacementPass, ObjectOpenHashSet<String>> identifiersByPass =
                new EnumMap<>(FeaturePlacementPass.class);

        for (FeaturePlacementPass pass : placementPasses) {
            featuresByPass.put(pass, new ArrayList<>());
            identifiersByPass.put(pass, new ObjectOpenHashSet<>());
        }

        for (int biomeId : allBiomesInChunk) {
            Pair<Short, BiomeDefinitionData> definition = Registries.BIOME.get(biomeId);
            BiomeDefinitionData biome = definition.second();
            BiomeDefinitionChunkGenData chunkGenData = biome.getChunkGenData();
            if (chunkGenData != null) {
                List<BiomeConsolidatedFeatureData> consolidatedFeaturesData = chunkGenData.getConsolidatedFeatures();
                if (consolidatedFeaturesData != null) {
                    for (BiomeConsolidatedFeatureData consolidatedFeatureData : consolidatedFeaturesData) {
                        String passName = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getPass());
                        FeaturePlacementPass pass = FeaturePlacementPass.fromName(passName);
                        ArrayList<BiomeConsolidatedFeatureData> passFeatures = featuresByPass.get(pass);
                        if (passFeatures == null) {
                            continue;
                        }

                        String identifier = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getIdentifier());
                        if (identifiersByPass.get(pass).add(identifier)) {
                            passFeatures.add(consolidatedFeatureData);
                        }
                    }
                }
            } else if (Registries.BIOME.containsTag(BiomeTags.JUNGLE, biome)) {
                log.warn("No chunkGenData for biome {}", definition.first());
            }
        }

        GeneratorRoot root = new GeneratorRoot(chunk.getLevel());
        for (FeaturePlacementPass pass : placementPasses) {
            for (BiomeConsolidatedFeatureData consolidatedFeatureData : featuresByPass.get(pass)) {
                String featureIdentifier = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getIdentifier());
                String featureName = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getFeature());
                String selectedKey = null;
                if (Registries.GENERATE_FEATURE.has(featureIdentifier)) {
                    selectedKey = featureIdentifier;
                } else if (Registries.GENERATE_FEATURE.has(featureName)) {
                    selectedKey = featureName;
                }

                if (selectedKey != null) {
                    try {
                        GenerateFeature feature = Registries.GENERATE_FEATURE.get(selectedKey);
                        feature.setRoot(root);
                        feature.apply(context);
                    } catch (Exception e) {
                        log.error("Error while applying feature {}", selectedKey, e);
                    }
                }
            }
        }

        LeafLitterPlacementFeature.pruneInvalidPlacements(root);

        var generationUpdates = chunk.getGenerationBlockUpdateQueue();
        schedulePerimeterWaterUpdates(chunk, root);

        if (context.getLevel().getGameplaySettings().enableLiquidFlow()) {
            generationUpdates.settleLiquids(new GenerationLiquidUpdateAccess(root, generationUpdates));
        }

        root.applySubChunkUpdate();
        generationUpdates.drainToRuntime(context.getLevel());
    }

    private static void schedulePerimeterWaterUpdates(IChunk chunk, GeneratorRoot root) {
        ChunkSection[] sections = chunk.getSections();
        int minSectionY = chunk.getDimensionData().getMinSectionY();
        boolean[] waterSections = new boolean[sections.length];
        boolean[] queuedPerimeterSections = new boolean[sections.length];

        for (int i = 0; i < sections.length; i++) {
            ChunkSection section = sections[i];
            waterSections[i] = section != null && section.blockLayer()[0].containsIdentifier(BlockID.WATER);
        }

        int[] columnTops = new int[256];
        for (int z = 0; z < 16; z++) {
            columnTops[z] = chunk.getHeightMap(0, z);
            columnTops[(15 << 4) | z] = chunk.getHeightMap(15, z);
        }
        for (int x = 1; x < 15; x++) {
            int index = x << 4;
            columnTops[index] = chunk.getHeightMap(x, 0);
            columnTops[index | 15] = chunk.getHeightMap(x, 15);
        }

        int chunkBaseX = chunk.getX() << 4;
        int chunkBaseZ = chunk.getZ() << 4;
        root.forEachBlock(block -> {
            if (block.layer != 0) {
                return;
            }

            int localX = block.getFloorX() - chunkBaseX;
            int localZ = block.getFloorZ() - chunkBaseZ;
            if (localX < 0 || localX >= 16 || localZ < 0 || localZ >= 16) {
                return;
            }
            if (localX != 0 && localX != 15 && localZ != 0 && localZ != 15) {
                return;
            }

            int sectionIndex = (block.getFloorY() >> 4) - minSectionY;
            if (sectionIndex >= 0 && sectionIndex < sections.length) {
                queuedPerimeterSections[sectionIndex] = true;
                if (BlockID.WATER.equals(block.getId())) {
                    waterSections[sectionIndex] = true;
                }
            }

            if (!block.isAir()) {
                int index = (localX << 4) | localZ;
                columnTops[index] = Math.max(columnTops[index], block.getFloorY() + 1);
            }
        });

        BlockState water = BlockWater.PROPERTIES.getDefaultState();
        for (int z = 0; z < 16; z++) {
            schedulePerimeterWaterColumn(
                    chunk, root, water, sections, waterSections, queuedPerimeterSections, minSectionY, columnTops, 0, z
            );
        }
        for (int x = 1; x < 15; x++) {
            schedulePerimeterWaterColumn(
                    chunk, root, water, sections, waterSections, queuedPerimeterSections, minSectionY, columnTops, x, 0
            );
            schedulePerimeterWaterColumn(
                    chunk, root, water, sections, waterSections, queuedPerimeterSections, minSectionY, columnTops, x, 15
            );
        }
        for (int z = 0; z < 16; z++) {
            schedulePerimeterWaterColumn(
                    chunk, root, water, sections, waterSections, queuedPerimeterSections, minSectionY, columnTops, 15, z
            );
        }
    }

    private static void schedulePerimeterWaterColumn(
            IChunk chunk,
            GeneratorRoot root,
            BlockState water,
            ChunkSection[] sections,
            boolean[] waterSections,
            boolean[] queuedPerimeterSections,
            int minSectionY,
            int[] columnTops,
            int localX,
            int localZ
    ) {
        int minY = root.getMinHeight();
        int top = Math.min(columnTops[(localX << 4) | localZ], root.getMaxHeight());
        if (top <= minY) {
            return;
        }

        int worldX = (chunk.getX() << 4) + localX;
        int worldZ = (chunk.getZ() << 4) + localZ;
        int firstSectionY = minY >> 4;
        int lastSectionY = (top - 1) >> 4;
        boolean outsideWaterRun = true;

        for (int sectionY = firstSectionY; sectionY <= lastSectionY; sectionY++) {
            int sectionIndex = sectionY - minSectionY;
            if (sectionIndex < 0 || sectionIndex >= sections.length || !waterSections[sectionIndex]) {
                outsideWaterRun = true;
                continue;
            }

            int startY = Math.max(minY, sectionY << 4);
            int endY = Math.min(top, (sectionY + 1) << 4);
            ChunkSection section = sections[sectionIndex];
            boolean queuedView = queuedPerimeterSections[sectionIndex];

            for (int y = startY; y < endY; y++) {
                BlockState state = queuedView
                        ? root.getBlockStateIfCachedOrLoaded(worldX, y, worldZ)
                        : section.getBlockState(localX, y & 0x0f, localZ);

                boolean waterBlock = BlockID.WATER.equals(state.getIdentifier());
                if (waterBlock && outsideWaterRun) {
                    chunk.getGenerationBlockUpdateQueue().add(water, worldX, y, worldZ, 1L, 0);
                }
                outsideWaterRun = !waterBlock;
            }
        }
    }

    protected boolean cacheBiomesForPopulation() {
        return false;
    }

    static void cacheGeneratedBiomes(IChunk chunk, IntOpenHashSet biomes) {
        synchronized (PREGENERATION_BIOMES) {
            if (PREGENERATION_BIOMES.size() >= PREGENERATION_BIOME_CACHE_LIMIT && !PREGENERATION_BIOMES.containsKey(chunk)) {
                PREGENERATION_BIOMES.clear();
            }
            PREGENERATION_BIOMES.put(chunk, biomes);
        }
    }

    private IntOpenHashSet resolveBiomesInChunk(IChunk chunk) {
        if (this.cacheBiomesForPopulation()) {
            synchronized (PREGENERATION_BIOMES) {
                IntOpenHashSet cached = PREGENERATION_BIOMES.get(chunk);
                if (cached != null) {
                    return cached;
                }
            }

            IntOpenHashSet biomes = collectBiomesInChunk(chunk, chunk.getLevel().getMinHeight());
            cacheGeneratedBiomes(chunk, biomes);
            return biomes;
        }

        synchronized (PREGENERATION_BIOMES) {
            IntOpenHashSet cached = PREGENERATION_BIOMES.remove(chunk);
            if (cached != null) {
                return cached;
            }
        }
        return collectBiomesInChunk(chunk, chunk.getLevel().getMinHeight());
    }

    protected List<FeaturePlacementPass> placementPasses() {
        return FeaturePlacementPass.regular();
    }

    private static IntOpenHashSet collectBiomesInChunk(IChunk chunk, int minHeight) {
        IntOpenHashSet allBiomesInChunk = new IntOpenHashSet();
        final ChunkSection[] sections = chunk.getSections();
        final int minSectionY = chunk.getDimensionData().getMinSectionY();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = chunk.getHeightMap(x, z) - 1;
                int currentSectionY = Integer.MIN_VALUE;
                ChunkSection currentSection = null;
                int previousBiomeId = 0;
                boolean hasPreviousBiome = false;

                while (y > minHeight) {
                    int sectionY = y >> 4;
                    if (sectionY != currentSectionY) {
                        int sectionIndex = sectionY - minSectionY;
                        currentSection = sectionIndex >= 0 && sectionIndex < sections.length ? sections[sectionIndex] : null;
                        currentSectionY = sectionY;
                    }

                    if (currentSection == null) {
                        y = Math.max(minHeight, (sectionY << 4) - 1);
                        continue;
                    }

                    for (int yInSection = y & 0x0f; yInSection >= 0 && y > minHeight; yInSection--, y--) {
                        int biomeId = currentSection.getBiomeId(x, yInSection, z);
                        if (!hasPreviousBiome || biomeId != previousBiomeId) {
                            allBiomesInChunk.add(biomeId);
                            previousBiomeId = biomeId;
                            hasPreviousBiome = true;
                        }
                    }
                }
            }
        }

        return allBiomesInChunk;
    }

    @Override
    public String name() {
        return NAME;
    }
}
