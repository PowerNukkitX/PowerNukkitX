package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeWeightedData implements IBiomeDefinitionListObject {

    public int biome;
    public int weight;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeShortLE(biome);
        byteBuf.writeIntLE(weight);
    }

    @Override
    public void parse(CompoundTag tag) {
        biome = tag.getShort("biomeIdentifier");
        weight = tag.getInt("weight");
    }
}
