package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.PropertySyncData;
import cn.nukkit.utils.Binary;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetEntityDataPacket extends DataPacket {
    public long eid;
    public EntityDataMap entityData;
    public PropertySyncData syncedProperties = new PropertySyncData(new int[]{}, new float[]{});
    public long frame;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarLong(this.eid);
        byteBuf.writeBytes(Binary.writeEntityData(this.entityData));
        byteBuf.writePropertySyncData(syncedProperties);
        byteBuf.writeUnsignedVarLong(this.frame);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_ENTITY_DATA_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
