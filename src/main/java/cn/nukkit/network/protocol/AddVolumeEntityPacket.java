package cn.nukkit.network.protocol;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;
import lombok.ToString;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddVolumeEntityPacket extends DataPacket {
    public int id;
    public CompoundTag data;
    /**
     * @since v465
     */
    public String engineVersion;
    /**
     * @since v485
     */
    public String identifier;
    /**
     * @since v485
     */
    public String instanceName;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        id = byteBuf.readUnsignedVarInt();
        data = byteBuf.readTag();
        engineVersion = byteBuf.readString();
        identifier = byteBuf.readString();
        instanceName = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt((int) Integer.toUnsignedLong(id));
        byteBuf.writeTag(data);
        byteBuf.writeString(engineVersion);
        byteBuf.writeString(identifier);
        byteBuf.writeString(instanceName);
    }

    @Override
    public int pid() {
        return ProtocolInfo.ADD_VOLUME_ENTITY_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
