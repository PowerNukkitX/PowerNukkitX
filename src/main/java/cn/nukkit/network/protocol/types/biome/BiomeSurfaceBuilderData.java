package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;

/**
 * @author Kaooot
 */
public class BiomeSurfaceBuilderData implements IBiomeDefinitionListObject {

    public OptionalValue<BiomeSurfaceMaterialData> surfaceMaterial = OptionalValue.empty();
    public boolean hasDefaultOverworldSurface;
    public boolean hasSwampSurface;
    public boolean hasFrozenOceanSurface;
    public boolean hasTheEndSurface;
    public OptionalValue<BiomeMesaSurfaceData> mesaSurface = OptionalValue.empty();
    public OptionalValue<BiomeCappedSurfaceData> cappedSurface = OptionalValue.empty();

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeOptional(surfaceMaterial, v -> v.encode(byteBuf));
        byteBuf.writeBoolean(hasDefaultOverworldSurface);
        byteBuf.writeBoolean(hasSwampSurface);
        byteBuf.writeBoolean(hasFrozenOceanSurface);
        byteBuf.writeBoolean(hasTheEndSurface);
        byteBuf.writeOptional(mesaSurface, v -> v.encode(byteBuf));
        byteBuf.writeOptional(cappedSurface, v -> v.encode(byteBuf));
        byteBuf.writeBoolean(false);
    }

    @Override
    public void parse(CompoundTag tag) {
        if(tag.containsCompound("surfaceMaterials")) surfaceMaterial = OptionalValue.of(IBiomeDefinitionListObject.parseFrom(tag.getCompound("surfaceMaterials"), new BiomeSurfaceMaterialData()));
        hasSwampSurface = tag.getBoolean("hasSwampSurface");
        hasFrozenOceanSurface = tag.getBoolean("hasFrozenOceanSurface");
        hasTheEndSurface = tag.getBoolean("hasTheEndSurface");
        if(tag.containsCompound("mesaSurface")) mesaSurface = OptionalValue.of(IBiomeDefinitionListObject.parseFrom(tag.getCompound("mesaSurface"), new BiomeMesaSurfaceData()));
        if(tag.containsCompound("cappedSurface")) cappedSurface = OptionalValue.of(IBiomeDefinitionListObject.parseFrom(tag.getCompound("cappedSurface"), new BiomeCappedSurfaceData()));
    }
}