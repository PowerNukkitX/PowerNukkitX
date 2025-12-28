package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.registry.Registries;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @implNote This is not an official protocol object. We added this for easier use on our side.
 */
public class BiomeDefinition implements IBiomeDefinitionListObject {

    public short stringIndex;
    public BiomeDefinitionData data;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeShortLE(stringIndex);
        data.encode(byteBuf);
    }

    public String getName() {
        return Registries.BIOME.getFromBiomeStringList(stringIndex);
    }

    public Set<String> getTags() {
        return Arrays.stream(data.tags.orElse(new Short[0])).map(Registries.BIOME::getFromBiomeStringList).collect(Collectors.toSet());
    }

    public int getId() {
        return Registries.BIOME.getBiomeId(this.getName());
    }

    @Override
    public void parse(CompoundTag tag) {
        stringIndex = tag.getShort("index");
        data = IBiomeDefinitionListObject.parseFrom(tag.getCompound("data"), new BiomeDefinitionData());
    }

}
