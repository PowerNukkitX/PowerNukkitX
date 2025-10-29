package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;

import static cn.nukkit.network.protocol.types.biome.IBiomeDefinitionListObject.parseFrom;

public class BiomeDefinitionChunkGenData implements IBiomeDefinitionListObject {

    public OptionalValue<BiomeClimateData> climate = OptionalValue.empty();
    public OptionalValue<BiomeConsolidatedFeatureData[]> consolidatedFeatures = OptionalValue.empty();
    public OptionalValue<BiomeMountainParamsData> mountainParams = OptionalValue.empty();
    public OptionalValue<BiomeSurfaceMaterialAdjustmentData> surfaceMaterialAdjustment = OptionalValue.empty();
    public OptionalValue<BiomeSurfaceMaterialData> surfaceMaterial = OptionalValue.empty();
    public boolean hasDefaultOverworldSurface;
    public boolean hasSwampSurface;
    public boolean hasFrozenOceanSurface;
    public boolean hasTheEndSurface;
    public OptionalValue<BiomeMesaSurfaceData> mesaSurface = OptionalValue.empty();
    public OptionalValue<BiomeCappedSurfaceData> cappedSurface = OptionalValue.empty();
    public OptionalValue<BiomeOverworldGenRulesData> overworldGenRules = OptionalValue.empty();
    public OptionalValue<BiomeMultinoiseGenRulesData> multinoiseGenRules = OptionalValue.empty();
    public OptionalValue<BiomeLegacyWorldGenRulesData> legacyWorldGenRules = OptionalValue.empty();

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeOptional(climate, v -> v.encode(byteBuf));
        byteBuf.writeOptional(consolidatedFeatures, v -> byteBuf.writeArray(v, a -> a.encode(byteBuf)));
        byteBuf.writeOptional(mountainParams, v -> v.encode(byteBuf));
        byteBuf.writeOptional(surfaceMaterialAdjustment, v -> v.encode(byteBuf));
        byteBuf.writeOptional(surfaceMaterial, v -> v.encode(byteBuf));
        byteBuf.writeBoolean(hasDefaultOverworldSurface);
        byteBuf.writeBoolean(hasSwampSurface);
        byteBuf.writeBoolean(hasFrozenOceanSurface);
        byteBuf.writeBoolean(hasTheEndSurface);
        byteBuf.writeOptional(mesaSurface, v -> v.encode(byteBuf));
        byteBuf.writeOptional(cappedSurface, v -> v.encode(byteBuf));
        byteBuf.writeOptional(overworldGenRules, v -> v.encode(byteBuf));
        byteBuf.writeOptional(multinoiseGenRules, v -> v.encode(byteBuf));
        byteBuf.writeOptional(legacyWorldGenRules, v -> v.encode(byteBuf));
        byteBuf.writeBoolean(false);
    }

    @Override
    public void parse(CompoundTag tag) {
        if(tag.containsCompound("climate")) climate = OptionalValue.of(parseFrom(tag.getCompound("climate"), new BiomeClimateData()));
        if(tag.containsCompound("consolidatedFeatures")) consolidatedFeatures = OptionalValue.of(tag.getCompound("consolidatedFeatures").getList("features", CompoundTag.class).getAll().stream().map(v -> parseFrom(v, new BiomeConsolidatedFeatureData())).toArray(BiomeConsolidatedFeatureData[]::new));
        if(tag.containsCompound("mountainParams")) mountainParams = OptionalValue.of(parseFrom(tag.getCompound("mountainParams"), new BiomeMountainParamsData()));
        if(tag.containsCompound("surfaceMaterialAdjustments")) surfaceMaterialAdjustment = OptionalValue.of(parseFrom(tag.getCompound("surfaceMaterialAdjustments"), new BiomeSurfaceMaterialAdjustmentData()));
        if(tag.containsCompound("surfaceMaterials")) surfaceMaterial = OptionalValue.of(parseFrom(tag.getCompound("surfaceMaterials"), new BiomeSurfaceMaterialData()));
        hasSwampSurface = tag.getBoolean("hasSwampSurface");
        hasFrozenOceanSurface = tag.getBoolean("hasFrozenOceanSurface");
        hasTheEndSurface = tag.getBoolean("hasTheEndSurface");
        if(tag.containsCompound("mesaSurface")) mesaSurface = OptionalValue.of(parseFrom(tag.getCompound("mesaSurface"), new BiomeMesaSurfaceData()));
        if(tag.containsCompound("cappedSurface")) cappedSurface = OptionalValue.of(parseFrom(tag.getCompound("cappedSurface"), new BiomeCappedSurfaceData()));
        if(tag.containsCompound("overworldGenRules")) overworldGenRules = OptionalValue.of(parseFrom(tag.getCompound("overworldGenRules"), new BiomeOverworldGenRulesData()));
        if(tag.containsCompound("multinoiseGenRules")) multinoiseGenRules = OptionalValue.of(parseFrom(tag.getCompound("multinoiseGenRules"), new BiomeMultinoiseGenRulesData()));
        if(tag.containsCompound("legacyWorldGenRules")) legacyWorldGenRules = OptionalValue.of(parseFrom(tag.getCompound("legacyWorldGenRules"), new BiomeLegacyWorldGenRulesData()));
    }
}
