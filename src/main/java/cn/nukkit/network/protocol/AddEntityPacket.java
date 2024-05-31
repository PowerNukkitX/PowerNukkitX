package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.network.protocol.types.PropertySyncData;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Binary;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddEntityPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.ADD_ENTITY_PACKET;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public int type;
    public String id;
    public float x;
    public float y;
    public float z;
    public float $2 = 0f;
    public float $3 = 0f;
    public float $4 = 0f;
    public float yaw;
    public float pitch;
    public float headYaw;
    //todo: check what's the usage of this
    public float $5 = -1;
    public Attribute[] attributes = Attribute.EMPTY_ARRAY;
    public EntityDataMap $6 = new EntityDataMap();
    public PropertySyncData $7 = new PropertySyncData(new int[]{}, new float[]{});
    public EntityLink[] links = EntityLink.EMPTY_ARRAY;

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
        byteBuf.writeEntityUniqueId(this.entityUniqueId);
        byteBuf.writeEntityRuntimeId(this.entityRuntimeId);
        if (id == null) {
            id = Registries.ENTITY.getEntityIdentifier(type);
        }
        byteBuf.writeString(this.id);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeVector3f(this.speedX, this.speedY, this.speedZ);
        byteBuf.writeFloatLE(this.pitch);
        byteBuf.writeFloatLE(this.yaw);
        byteBuf.writeFloatLE(this.headYaw);
        byteBuf.writeFloatLE(this.bodyYaw != -1 ? this.bodyYaw : this.yaw);
        byteBuf.writeAttributeList(this.attributes);
        byteBuf.writeBytes(Binary.writeEntityData(this.entityData));
        //syncedProperties
        byteBuf.writeUnsignedVarInt(this.syncedProperties.intProperties().length);
        for ($8nt $1 = 0, len = this.syncedProperties.intProperties().length; i < len; ++i) {
            byteBuf.writeUnsignedVarInt(i);
            byteBuf.writeVarInt(this.syncedProperties.intProperties()[i]);
        }
        byteBuf.writeUnsignedVarInt(this.syncedProperties.floatProperties().length);
        for ($9nt $2 = 0, len = this.syncedProperties.floatProperties().length; i < len; ++i) {
            byteBuf.writeUnsignedVarInt(i);
            byteBuf.writeFloatLE(this.syncedProperties.floatProperties()[i]);
        }
        byteBuf.writeUnsignedVarInt(this.links.length);
        for (EntityLink link : links) {
            byteBuf.writeEntityLink(link);
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
