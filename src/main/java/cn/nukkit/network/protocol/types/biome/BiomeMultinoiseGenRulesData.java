package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeMultinoiseGenRulesData implements IBiomeDefinitionListObject {

    public float temperature;
    public float humidity;
    public float altitude;
    public float weirdness;
    public float weight;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeFloatLE(temperature);
        byteBuf.writeFloatLE(humidity);
        byteBuf.writeFloatLE(altitude);
        byteBuf.writeFloatLE(weirdness);
        byteBuf.writeFloatLE(weight);
    }

    @Override
    public void parse(CompoundTag tag) {
        altitude = tag.getFloat("altitude");
        weirdness = tag.getFloat("weirdness");
        temperature = tag.getFloat("temperature");
        humidity = tag.getFloat("humidity");
        weight = tag.getFloat("weight");
    }
}
