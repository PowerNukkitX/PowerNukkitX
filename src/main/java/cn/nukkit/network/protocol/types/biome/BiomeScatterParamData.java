package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeScatterParamData implements IBiomeDefinitionListObject {

    public BiomeCoordinateData[] coordinates;
    public int evalOrder;
    public int chancePercentType;
    public int chancePercent;
    public int chanceNumerator;
    public int chanceDenominator;
    public int iterationsType;
    public int iterations;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(coordinates, biomeCoordinateData -> biomeCoordinateData.encode(byteBuf));
        byteBuf.writeVarInt(evalOrder);
        byteBuf.writeVarInt(chancePercentType);
        byteBuf.writeShortLE(chancePercent);
        byteBuf.writeIntLE(chanceNumerator);
        byteBuf.writeIntLE(chanceDenominator);
        byteBuf.writeVarInt(iterationsType);
        byteBuf.writeShortLE(iterations);
    }

    @Override
    public void parse(CompoundTag tag) {
        coordinates = tag.getList("coordinates", CompoundTag.class).getAll().stream().map(v -> {
           BiomeCoordinateData data = new BiomeCoordinateData();
           data.parse(v);
           return data;
        }).toArray(BiomeCoordinateData[]::new);
        evalOrder = tag.getInt("evalOrder");
        chanceDenominator = tag.getInt("changeDenominator"); //Typo in kaooots data?
        chancePercent = tag.getShort("changePercent");
        chancePercentType = tag.getInt("changePercentType");
        iterationsType = tag.getInt("iterationsType");
        chanceNumerator = tag.getInt("changeNumerator");
        iterations = tag.getShort("iterations");
    }
}
