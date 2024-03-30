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
    public static final int NETWORK_ID = ProtocolInfo.ADD_ENTITY_PACKET;

    @Override
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
    public float speedX = 0f;
    public float speedY = 0f;
    public float speedZ = 0f;
    public float yaw;
    public float pitch;
    public float headYaw;
    //todo: check what's the usage of this
    public float bodyYaw = -1;
    public Attribute[] attributes = Attribute.EMPTY_ARRAY;
    public EntityDataMap entityData = new EntityDataMap();
    public PropertySyncData syncedProperties = new PropertySyncData(new int[]{}, new float[]{});
    public EntityLink[] links = EntityLink.EMPTY_ARRAY;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
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
        for (int i = 0, len = this.syncedProperties.intProperties().length; i < len; ++i) {
            byteBuf.writeUnsignedVarInt(i);
            byteBuf.writeVarInt(this.syncedProperties.intProperties()[i]);
        }
        byteBuf.writeUnsignedVarInt(this.syncedProperties.floatProperties().length);
        for (int i = 0, len = this.syncedProperties.floatProperties().length; i < len; ++i) {
            byteBuf.writeUnsignedVarInt(i);
            byteBuf.writeFloatLE(this.syncedProperties.floatProperties()[i]);
        }
        byteBuf.writeUnsignedVarInt(this.links.length);
        for (EntityLink link : links) {
            byteBuf.writeEntityLink(link);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
