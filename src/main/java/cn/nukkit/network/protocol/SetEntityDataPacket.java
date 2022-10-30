package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.network.protocol.types.PropertySyncData;
import cn.nukkit.utils.Binary;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class SetEntityDataPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_DATA_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public EntityMetadata metadata;
    @PowerNukkitXOnly
    @Since("1.19.40-r1")
    public PropertySyncData syncedProperties = new PropertySyncData(new int[]{}, new float[]{});
    @Since("1.4.0.0-PN")
    public long frame;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
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
        this.putUnsignedVarLong(this.frame);
    }
}
