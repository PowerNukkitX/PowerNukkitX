package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeClimateData implements IBiomeDefinitionListObject {

    public float temperature;
    public float downfall;
    public float redSporeDensity;
    public float blueSporeDensity;
    public float ashDensity;
    public float whiteAshDensity;
    public float snowAccumulationMin;
    public float snowAccumulationMax;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeFloatLE(temperature);
        byteBuf.writeFloatLE(downfall);
        byteBuf.writeFloatLE(redSporeDensity);
        byteBuf.writeFloatLE(blueSporeDensity);
        byteBuf.writeFloatLE(ashDensity);
        byteBuf.writeFloatLE(whiteAshDensity);
        byteBuf.writeFloatLE(snowAccumulationMin);
        byteBuf.writeFloatLE(snowAccumulationMax);
    }

    @Override
    public void parse(CompoundTag tag) {
        temperature = tag.getFloat("temperature");
        downfall = tag.getFloat("downfall");
        redSporeDensity = tag.getFloat("redSporeDensity");
        blueSporeDensity = tag.getFloat("blueSporeDensity");
        ashDensity = tag.getFloat("ashDensity");
        whiteAshDensity = tag.getFloat("whiteAshDensity");
        snowAccumulationMin = tag.getFloat("snowAccumulationMin");
        snowAccumulationMax = tag.getFloat("snowAccumulationMax");
    }
}
