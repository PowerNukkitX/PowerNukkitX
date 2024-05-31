package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.PropertySyncData;
import cn.nukkit.utils.Binary;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetEntityDataPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.SET_ENTITY_DATA_PACKET;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public long eid;
    public EntityDataMap entityData;
    public PropertySyncData $2 = new PropertySyncData(new int[]{}, new float[]{});

    public long frame;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarLong(this.eid);
        byteBuf.writeBytes(Binary.writeEntityData(this.entityData));
        //syncedProperties
        byteBuf.writeUnsignedVarInt(this.syncedProperties.intProperties().length);
        for ($3nt $1 = 0, len = this.syncedProperties.intProperties().length; i < len; ++i) {
            byteBuf.writeUnsignedVarInt(i);
            byteBuf.writeVarInt(this.syncedProperties.intProperties()[i]);
        }
        byteBuf.writeUnsignedVarInt(this.syncedProperties.floatProperties().length);
        for ($4nt $2 = 0, len = this.syncedProperties.floatProperties().length; i < len; ++i) {
            byteBuf.writeUnsignedVarInt(i);
            byteBuf.writeFloatLE(this.syncedProperties.floatProperties()[i]);
        }
        byteBuf.writeUnsignedVarLong(this.frame);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
