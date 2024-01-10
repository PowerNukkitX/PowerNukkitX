package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.network.protocol.types.PropertySyncData;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Binary;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class AddEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADD_ENTITY_PACKET;
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
    public EntityMetadata metadata = new EntityMetadata();
    public PropertySyncData syncedProperties = new PropertySyncData(new int[]{}, new float[]{});
    public EntityLink[] links = EntityLink.EMPTY_ARRAY;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        if (id == null) {
            id =  Registries.ENTITY.getEntityIdentifier(type);
        }
        this.putString(this.id);
        this.putVector3f(this.x, this.y, this.z);
        this.putVector3f(this.speedX, this.speedY, this.speedZ);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putLFloat(this.headYaw);
        this.putLFloat(this.bodyYaw != -1 ? this.bodyYaw : this.yaw);
        this.putAttributeList(this.attributes);
        this.put(Binary.writeMetadata(this.metadata));
        //syncedProperties
        this.putUnsignedVarInt(this.syncedProperties.intProperties().length);
        for (int i = 0, len = this.syncedProperties.intProperties().length; i < len; ++i) {
            this.putUnsignedVarInt(i);
            this.putVarInt(this.syncedProperties.intProperties()[i]);
        }
        this.putUnsignedVarInt(this.syncedProperties.floatProperties().length);
        for (int i = 0, len = this.syncedProperties.floatProperties().length; i < len; ++i) {
            this.putUnsignedVarInt(i);
            this.putLFloat(this.syncedProperties.floatProperties()[i]);
        }
        this.putUnsignedVarInt(this.links.length);
        for (EntityLink link : links) {
            putEntityLink(link);
        }
    }

    public AddEntityPacket() {
    }
}
