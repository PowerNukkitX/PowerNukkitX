package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.PropertySyncData;
import cn.nukkit.utils.Binary;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class SetEntityDataPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.SET_ENTITY_DATA_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

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
        //syncedProperties
        byteBuf.writeUnsignedVarInt(this.syncedProperties.intProperties().length);
        for (int i = 0, len = this.syncedProperties.intProperties().length; i < len; ++i) {
            byteBuf.writeUnsignedVarInt(i);
            byteBuf.writeVarInt(this.syncedProperties.intProperties()[i]);
        }
        byteBuf.writeUnsignedVarInt(this.syncedProperties.floatProperties().length);
        for (int i = 0, len = this.syncedProperties.floatProperties().length; i < len; ++i) {
            byteBuf.writeUnsignedVarInt(i);
            byteBuf.writeFloatLE(this.syncedProperties.floatProperties()[i]);
        }
        byteBuf.writeUnsignedVarLong(this.frame);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
