package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeOverworldGenRulesData implements IBiomeDefinitionListObject {

    public BiomeWeightedData[] hillsTransformations;
    public BiomeWeightedData[] mutateTransformations;
    public BiomeWeightedData[] riverTransformations;
    public BiomeWeightedData[] shoreTransformations;
    public BiomeConditionalTransformationData[] preHillsEdgeTransformations;
    public BiomeConditionalTransformationData[] postShoreTransformations;
    public BiomeWeightedTemperatureData[] climateTransformations;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(hillsTransformations, v -> v.encode(byteBuf));
        byteBuf.writeArray(mutateTransformations, v -> v.encode(byteBuf));
        byteBuf.writeArray(riverTransformations, v -> v.encode(byteBuf));
        byteBuf.writeArray(shoreTransformations, v -> v.encode(byteBuf));
        byteBuf.writeArray(preHillsEdgeTransformations, v -> v.encode(byteBuf));
        byteBuf.writeArray(postShoreTransformations, v -> v.encode(byteBuf));
        byteBuf.writeArray(climateTransformations, v -> v.encode(byteBuf));
    }

    @Override
    public void parse(CompoundTag tag) {
        hillsTransformations = tag.getList("hillsTransformations", CompoundTag.class).getAll().stream().map(v -> {
            BiomeWeightedData data = new BiomeWeightedData();
            data.parse(v);
            return data;
        }).toArray(BiomeWeightedData[]::new);
        hillsTransformations = tag.getList("hillsTransformations", CompoundTag.class).getAll().stream().map(v -> {
            BiomeWeightedData data = new BiomeWeightedData();
            data.parse(v);
            return data;
        }).toArray(BiomeWeightedData[]::new);
        mutateTransformations = tag.getList("mutateTransformations", CompoundTag.class).getAll().stream().map(v -> { //Unused?? Key not confirmed!
            BiomeWeightedData data = new BiomeWeightedData();
            data.parse(v);
            return data;
        }).toArray(BiomeWeightedData[]::new);
        shoreTransformations = tag.getList("shoreTransformations", CompoundTag.class).getAll().stream().map(v -> {
            BiomeWeightedData data = new BiomeWeightedData();
            data.parse(v);
            return data;
        }).toArray(BiomeWeightedData[]::new);
        postShoreTransformations = tag.getList("postShoreEdge", CompoundTag.class).getAll().stream().map(v -> {
            BiomeConditionalTransformationData data = new BiomeConditionalTransformationData();
            data.parse(v);
            return data;
        }).toArray(BiomeConditionalTransformationData[]::new);
        preHillsEdgeTransformations = tag.getList("preHillsEdge", CompoundTag.class).getAll().stream().map(v -> {
            BiomeConditionalTransformationData data = new BiomeConditionalTransformationData();
            data.parse(v);
            return data;
        }).toArray(BiomeConditionalTransformationData[]::new);
        climateTransformations = tag.getList("climate", CompoundTag.class).getAll().stream().map(v -> {
            BiomeWeightedTemperatureData data = new BiomeWeightedTemperatureData();
            data.parse(v);
            return data;
        }).toArray(BiomeWeightedTemperatureData[]::new);
    }
}
