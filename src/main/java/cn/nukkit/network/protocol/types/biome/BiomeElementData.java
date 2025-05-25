package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeElementData implements IBiomeDefinitionListObject {

    public float noiseFrequencyScale;
    public float noiseLowerBound;
    public float noiseUpperBound;
    public int heightMinType;
    public int heightMin;
    public int heightMaxType;
    public int heightMax;
    public BiomeSurfaceMaterialData adjustedMaterials;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeFloatLE(noiseFrequencyScale);
        byteBuf.writeFloatLE(noiseLowerBound);
        byteBuf.writeFloatLE(noiseUpperBound);
        byteBuf.writeVarInt(heightMinType);
        byteBuf.writeShortLE(heightMin);
        byteBuf.writeVarInt(heightMaxType);
        byteBuf.writeShortLE(heightMax);
        adjustedMaterials.encode(byteBuf);
    }

    @Override
    public void parse(CompoundTag tag) {
        noiseUpperBound = tag.getFloat("noiseUpperBound");
        noiseLowerBound = tag.getFloat("noiseLowerBound");
        heightMinType = tag.getInt("heightMinType");
        heightMax = tag.getInt("heightMax");
        noiseFrequencyScale = tag.getFloat("noiseFreqScale");
        heightMaxType = tag.getInt("heightMaxType");
        heightMin = tag.getShort("heightMin");
        adjustedMaterials = new BiomeSurfaceMaterialData();
        adjustedMaterials.parse(tag.getCompound("adjustedMaterials"));
    }
}
