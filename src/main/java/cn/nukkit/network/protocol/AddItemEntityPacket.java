package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.Binary;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddItemEntityPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.ADD_ITEM_ENTITY_PACKET;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public Item item;
    public float x;
    public float y;
    public float z;
    public float speedX;
    public float speedY;
    public float speedZ;
    public EntityDataMap $2 = new EntityDataMap();
    public boolean $3 = false;

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
        byteBuf.writeSlot(this.item);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeVector3f(this.speedX, this.speedY, this.speedZ);
        byteBuf.writeBytes(Binary.writeEntityData(entityData));
        byteBuf.writeBoolean(this.isFromFishing);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
