package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.PropertySyncData;
import cn.nukkit.utils.Binary;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddPlayerPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.ADD_PLAYER_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public UUID uuid;
    public String username;
    public long entityUniqueId;
    public long entityRuntimeId;
    public String platformChatId = "";
    public float x;
    public float y;
    public float z;
    public float speedX;
    public float speedY;
    public float speedZ;
    public float pitch;
    public float yaw;
    public Item item;
    public int gameType = Server.getInstance().getGamemode();
    public EntityDataMap entityData = new EntityDataMap();


    public PropertySyncData syncedProperties = new PropertySyncData(new int[]{}, new float[]{});
    //public EntityLink links = new EntityLink[0];
    public String deviceId = "";
    public int buildPlatform = -1;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeUUID(this.uuid);
        byteBuf.writeString(this.username);
//        byteBuf.writeEntityUniqueId(this.entityUniqueId);
        byteBuf.writeEntityRuntimeId(this.entityRuntimeId);
        byteBuf.writeString(this.platformChatId);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeVector3f(this.speedX, this.speedY, this.speedZ);
        byteBuf.writeFloatLE(this.pitch);
        byteBuf.writeFloatLE(this.yaw); //TODO headrot
        byteBuf.writeFloatLE(this.yaw);
        byteBuf.writeSlot(this.item);
        byteBuf.writeVarInt(this.gameType);
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
//        byteBuf.writeUnsignedVarInt(0); //TODO: Adventure settings
//        byteBuf.writeUnsignedVarInt(0);
//        byteBuf.writeUnsignedVarInt(0);
//        byteBuf.writeUnsignedVarInt(0);
//        byteBuf.writeUnsignedVarInt(0);
        byteBuf.writeLongLE(entityUniqueId);
        byteBuf.writeUnsignedVarInt(0); // playerPermission
        byteBuf.writeUnsignedVarInt(0); // commandPermission
        byteBuf.writeUnsignedVarInt(1); // abilitiesLayer size
        byteBuf.writeShortLE(1); // BASE layer type
        byteBuf.writeIntLE(262143); // abilitiesSet - all abilities
        byteBuf.writeIntLE(63); // abilityValues - survival abilities
        byteBuf.writeFloatLE(0.1f); // flySpeed
        byteBuf.writeFloatLE(0.05f); // walkSpeed
        byteBuf.writeUnsignedVarInt(0); //TODO: Entity links
        byteBuf.writeString(deviceId);
        byteBuf.writeIntLE(buildPlatform);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
