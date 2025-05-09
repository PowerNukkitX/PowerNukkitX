package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeSurfaceMaterialAdjustmentData implements IBiomeDefinitionListObject {

    public BiomeElementData[] biomeElements;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(biomeElements, v -> v.encode(byteBuf));
    }

    @Override
    public void parse(CompoundTag tag) {
        biomeElements = tag.getList("adjustments", CompoundTag.class).getAll().stream().map(v -> {
            BiomeElementData data = new BiomeElementData();
            data.parse(v);
            return data;
        }).toArray(BiomeElementData[]::new);
    }
}
