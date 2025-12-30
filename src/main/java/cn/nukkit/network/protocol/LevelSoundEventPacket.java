package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import lombok.*;

import static cn.nukkit.utils.Utils.dynamic;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LevelSoundEventPacket extends DataPacket {
    public LevelSoundEvent sound;
    public float x;
    public float y;
    public float z;
    public int extraData = -1;
    public String entityIdentifier;
    public boolean isBabyMob;
    public boolean isGlobal;
    public long entityUniqueId = -1;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.sound = LevelSoundEvent.fromId(byteBuf.readUnsignedVarInt());
        Vector3f v = byteBuf.readVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.extraData = byteBuf.readVarInt();
        this.entityIdentifier = byteBuf.readString();
        this.isBabyMob = byteBuf.readBoolean();
        this.isGlobal = byteBuf.readBoolean();
        this.entityUniqueId = byteBuf.readLongLE();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(this.sound.getId());
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeVarInt(this.extraData);
        byteBuf.writeString(this.entityIdentifier);
        byteBuf.writeBoolean(this.isBabyMob);
        byteBuf.writeBoolean(this.isGlobal);
        byteBuf.writeLongLE(this.entityUniqueId);
    }

    @Override
    public int pid() {
        return ProtocolInfo.LEVEL_SOUND_EVENT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
