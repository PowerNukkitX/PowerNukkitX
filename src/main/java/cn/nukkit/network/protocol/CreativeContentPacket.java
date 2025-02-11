package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemData;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemGroup;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.NoArgsConstructor;import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor
public class CreativeContentPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CREATIVE_CONTENT_PACKET;

    private final List<CreativeItemGroup> groups = new ObjectArrayList<>();
    private final List<CreativeItemData> contents = new ObjectArrayList<>();

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(Registries.CREATIVE.getCreativeGroups(), this::writeGroup);
        byteBuf.writeArray(Registries.CREATIVE.getCreativeItemData(), this::writeItem);
    }

    private void writeGroup(HandleByteBuf byteBuf, CreativeItemGroup group) {
        byteBuf.writeIntLE(group.getCategory().ordinal());
        byteBuf.writeString(group.getName());
        byteBuf.writeSlot(group.getIcon(), true);
    }

    private void writeItem(HandleByteBuf byteBuf, CreativeItemData data) {
        byteBuf.writeUnsignedVarInt(Registries.CREATIVE.getCreativeItemIndex(data.getItem()));
        byteBuf.writeSlot(data.getItem(), true);
        byteBuf.writeUnsignedVarInt(data.getGroupId());
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
