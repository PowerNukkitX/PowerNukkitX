package cn.nukkit.registry;

import cn.nukkit.utils.RuntimeBlockDefinition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
import org.cloudburstmc.protocol.common.util.Preconditions;
import org.cloudburstmc.protocol.common.util.index.Indexed;
import org.cloudburstmc.protocol.common.util.index.IndexedList;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BiomeRegistry implements IRegistry<Integer, Pair<String, BiomeDefinitionData>, Pair<String, BiomeDefinitionData>> {
    private static final ObjectArrayList<String> BIOME_STRING_LIST = new ObjectArrayList<>();
    private static final Int2ObjectOpenHashMap<Pair<String, BiomeDefinitionData>> DEFINITIONS = new Int2ObjectOpenHashMap<>(0xFF);
    private static final Object2IntOpenHashMap<String> NAME2ID = new Object2IntOpenHashMap<>(0xFF);
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

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
            final BiomeDefinitions definition = this.parseBiomeDefinitions(biomeData);
            Preconditions.checkNotNull(definition.getDefinitions(), "Biome Definitions must not be null");
            for (final Map.Entry<String, BiomeDefinitionData> entry : definition.getDefinitions().entrySet()) {
                final int biomeId = this.getBiomeId(entry.getKey());
                this.register(biomeId, Pair.of(entry.getKey(), entry.getValue()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pair<String, BiomeDefinitionData> get(Integer key) {
        return DEFINITIONS.get(key.intValue());
    }

    public Pair<String, BiomeDefinitionData> get(String biomeName) {
        return get(NAME2ID.getInt(biomeName));
    }

    public String getFromBiomeStringList(int index) {
        return BIOME_STRING_LIST.get(index);
    }

    public int getBiomeId(String biomeName) {
        return NAME2ID.getInt(biomeName.split(":")[1]);
    }

    public BiomeDefinitionListPacket getBiomeDefinitionListPacket() {
        final Map<String, BiomeDefinitionData> map = new HashMap<>();
        for (Pair<String, BiomeDefinitionData> biomeDefinition : this.getBiomeDefinitions()) {
            map.put(biomeDefinition.first(), biomeDefinition.second());
        }
        final BiomeDefinitionListPacket packet = new BiomeDefinitionListPacket();
        packet.setBiomes(new BiomeDefinitions(map));
        return packet;
    }

    @UnmodifiableView
    public Set<Pair<String, BiomeDefinitionData>> getBiomeDefinitions() {
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
        init();
    }

    @Override
    public void register(Integer key, Pair<String, BiomeDefinitionData> value) throws RegisterException {
        int id = key.intValue();
        if (DEFINITIONS.putIfAbsent(id, value) == null) {
            NAME2ID.put(value.first(), id);
        } else {
            throw new RegisterException("This biome " + value.first() + " has already been registered with the id: " + id);
        }
    }

    public int registerToBiomeStringList(String value) {
        BIOME_STRING_LIST.add(value);
        return BIOME_STRING_LIST.size() - 1;
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
            final BiomeDefinitions definition = this.parseBiomeDefinitions(biomeData);
            Preconditions.checkNotNull(definition.getDefinitions(), "Biome Definitions must not be null");
            for (final Map.Entry<String, BiomeDefinitionData> entry : definition.getDefinitions().entrySet()) {
                final int biomeId = this.getBiomeId(entry.getKey());
                try {
                    this.register(biomeId, Pair.of(entry.getKey(), entry.getValue()));
                } catch (RegisterException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private BiomeDefinitions parseBiomeDefinitions(List<NbtMap> nbtMap) {
        final Map<String, BiomeDefinitionData> map = new Object2ObjectOpenHashMap<>();
        for (NbtMap biomeData : nbtMap) {
            map.put(BIOME_STRING_LIST.get(biomeData.getShort("index")), this.readBiomeDefinitionData(biomeData.getCompound("data")));

        }
        return new BiomeDefinitions(map);
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
        final int[] tags = nbtMap.getCompound("tags").getList("tags", NbtType.SHORT).stream()
                .mapToInt(Short::intValue)
                .toArray();
        final BiomeDefinitionChunkGenData chunkGenData = this.readBiomeDefinitionChunkGenData(nbtMap.getCompound("chunkGenData"));
        return new BiomeDefinitionData(
                id == -1 ? null : new Indexed<>(BIOME_STRING_LIST, id),
                temperature,
                downfall,
                0f,
                0f,
                0f,
                0f,
                depth,
                scale,
                new Color(mapWaterColorARGB),
                rain,
                new IndexedList<>(BIOME_STRING_LIST, tags),
                chunkGenData,
                foliageSnow
        );
    }

    private BiomeDefinitionChunkGenData readBiomeDefinitionChunkGenData(NbtMap nbtMap) {
        final BiomeClimateData climate = nbtMap.containsKey("climate") ?
                this.readBiomeClimateData(nbtMap.getCompound("climate")) : null;
        final List<BiomeConsolidatedFeatureData> consolidatedFeatures = nbtMap.containsKey("consolidatedFeatures") ?
                nbtMap.getList("consolidatedFeatures", NbtType.COMPOUND)
                        .stream()
                        .map(this::readBiomeConsolidatedFeatureData)
                        .toList() : null;
        final BiomeMountainParamsData mountainParams = nbtMap.containsKey("mountainParams") ?
                this.readBiomeMountainParamsData(nbtMap.getCompound("mountainParams")) : null;
        final BiomeSurfaceMaterialAdjustmentData surfaceMaterialAdjustment = nbtMap.containsKey("surfaceMaterialAdjustments") ?
                this.readBiomeSurfaceMaterialAdjustmentData(nbtMap.getCompound("surfaceMaterialAdjustments")) : null;
        final BiomeSurfaceBuilderData surfaceBuilderData = this.readBiomeSurfaceBuilderData(nbtMap);
        final BiomeOverworldGenRulesData overworldGenRules = nbtMap.containsKey("overworldGenRules") ?
                this.readBiomeOverworldGenRulesData(nbtMap.getCompound("overworldGenRules")) : null;
        final BiomeMultinoiseGenRulesData multinoiseGenRules = nbtMap.containsKey("multinoiseGenRules") ?
                this.readBiomeMultinoiseGenRulesData(nbtMap.getCompound("multinoiseGenRules")) : null;
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
                new Indexed<>(BIOME_STRING_LIST, feature),
                new Indexed<>(BIOME_STRING_LIST, identifier),
                new Indexed<>(BIOME_STRING_LIST, pass),
                canUseInternalFeature
        );
    }

    private BiomeScatterParamData readBiomeScatterParamData(NbtMap nbtMap) {
        final List<BiomeCoordinateData> coordinates = nbtMap.getList("coordinates", NbtType.COMPOUND).stream()
                .map(this::readBiomeCoordinateData)
                .toList();
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
                new Indexed<>(BIOME_STRING_LIST, chancePercent),
                chanceNumerator,
                chanceDenominator,
                iterationsType,
                new Indexed<>(BIOME_STRING_LIST, iterations)
        );
    }

    private BiomeCoordinateData readBiomeCoordinateData(NbtMap nbtMap) {
        final ExpressionOp minValueType = ExpressionOp.values()[nbtMap.getInt("minValueType")];
        final short minValue = nbtMap.getShort("minValue");
        final ExpressionOp maxValueType = ExpressionOp.values()[nbtMap.getInt("maxValueType")];
        final short maxValue = nbtMap.getShort("maxValue");
        final long gridOffset = nbtMap.getLong("gridOffset");
        final long gridStepSize = nbtMap.getLong("gridStepSize");
        final RandomDistributionType distribution = RandomDistributionType.values()[nbtMap.getInt("distribution")];
        return new BiomeCoordinateData(
                minValueType,
                new Indexed<>(BIOME_STRING_LIST, minValue),
                maxValueType,
                new Indexed<>(BIOME_STRING_LIST, maxValue),
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
                .toList();
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
                new Indexed<>(BIOME_STRING_LIST, heightMin),
                heightMaxType,
                new Indexed<>(BIOME_STRING_LIST, heightMax),
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
                .toList();
        final List<BlockDefinition> ceilingBlocks = nbtMap.getList("ceilingBlocks", NbtType.INT).stream()
                .map(integer -> ((BlockDefinition) new RuntimeBlockDefinition(integer)))
                .toList();
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
                .toList();
        final List<BiomeWeightedData> mutateTransformations = nbtMap.getList("mutateTransformations", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedData)
                .toList();
        final List<BiomeWeightedData> riverTransformations = nbtMap.getList("riverTransformations", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedData)
                .toList();
        final List<BiomeWeightedData> shoreTransformations = nbtMap.getList("shoreTransformations", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedData)
                .toList();
        final List<BiomeConditionalTransformationData> preHillsEdge = nbtMap.getList("preHillsEdge", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeConditionalTransformationData)
                .toList();
        final List<BiomeConditionalTransformationData> postShoreEdge = nbtMap.getList("postShoreEdge", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeConditionalTransformationData)
                .toList();
        final List<BiomeWeightedTemperatureData> climate = nbtMap.getList("climate", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedTemperatureData)
                .toList();
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
                new Indexed<>(BIOME_STRING_LIST, biomeIdentifier),
                weight
        );
    }

    private BiomeConditionalTransformationData readBiomeConditionalTransformationData(NbtMap nbtMap) {
        final List<BiomeWeightedData> transformsInto = nbtMap.getList("transformsInto", NbtType.COMPOUND)
                .stream()
                .map(this::readBiomeWeightedData)
                .toList();
        final short conditionJson = nbtMap.getShort("conditionJson");
        final long minPassingNeighbors = nbtMap.getLong("minPassingNeighbors");
        return new BiomeConditionalTransformationData(
                transformsInto,
                new Indexed<>(BIOME_STRING_LIST, conditionJson),
                minPassingNeighbors
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
                .toList();
        return new BiomeLegacyWorldGenRulesData(
                legacyPreHills
        );
    }

    private BiomeReplacementData readBiomeReplacementData(NbtMap nbtMap) {
        final short replacementBiome = nbtMap.getShort("replacementBiome");
        final short dimension = nbtMap.getShort("dimension");
        final int[] targetBiomes = nbtMap.getList("targetBiomes", NbtType.SHORT)
                .stream()
                .mapToInt(Short::intValue)
                .toArray();
        final float amount = nbtMap.getFloat("amount");
        final float noiseFrequencyScale = nbtMap.getFloat("noiseFrequencyScale");
        final int replacementIndex = nbtMap.getInt("replacementIndex");
        return new BiomeReplacementData(
                new Indexed<>(BIOME_STRING_LIST, replacementBiome),
                new Indexed<>(BIOME_STRING_LIST, dimension),
                new IndexedList<>(BIOME_STRING_LIST, targetBiomes),
                amount,
                noiseFrequencyScale,
                replacementIndex
        );
    }
}