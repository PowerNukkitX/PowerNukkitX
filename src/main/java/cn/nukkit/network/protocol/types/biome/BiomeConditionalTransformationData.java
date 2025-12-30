package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeConditionalTransformationData implements IBiomeDefinitionListObject {

    public BiomeWeightedData[] weightedBiomes;
    public int conditionJson;
    public int minPassingNeighbors;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(weightedBiomes, biomeWeightedData -> biomeWeightedData.encode(byteBuf));
        byteBuf.writeShortLE(conditionJson);
        byteBuf.writeIntLE(minPassingNeighbors);
    }

    @Override
    public void parse(CompoundTag tag) {
        weightedBiomes = tag.getList("transformsInto", CompoundTag.class).getAll().stream().map(v -> {
            BiomeWeightedData data = new BiomeWeightedData();
            data.parse(v);
            return data;
        }).toArray(BiomeWeightedData[]::new);
        conditionJson = tag.getShort("conditionJson");
        minPassingNeighbors = tag.getInt("minPassingNeighbors");
    }
}
