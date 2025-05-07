package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public interface IBiomeDefinitionListObject {

    void encode(HandleByteBuf byteBuf);

    void parse(CompoundTag tag);

    static <T extends IBiomeDefinitionListObject> T parseFrom(CompoundTag tag, T object) {
        object.parse(tag);
        return object;
    }

}
