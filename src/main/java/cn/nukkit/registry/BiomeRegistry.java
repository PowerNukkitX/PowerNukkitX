package cn.nukkit.registry;

import cn.nukkit.utils.RuntimeBlockDefinition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.CoordinateEvaluationOrder;
import org.cloudburstmc.protocol.bedrock.data.ExpressionOp;
import org.cloudburstmc.protocol.bedrock.data.RandomDistributionType;
import org.cloudburstmc.protocol.bedrock.data.biome.*;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.bedrock.packet.BiomeDefinitionListPacket;
import org.jetbrains.annotations.UnmodifiableView;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BiomeRegistry implements IRegistry<Integer, Pair<Short, BiomeDefinitionData>, Pair<Short, BiomeDefinitionData>> {
    private static final ObjectArrayList<String> BIOME_STRING_LIST = new ObjectArrayList<>();
    private static final Int2ObjectOpenHashMap<Pair<Short, BiomeDefinitionData>> DEFINITIONS = new Int2ObjectOpenHashMap<>(0xFF);
    private static final Object2IntOpenHashMap<String> NAME2ID = new Object2IntOpenHashMap<>(0xFF);
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);
    private static final Map<String, List<String>> BIOME_NAME_TO_TAGS_MAP = new Object2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<List<String>> BIOME_ID_TO_TAGS_MAP = new Int2ObjectOpenHashMap<>();
    private static final List<Pair<Short, BiomeDefinitionData>> LIST = new ObjectArrayList<>();

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/biomes.json");
             var reader = new InputStreamReader(stream)) { //From Endstone Data
            Gson gson = new GsonBuilder().setObjectToNumberStrategy(JsonReader::nextInt).create();
            Map<String, ?> map = gson.fromJson(reader, new TypeToken<Map<String, ?>>() {
            }.getType());
            for (var e : map.entrySet()) {
                Object value = e.getValue();
                if (value instanceof Number number) {
                    NAME2ID.put(e.getKey(), number.intValue());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/biome_definitions.nbt");
             var nbtInputStream = NbtUtils.createGZIPReader(stream)) {
            final NbtMap root = (NbtMap) nbtInputStream.readTag();
            BIOME_STRING_LIST.addAll(root.getList("biomeStringList", NbtType.STRING));
            final List<NbtMap> biomeData = root.getList("biomeData", NbtType.COMPOUND);
            final List<Pair<Short, BiomeDefinitionData>> biomes = this.parseBiomeDefinitions(biomeData);
            for (Pair<Short, BiomeDefinitionData> pair : biomes) {
                LIST.add(pair);
                final String biomeName = BIOME_STRING_LIST.get(pair.key());
                final int biomeId = this.getBiomeId(biomeName);
                this.register(biomeId, Pair.of(pair.key(), pair.value()));

                final List<Short> shortTags = pair.second().getTags();
                final List<String> tags = new ObjectArrayList<>();
                if (shortTags != null) {
                    for (short tag : shortTags) {
                        tags.add(BIOME_STRING_LIST.get(tag));
                    }
                }
                BIOME_NAME_TO_TAGS_MAP.put(biomeName, shortTags == null ? Collections.emptyList() : tags);
                BIOME_ID_TO_TAGS_MAP.put(biomeId, shortTags == null ? Collections.emptyList() : tags);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pair<Short, BiomeDefinitionData> get(Integer key) {
        return DEFINITIONS.get(key.intValue());
    }

    public Pair<Short, BiomeDefinitionData> get(String biomeName) {
        return get(NAME2ID.getInt(biomeName));
    }

    public String getFromBiomeStringList(short index) {
        return BIOME_STRING_LIST.get(index);
    }

    public int getBiomeId(String biomeName) {
        return NAME2ID.getInt(biomeName.split(":")[1]);
    }

    public BiomeDefinitionListPacket getBiomeDefinitionListPacket() {
        final BiomeDefinitionListPacket packet = new BiomeDefinitionListPacket();
        packet.getBiomeStringList().addAll(BIOME_STRING_LIST);
        packet.getBiomes().addAll(LIST);
        return packet;
    }

    @UnmodifiableView
    public Set<Pair<Short, BiomeDefinitionData>> getBiomeDefinitions() {
        return Collections.unmodifiableSet(new HashSet<>(DEFINITIONS.values()));
    }

    @Override
    public void trim() {
        DEFINITIONS.trim();
        NAME2ID.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        DEFINITIONS.clear();
        NAME2ID.clear();
        BIOME_STRING_LIST.clear();
        BIOME_NAME_TO_TAGS_MAP.clear();
        BIOME_ID_TO_TAGS_MAP.clear();
        init();
    }

    @Override
    public void register(Integer key, Pair<Short, BiomeDefinitionData> value) throws RegisterException {
        int id = key.intValue();
        if (DEFINITIONS.putIfAbsent(id, value) == null) {
            NAME2ID.put(BIOME_STRING_LIST.get(value.first()), id);
        } else {
            throw new RegisterException("This biome " + value.first() + " has already been registered with the id: " + id);
        }
    }

    public int registerToBiomeStringList(String value) {
        BIOME_STRING_LIST.add(value);
        return BIOME_STRING_LIST.size() - 1;
    }

    public List<String> getTags(int biomeId) {
        return BIOME_ID_TO_TAGS_MAP.get(biomeId);
    }

    public List<String> getTags(String biomeName) {
        return BIOME_NAME_TO_TAGS_MAP.get(biomeName);
    }

    void writeCache(DataOutputStream out) throws IOException {
        // NAME2ID
        out.writeInt(NAME2ID.size());
        for (var e : NAME2ID.object2IntEntrySet()) {
            out.writeUTF(e.getKey());
            out.writeInt(e.getIntValue());
        }
        // BIOME_STRING_LIST
        out.writeInt(BIOME_STRING_LIST.size());
        for (String s : BIOME_STRING_LIST) out.writeUTF(s);
        // Definitions: re-read and store as uncompressed NBT bytes.
        // This skips Gzip decompression on restore while keeping parse() logic unchanged.
        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/biome_definitions.nbt");
             var nbtInputStream = NbtUtils.createGZIPReader(stream)) {
            final NbtMap root = (NbtMap) nbtInputStream.readTag();
            try (var baos = new ByteArrayOutputStream(); var nbtOutputStream = NbtUtils.createWriter(baos)) {
                nbtOutputStream.writeTag(root);
                final byte[] nbtBytes = baos.toByteArray();
                out.writeInt(nbtBytes.length);
                out.write(nbtBytes);
            }
        }
    }

    void restoreCache(DataInputStream in) throws IOException {
        if (isLoad.getAndSet(true)) return;
        // NAME2ID
        int nameSize = in.readInt();
        for (int i = 0; i < nameSize; i++) {
            NAME2ID.put(in.readUTF(), in.readInt());
        }
        // BIOME_STRING_LIST
        int listSize = in.readInt();
        for (int i = 0; i < listSize; i++) {
            BIOME_STRING_LIST.add(in.readUTF());
        }
        // Definitions from uncompressed NBT bytes (no Gzip decompression needed)
        int nbtLen = in.readInt();
        byte[] nbtBytes = new byte[nbtLen];
        in.readFully(nbtBytes);
        try (var inputStream = new ByteArrayInputStream(nbtBytes); var nbtInputStream = NbtUtils.createReader(inputStream)) {
            final NbtMap root = (NbtMap) nbtInputStream.readTag();
            final List<NbtMap> biomeData = root.getList("biomeData", NbtType.COMPOUND);
            final List<Pair<Short, BiomeDefinitionData>> definition = this.parseBiomeDefinitions(biomeData);
            for (Pair<Short, BiomeDefinitionData> pair : definition) {
                final int biomeId = this.getBiomeId(BIOME_STRING_LIST.get(pair.key()));
                try {
                    this.register(biomeId, Pair.of(pair.key(), pair.value()));
                } catch (RegisterException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private List<Pair<Short, BiomeDefinitionData>> parseBiomeDefinitions(List<NbtMap> nbtMap) {
        final List<Pair<Short, BiomeDefinitionData>> map = new ObjectArrayList<>();
        for (NbtMap biomeData : nbtMap) {
            map.add(Pair.of(biomeData.getShort("index"), this.readBiomeDefinitionData(biomeData.getCompound("data"))));
        }
        return map;
    }

    private BiomeDefinitionData readBiomeDefinitionData(NbtMap nbtMap) {
        final short id = nbtMap.getShort("id");
        final float temperature = nbtMap.getFloat("temperature");
        final float downfall = nbtMap.getFloat("downfall");
        final float foliageSnow = nbtMap.getFloat("foliageSnow");
        final float depth = nbtMap.getFloat("depth");
        final float scale = nbtMap.getFloat("scale");
        final int mapWaterColorARGB = nbtMap.getInt("mapWaterColorARGB");
        final boolean rain = nbtMap.getBoolean("rain");
        final List<Short> tags = nbtMap.getCompound("tags").getList("tags", NbtType.SHORT);
        final BiomeDefinitionChunkGenData chunkGenData = this.readBiomeDefinitionChunkGenData(nbtMap.getCompound("chunkGenData"));
        return new BiomeDefinitionData(
                id,
                temperature,
                downfall,
                0f,
                0f,
                0f,
                0f,
                depth,
                scale,
                new Color(mapWaterColorARGB, true),
                rain,
                tags,
                chunkGenData,
                foliageSnow
        );
    }

    private BiomeDefinitionChunkGenData readBiomeDefinitionChunkGenData(NbtMap nbtMap) {
        final BiomeClimateData climate = nbtMap.containsKey("climate") ?
                this.readBiomeClimateData(nbtMap.getCompound("climate")) : null; // DONE
        final List<BiomeConsolidatedFeatureData> consolidatedFeatures = nbtMap.containsKey("consolidatedFeatures") ?
                nbtMap.getCompound("consolidatedFeatures").getList("features", NbtType.COMPOUND)
                        .stream()
                        .map(this::readBiomeConsolidatedFeatureData)
                        .collect(Collectors.toCollection(LinkedList::new)) : null;
        final BiomeMountainParamsData mountainParams = nbtMap.containsKey("mountainParams") ?
                this.readBiomeMountainParamsData(nbtMap.getCompound("mountainParams")) : null;
        final BiomeSurfaceMaterialAdjustmentData surfaceMaterialAdjustment = nbtMap.containsKey("surfaceMaterialAdjustments") ?
                this.readBiomeSurfaceMaterialAdjustmentData(nbtMap.getCompound("surfaceMaterialAdjustments")) : null;
        final BiomeSurfaceBuilderData surfaceBuilderData = this.readBiomeSurfaceBuilderData(nbtMap); // DONE
        final BiomeOverworldGenRulesData overworldGenRules = nbtMap.containsKey("overworldGenRules") ?
                this.readBiomeOverworldGenRulesData(nbtMap.getCompound("overworldGenRules")) : null; // DONE
        final BiomeMultinoiseGenRulesData multinoiseGenRules = nbtMap.containsKey("multinoiseGenRules") ?
                this.readBiomeMultinoiseGenRulesData(nbtMap.getCompound("multinoiseGenRules")) : null; // DONE
        final BiomeLegacyWorldGenRulesData legacyWorldGenRules = nbtMap.containsKey("legacyWorldGenRules") ?
                this.readBiomeLegacyWorldGenRulesData(nbtMap.getCompound("legacyWorldGenRules")) : null;
        final BiomeReplacementData replacementBiomes = nbtMap.containsKey("replacementBiomes") ?
                this.readBiomeReplacementData(nbtMap.getCompound("replacementBiomes")) : null;
        final VillageType villageType = nbtMap.containsKey("villageType") ?
                VillageType.from(nbtMap.getInt("villageType")) : null;
        return new BiomeDefinitionChunkGenData(
                climate,
                consolidatedFeatures,
                mountainParams,
                surfaceMaterialAdjustment,
                surfaceBuilderData,
                overworldGenRules,
                multinoiseGenRules,
                legacyWorldGenRules,
                replacementBiomes,
                null,
                villageType,
                null
        );
    }

    private BiomeClimateData readBiomeClimateData(NbtMap nbtMap) {
        final float temperature = nbtMap.getFloat("temperature");
        final float downfall = nbtMap.getFloat("downfall");
        final float snowAccumulationMin = nbtMap.getFloat("snowAccumulationMin");
        final float snowAccumulationMax = nbtMap.getFloat("snowAccumulationMax");
        return new BiomeClimateData(
                temperature,
                downfall,
                0f,
                0f,
                0f,
                0f,
                snowAccumulationMin,
                snowAccumulationMax
        );
    }

    private BiomeConsolidatedFeatureData readBiomeConsolidatedFeatureData(NbtMap nbtMap) {
        final BiomeScatterParamData scatter = this.readBiomeScatterParamData(nbtMap.getCompound("scatter"));
        final short feature = nbtMap.getShort("feature");
        final short identifier = nbtMap.getShort("identifier");
        final short pass = nbtMap.getShort("pass");
        final boolean canUseInternalFeature = nbtMap.getBoolean("canUseInternalFeature");
        return new BiomeConsolidatedFeatureData(
                scatter,
                feature,
                identifier,
                pass,
                canUseInternalFeature
        );
    }

    private BiomeScatterParamData readBiomeScatterParamData(NbtMap nbtMap) {
        final List<BiomeCoordinateData> coordinates = nbtMap.getList("coordinates", NbtType.COMPOUND).stream()
                .map(this::readBiomeCoordinateData)
                .collect(Collectors.toCollection(LinkedList::new));
        final CoordinateEvaluationOrder evalOrder = CoordinateEvaluationOrder.values()[nbtMap.getInt("evalOrder")];
        final ExpressionOp chancePercentType = ExpressionOp.values()[nbtMap.getInt("chancePercentType")];
        final short chancePercent = nbtMap.getShort("chancePercent");
        final int chanceNumerator = nbtMap.getInt("chanceNumerator");
        final int chanceDenominator = nbtMap.getInt("chanceDenominator");
        final ExpressionOp iterationsType = ExpressionOp.values()[nbtMap.getInt("iterationsType")];
        final short iterations = nbtMap.getShort("iterations");
        return new BiomeScatterParamData(
                coordinates,
                evalOrder,
                chancePercentType,
                chancePercent,
                chanceNumerator,
                chanceDenominator,
                iterationsType,
                iterations
        );
    }

    private BiomeCoordinateData readBiomeCoordinateData(NbtMap nbtMap) {
        final ExpressionOp minValueType = nbtMap.containsKey("minValueType") ?
                ExpressionOp.values()[nbtMap.getInt("minValueType")] : null;
        final short minValue = nbtMap.getShort("minValue");
        final ExpressionOp maxValueType = nbtMap.containsKey("maxValueType") ?
                ExpressionOp.values()[nbtMap.getInt("maxValueType")] : null;
        final short maxValue = nbtMap.getShort("maxValue");
        final long gridOffset = nbtMap.getLong("gridOffset");
        final long gridStepSize = nbtMap.getLong("gridStepSize");
        final RandomDistributionType distribution = nbtMap.containsKey("distribution") ?
                RandomDistributionType.values()[nbtMap.getInt("distribution")] : null;
        return new BiomeCoordinateData(
                minValueType,
                minValue,
                maxValueType,
                maxValue,
                gridOffset,
                gridStepSize,
                distribution
        );
    }

    private BiomeMountainParamsData readBiomeMountainParamsData(NbtMap nbtMap) {
        final BlockDefinition steepBlock = new RuntimeBlockDefinition(nbtMap.getInt("steepBlock"));
        final boolean northSlopes = nbtMap.getBoolean("northSlopes");
        final boolean southSlopes = nbtMap.getBoolean("southSlopes");
        final boolean westSlopes = nbtMap.getBoolean("westSlopes");
        final boolean eastSlopes = nbtMap.getBoolean("eastSlopes");
        final boolean topSlideEnabled = nbtMap.getBoolean("topSlideEnabled");
        return new BiomeMountainParamsData(
                steepBlock,
                northSlopes,
                southSlopes,
                westSlopes,
                eastSlopes,
                topSlideEnabled
        );
    }

    private BiomeSurfaceMaterialAdjustmentData readBiomeSurfaceMaterialAdjustmentData(NbtMap nbtMap) {
        final List<BiomeElementData> biomeElements = nbtMap.getList("adjustments", NbtType.COMPOUND).stream()
                .map(this::readBiomeElementData)
                .collect(Collectors.toCollection(LinkedList::new));
        return new BiomeSurfaceMaterialAdjustmentData(
                biomeElements
        );
    }

    private BiomeElementData readBiomeElementData(NbtMap nbtMap) {
        final float noiseFrequencyScale = nbtMap.getFloat("noiseFrequencyScale");
        final float noiseLowerBound = nbtMap.getFloat("noiseLowerBound");
        final float noiseUpperBound = nbtMap.getFloat("noiseUpperBound");
        final ExpressionOp heightMinType = ExpressionOp.values()[nbtMap.getInt("heightMinType")];
        final short heightMin = nbtMap.getShort("heightMin");
        final ExpressionOp heightMaxType = ExpressionOp.values()[nbtMap.getInt("heightMaxType")];
        final short heightMax = nbtMap.getShort("heightMax");
        final BiomeSurfaceMaterialData adjustedMaterials = this.readBiomeSurfaceMaterialData(
                nbtMap.getCompound("adjustedMaterials")
        );
        return new BiomeElementData(
                noiseFrequencyScale,
                noiseLowerBound,
                noiseUpperBound,
                heightMinType,
                heightMin,
                heightMaxType,
                heightMax,
                adjustedMaterials
        );
    }

    private BiomeSurfaceMaterialData readBiomeSurfaceMaterialData(NbtMap nbtMap) {
        final BlockDefinition topBlock = new RuntimeBlockDefinition(nbtMap.getInt("topBlock"));
        final BlockDefinition midBlock = new RuntimeBlockDefinition(nbtMap.getInt("midBlock"));
        final BlockDefinition seaFloorBlock = new RuntimeBlockDefinition(nbtMap.getInt("seaFloorBlock"));
        final BlockDefinition foundationBlock = new RuntimeBlockDefinition(nbtMap.getInt("foundationBlock"));
        final BlockDefinition seaBlock = new RuntimeBlockDefinition(nbtMap.getInt("seaBlock"));
        final int seaFloorDepth = nbtMap.getInt("seaFloorDepth");
        return new BiomeSurfaceMaterialData(
                topBlock,
                midBlock,
                seaFloorBlock,
                foundationBlock,
                seaBlock,
                seaFloorDepth
        );
    }

    private BiomeSurfaceBuilderData readBiomeSurfaceBuilderData(NbtMap nbtMap) {
        final BiomeSurfaceMaterialData surfaceMaterial = nbtMap.containsKey("surfaceMaterials") ?
                this.readBiomeSurfaceMaterialData(nbtMap.getCompound("surfaceMaterials")) : null;
        final boolean hasDefaultOverworldSurface = nbtMap.getBoolean("hasDefaultOverworldSurface");
        final boolean hasSwampSurface = nbtMap.getBoolean("hasSwampSurface");
        final boolean hasFrozenOceanSurface = nbtMap.getBoolean("hasFrozenOceanSurface");
        final boolean hasTheEndSurface = nbtMap.getBoolean("hasTheEndSurface");
        final BiomeMesaSurfaceData mesaSurface = nbtMap.containsKey("mesaSurface") ?
                this.readBiomeMesaSurfaceData(nbtMap.getCompound("mesaSurface")) : null;
        final BiomeCappedSurfaceData cappedSurface = nbtMap.containsKey("cappedSurface") ?
                this.readBiomeCappedSurfaceData(nbtMap.getCompound("cappedSurface")) : null;
        return new BiomeSurfaceBuilderData(
                surfaceMaterial,
                hasDefaultOverworldSurface,
                hasSwampSurface,
                hasFrozenOceanSurface,
                hasTheEndSurface,
                mesaSurface,
                cappedSurface,
                null
        );
    }

    private BiomeMesaSurfaceData readBiomeMesaSurfaceData(NbtMap nbtMap) {
        final BlockDefinition clayMaterial = new RuntimeBlockDefinition(nbtMap.getInt("clayMaterial"));
        final BlockDefinition hardClayMaterial = new RuntimeBlockDefinition(nbtMap.getInt("hardClayMaterial"));
        final boolean brycePillars = nbtMap.getBoolean("brycePillars");
        final boolean hasForest = nbtMap.getBoolean("hasForest");
        return new BiomeMesaSurfaceData(
                clayMaterial,
                hardClayMaterial,
                brycePillars,
                hasForest
        );
    }

    private BiomeCappedSurfaceData readBiomeCappedSurfaceData(NbtMap nbtMap) {
        final List<BlockDefinition> floorBlocks = nbtMap.getList("floorBlocks", NbtType.INT).stream()
                .map(integer -> ((BlockDefinition) new RuntimeBlockDefinition(integer)))
                .collect(Collectors.toCollection(LinkedList::new));
        final List<BlockDefinition> ceilingBlocks = nbtMap.getList("ceilingBlocks", NbtType.INT).stream()
                .map(integer -> ((BlockDefinition) new RuntimeBlockDefinition(integer)))
                .collect(Collectors.toCollection(LinkedList::new));
        final BlockDefinition seaBlock = new RuntimeBlockDefinition(nbtMap.getInt("seaBlock"));
        final BlockDefinition foundationBlock = new RuntimeBlockDefinition(nbtMap.getInt("foundationBlock"));
        final BlockDefinition beachBlock = new RuntimeBlockDefinition(nbtMap.getInt("beachBlock"));
        return new BiomeCappedSurfaceData(
                floorBlocks,
                ceilingBlocks,
                seaBlock,
                foundationBlock,
                beachBlock
        );
    }

    private BiomeOverworldGenRulesData readBiomeOverworldGenRulesData(NbtMap nbtMap) {
        final List<BiomeWeightedData> hillsTransformations = nbtMap.getList("hillsTransformations", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedData)
                .collect(Collectors.toCollection(LinkedList::new));
        final List<BiomeWeightedData> mutateTransformations = nbtMap.getList("mutateTransformations", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedData)
                .collect(Collectors.toCollection(LinkedList::new));
        final List<BiomeWeightedData> riverTransformations = nbtMap.getList("riverTransformations", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedData)
                .collect(Collectors.toCollection(LinkedList::new));
        final List<BiomeWeightedData> shoreTransformations = nbtMap.getList("shoreTransformations", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedData)
                .collect(Collectors.toCollection(LinkedList::new));
        final List<BiomeConditionalTransformationData> preHillsEdge = nbtMap.getList("preHillsEdge", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeConditionalTransformationData)
                .collect(Collectors.toCollection(LinkedList::new));
        final List<BiomeConditionalTransformationData> postShoreEdge = nbtMap.getList("postShoreEdge", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeConditionalTransformationData)
                .collect(Collectors.toCollection(LinkedList::new));
        final List<BiomeWeightedTemperatureData> climate = nbtMap.getList("climate", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedTemperatureData)
                .collect(Collectors.toCollection(LinkedList::new));
        return new BiomeOverworldGenRulesData(
                hillsTransformations,
                mutateTransformations,
                riverTransformations,
                shoreTransformations,
                preHillsEdge,
                postShoreEdge,
                climate
        );
    }

    private BiomeWeightedData readBiomeWeightedData(NbtMap nbtMap) {
        final short biomeIdentifier = nbtMap.getShort("biomeIdentifier");
        final int weight = nbtMap.getInt("weight");
        return new BiomeWeightedData(
                biomeIdentifier,
                weight
        );
    }

    private BiomeConditionalTransformationData readBiomeConditionalTransformationData(NbtMap nbtMap) {
        final List<BiomeWeightedData> transformsInto = nbtMap.getList("transformsInto", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedData)
                .collect(Collectors.toCollection(LinkedList::new));
        final short conditionJson = nbtMap.getShort("conditionJson");
        final long minPassingNeighbors = nbtMap.getLong("minPassingNeighbors");
        return new BiomeConditionalTransformationData(
                transformsInto,
                conditionJson,
                (int) minPassingNeighbors
        );
    }

    private BiomeWeightedTemperatureData readBiomeWeightedTemperatureData(NbtMap nbtMap) {
        final BiomeTemperatureCategory temperature = BiomeTemperatureCategory.values()[nbtMap.getInt("temperature")];
        final long weight = nbtMap.getLong("weight");
        return new BiomeWeightedTemperatureData(
                temperature,
                weight
        );
    }

    private BiomeMultinoiseGenRulesData readBiomeMultinoiseGenRulesData(NbtMap nbtMap) {
        final float temperature = nbtMap.getFloat("temperature");
        final float humidity = nbtMap.getFloat("humidity");
        final float altitude = nbtMap.getFloat("altitude");
        final float weirdness = nbtMap.getFloat("weirdness");
        final float weight = nbtMap.getFloat("weight");
        return new BiomeMultinoiseGenRulesData(
                temperature,
                humidity,
                altitude,
                weirdness,
                weight
        );
    }

    private BiomeLegacyWorldGenRulesData readBiomeLegacyWorldGenRulesData(NbtMap nbtMap) {
        final List<BiomeConditionalTransformationData> legacyPreHills = nbtMap.getList("legacyPreHills", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeConditionalTransformationData)
                .collect(Collectors.toCollection(LinkedList::new));
        return new BiomeLegacyWorldGenRulesData(
                legacyPreHills
        );
    }

    private BiomeReplacementData readBiomeReplacementData(NbtMap nbtMap) {
        final short replacementBiome = nbtMap.getShort("replacementBiome");
        final short dimension = nbtMap.getShort("dimension");
        final List<Short> targetBiomes = nbtMap.getList("targetBiomes", NbtType.SHORT);
        final float amount = nbtMap.getFloat("amount");
        final float noiseFrequencyScale = nbtMap.getFloat("noiseFrequencyScale");
        final int replacementIndex = nbtMap.getInt("replacementIndex");
        return new BiomeReplacementData(
                replacementBiome,
                dimension,
                targetBiomes,
                amount,
                noiseFrequencyScale,
                replacementIndex
        );
    }
}