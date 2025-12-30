package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeLegacyWorldGenRulesData implements IBiomeDefinitionListObject {

    public BiomeConditionalTransformationData[] legacyPreHills;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(legacyPreHills, legacyPreHill -> legacyPreHill.encode(byteBuf));
    }

    @Override
    public void parse(CompoundTag tag) {
        legacyPreHills = tag.getList("legacyPreHillsEdge", CompoundTag.class).getAll().stream().map(v -> {
            BiomeConditionalTransformationData data = new BiomeConditionalTransformationData();
            data.parse(v);
            return data;
        }).toArray(BiomeConditionalTransformationData[]::new);
    }
}
