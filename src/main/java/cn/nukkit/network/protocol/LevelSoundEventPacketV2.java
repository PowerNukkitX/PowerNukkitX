package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class LevelSoundEventPacketV2 extends LevelSoundEventPacket {
    public static final int NETWORK_ID = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2;

    public int sound;
    public float x;
    public float y;
    public float z;
    public int extraData = -1;
    public String entityIdentifier;
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
        this.entityIdentifier = byteBuf.readString();
        this.isBabyMob = byteBuf.readBoolean();
        this.isGlobal = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeByte((byte) this.sound);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeVarInt(this.extraData);
        byteBuf.writeString(this.entityIdentifier);
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
