package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.connection.util.HandleByteBuf;

import java.util.ArrayList;
import java.util.List;

public class SubChunkRequestPacket extends DataPacket {

    public int dimensionType;
    public Vector3 centerPos;
    public List<Vector3> subChunkPosOffset = new ArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.dimensionType = byteBuf.readVarInt();
        this.centerPos = new Vector3(
                byteBuf.readVarInt(),
                byteBuf.readVarInt(),
                byteBuf.readVarInt()
        );
        byteBuf.readArray(subChunkPosOffset, buf -> new Vector3(byteBuf.readByte(), byteBuf.readByte(), byteBuf.readByte()));
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.SUB_CHUNK_REQUEST_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {

    }
}
