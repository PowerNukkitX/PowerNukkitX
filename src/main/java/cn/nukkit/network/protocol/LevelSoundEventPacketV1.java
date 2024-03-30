package cn.nukkit.network.protocol;


import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LevelSoundEventPacketV1 extends LevelSoundEventPacket {
    public static final int NETWORK_ID = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1;

    public int sound;
    public float x;
    public float y;
    public float z;
    public int extraData = -1; //TODO: Check name
    public int pitch = 1; //TODO: Check name
    public boolean isBabyMob;
    public boolean isGlobal;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.sound = byteBuf.readByte();
        Vector3f v = byteBuf.readVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.extraData = byteBuf.readVarInt();
        this.pitch = byteBuf.readVarInt();
        this.isBabyMob = byteBuf.readBoolean();
        this.isGlobal = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeByte((byte) this.sound);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeVarInt(this.extraData);
        byteBuf.writeVarInt(this.pitch);
        byteBuf.writeBoolean(this.isBabyMob);
        byteBuf.writeBoolean(this.isGlobal);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
