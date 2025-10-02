package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ShortTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;

public class BiomeDefinitionData implements IBiomeDefinitionListObject {

    public short id = -1; //Only used for custom biomes
    public float temperature;
    public float downfall;
    public float foliageSnow = 0;
    public float depth;
    public float scale;
    public int mapWaterColor;
    public boolean rain;
    public OptionalValue<Short[]> tags = OptionalValue.empty();
    public OptionalValue<BiomeDefinitionChunkGenData> chunkGenData = OptionalValue.empty();

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeShort(id);
        byteBuf.writeFloatLE(temperature);
        byteBuf.writeFloatLE(downfall);
        byteBuf.writeFloatLE(foliageSnow);
        byteBuf.writeFloatLE(depth);
        byteBuf.writeFloatLE(scale);
        byteBuf.writeIntLE(mapWaterColor);
        byteBuf.writeBoolean(rain);
        byteBuf.writeOptional(tags, tag -> byteBuf.writeArray(tag, v -> byteBuf.writeShortLE(v)));
        byteBuf.writeOptional(chunkGenData, chunkGenData -> chunkGenData.encode(byteBuf));
    }

    @Override
    public void parse(CompoundTag tag) {
        id = tag.getShort("id");
        temperature = tag.getFloat("temperature");
        downfall = tag.getFloat("downfall");
        depth = tag.getFloat("depth");
        scale = tag.getFloat("scale");
        mapWaterColor = tag.getInt("mapWaterColorARGB");
        rain = tag.getBoolean("rain");
        if(tag.containsCompound("tags")) tags = OptionalValue.of(tag.getCompound("tags").getList("tags", ShortTag.class).getAll().stream().map(v -> v.data).toArray(Short[]::new));
        if(tag.containsCompound("chunkGenData")) chunkGenData = OptionalValue.of(IBiomeDefinitionListObject.parseFrom(tag.getCompound("chunkGenData"), new BiomeDefinitionChunkGenData()));
    }
}
