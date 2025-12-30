package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeWeightedTemperatureData implements IBiomeDefinitionListObject {

    public int temperature;
    public int weight;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(temperature);
        byteBuf.writeIntLE(weight);
    }

    @Override
    public void parse(CompoundTag tag) {
        temperature = tag.getInt("temperature");
        weight = tag.getInt("weight");
    }
}
